package com.routinealarm.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.data.repository.AlarmSettingsRepository
import com.routinealarm.data.repository.TodayAlarmRepository
import com.routinealarm.data.repository.WeeklyAlarmRepository
import com.routinealarm.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import android.util.Log

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModel @Inject constructor(
    private val todayRepo: TodayAlarmRepository,
    private val weeklyRepo: WeeklyAlarmRepository,
    private val settingsRepo: AlarmSettingsRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    private val _today = MutableStateFlow(LocalDate.now())
    val today: StateFlow<LocalDate> = _today

    val alarms: StateFlow<List<TodayAlarmEntity>> = _today
        .flatMapLatest { todayRepo.getByDate(it.toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val delayMinutes: StateFlow<Int> = settingsRepo.todayDelayMinutesFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            settingsRepo.getTodayDelayMinutes()
        )

    /** 日付変更処理（手動更新ボタンでも呼べる） */
    fun refresh() {
        viewModelScope.launch {
            val date = LocalDate.now()
            _today.value = date
            val dateStr = date.toString()

            // 古い非「本日のみ」アラームを削除
            todayRepo.deleteOldNonTodayOnly(dateStr)

            todayRepo.getByDateOnce(dateStr).forEach { alarm ->
                if (alarm.isTodayOnly) {
                    val restored = alarm.restoredToOriginalTime()
                    if (restored != alarm) {
                        todayRepo.update(restored)
                    }
                    if (restored.isEnabled) {
                        scheduler.cancel(restored.id)
                        scheduleIfFuture(restored.id, date, restored.hour, restored.minute, restored.eventName)
                    }
                } else {
                    scheduler.cancel(alarm.id)
                }
            }

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
            val updated = if (alarm.isEnabled) {
                alarm.restoredToOriginalTime().copy(isEnabled = false)
            } else {
                alarm.restoredToOriginalTime().copy(isEnabled = true)
            }
            todayRepo.update(updated)
            if (updated.isEnabled) {
                val date = LocalDate.parse(updated.date)
                scheduleIfFuture(updated.id, date, updated.hour, updated.minute, updated.eventName)
            } else {
                scheduler.cancel(updated.id)
            }
        }
    }

    fun delayTodayAlarm(alarm: TodayAlarmEntity) {
        if (!alarm.isEnabled) return

        viewModelScope.launch {
            val delayMinutes = settingsRepo.getTodayDelayMinutes()
            val baseHour = alarm.normalizedOriginalHour()
            val baseMinute = alarm.normalizedOriginalMinute()
            val delayedTotalMinutes = (alarm.hour * 60 + alarm.minute + delayMinutes)
                .coerceAtMost(23 * 60 + 59)
            val updated = alarm.copy(
                hour = delayedTotalMinutes / 60,
                minute = delayedTotalMinutes % 60,
                originalHour = baseHour,
                originalMinute = baseMinute,
                isEnabled = true
            )
            todayRepo.update(updated)
            val date = LocalDate.parse(updated.date)
            scheduler.cancel(updated.id)
            scheduleIfFuture(updated.id, date, updated.hour, updated.minute, updated.eventName)
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
            val updated = alarm.copy(
                hour = hour,
                minute = minute,
                eventName = eventName,
                isEnabled = true,
                originalHour = hour,
                originalMinute = minute
            )
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

    private fun TodayAlarmEntity.normalizedOriginalHour(): Int =
        originalHour.takeIf { it in 0..23 } ?: hour

    private fun TodayAlarmEntity.normalizedOriginalMinute(): Int =
        originalMinute.takeIf { it in 0..59 } ?: minute

    private fun TodayAlarmEntity.restoredToOriginalTime(): TodayAlarmEntity =
        copy(hour = normalizedOriginalHour(), minute = normalizedOriginalMinute())
}
