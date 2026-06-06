package com.routinealarm.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.data.repository.TodayAlarmRepository
import com.routinealarm.data.repository.WeeklyAlarmRepository
import com.routinealarm.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val todayRepo: TodayAlarmRepository,
    private val weeklyRepo: WeeklyAlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    private val _today = MutableStateFlow(LocalDate.now())
    val today: StateFlow<LocalDate> = _today

    val alarms: StateFlow<List<TodayAlarmEntity>> = _today
        .flatMapLatest { todayRepo.getByDate(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 日付変更処理（手動更新ボタンでも呼べる） */
    fun refresh() {
        viewModelScope.launch {
            val date = LocalDate.now()
            _today.value = date
            val dateStr = date.toString()

            // 古い非「本日のみ」アラームを削除
            todayRepo.deleteOldNonTodayOnly(dateStr)
            // 本日の非「本日のみ」アラームを再生成
            todayRepo.deleteTodayNonOnlyByDate(dateStr)

            val dayOfWeek = date.dayOfWeek.value % 7 + 1 // 1=日, 2=月, ..., 7=土
            Log.d("AlarmDebug", "TodayViewModel: refresh() called. Date=$dateStr, DayOfWeek=$dayOfWeek")
            weeklyRepo.getByDay(dayOfWeek).first()
                .filter { it.isEnabled }
                .forEach { weekly ->
                    Log.d("AlarmDebug", "TodayViewModel: Found enabled weekly alarm: ${weekly.hour}:${weekly.minute}")
                    val newId = todayRepo.add(dateStr, weekly.hour, weekly.minute, weekly.eventName, false)
                    scheduleIfFuture(newId, date, weekly.hour, weekly.minute, weekly.eventName)
                }
        }
    }

    fun toggleEnabled(alarm: TodayAlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            todayRepo.update(updated)
            if (updated.isEnabled) {
                val date = LocalDate.parse(alarm.date)
                scheduleIfFuture(alarm.id, date, alarm.hour, alarm.minute, alarm.eventName)
            } else {
                scheduler.cancel(alarm.id)
            }
        }
    }

    fun addTodayOnly(hour: Int, minute: Int, eventName: String) {
        viewModelScope.launch {
            val date = _today.value
            val id = todayRepo.add(date.toString(), hour, minute, eventName, true)
            scheduleIfFuture(id, date, hour, minute, eventName)
        }
    }

    fun delete(alarm: TodayAlarmEntity) {
        viewModelScope.launch {
            scheduler.cancel(alarm.id)
            todayRepo.delete(alarm)
        }
    }

    fun updateAlarm(alarm: TodayAlarmEntity, hour: Int, minute: Int, eventName: String) {
        viewModelScope.launch {
            val updated = alarm.copy(hour = hour, minute = minute, eventName = eventName, isEnabled = true)
            todayRepo.update(updated)
            val date = LocalDate.parse(updated.date)
            scheduleIfFuture(updated.id, date, updated.hour, updated.minute, updated.eventName)
        }
    }

    private fun scheduleIfFuture(id: Long, date: LocalDate, hour: Int, minute: Int, eventName: String) {
        // 現在時刻と比較してベースとなる日時を決める
        var targetDate = date
        var millis = targetDate.atTime(hour, minute)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        
        // もし計算された時間が過去なら、翌日の同じ時間に設定する
        if (millis <= now) {
            targetDate = LocalDate.now().plusDays(1)
            millis = targetDate.atTime(hour, minute)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            Log.d("AlarmDebug", "TodayViewModel: scheduleIfFuture -> Past detected. Adjusted targetDate to Tomorrow ($targetDate).")
        }
        
        Log.d("AlarmDebug", "TodayViewModel: scheduleIfFuture -> Scheduling for $millis (in ${millis - now} ms).")
        scheduler.schedule(id, millis, eventName)
    }
}
