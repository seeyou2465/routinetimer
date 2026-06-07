package com.routinealarm.ui.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.data.repository.WeeklyAlarmRepository
import com.routinealarm.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val repo: WeeklyAlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    val allAlarms: StateFlow<List<WeeklyAlarmEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getAlarmsForDay(dayOfWeek: Int) =
        allAlarms.value.filter { it.dayOfWeek == dayOfWeek }
            .sortedWith(compareBy({ it.hour }, { it.minute }))

    fun toggleEnabled(alarm: WeeklyAlarmEntity) {
        viewModelScope.launch {
            repo.update(alarm.copy(isEnabled = !alarm.isEnabled))
        }
    }

    fun addAlarm(dayOfWeek: Int, hour: Int, minute: Int, eventName: String, alarmType: String, timerMinutes: Int) {
        viewModelScope.launch {
            repo.add(dayOfWeek, hour, minute, eventName, isFromRoutine = false, alarmType, timerMinutes)
        }
    }

    fun updateAlarm(
        alarm: WeeklyAlarmEntity,
        hour: Int,
        minute: Int,
        eventName: String,
        alarmType: String,
        timerMinutes: Int
    ) {
        viewModelScope.launch {
            repo.update(
                alarm.copy(
                    hour = hour,
                    minute = minute,
                    eventName = eventName,
                    alarmType = alarmType,
                    timerMinutes = timerMinutes
                )
            )
        }
    }

    fun deleteAlarm(alarm: WeeklyAlarmEntity) {
        viewModelScope.launch {
            repo.delete(alarm)
        }
    }
}
