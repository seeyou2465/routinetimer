package com.routinealarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.data.repository.TodayAlarmRepository
import com.routinealarm.data.repository.WeeklyAlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var todayRepo: TodayAlarmRepository
    @Inject lateinit var weeklyRepo: WeeklyAlarmRepository
    @Inject lateinit var scheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                rescheduleAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun toMillis(date: LocalDate, hour: Int, minute: Int): Long {
        return date.atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }

    private suspend fun rescheduleAlarms() {
        val today = LocalDate.now()
        val todayStr = today.toString()
        val alarms = todayRepo.getByDateOnce(todayStr)
        val now = System.currentTimeMillis()
        
        alarms.filter { it.isEnabled }.forEach { alarm ->
            var triggerMillis = toMillis(today, alarm.hour, alarm.minute)
            
            if (triggerMillis <= now) {
                // 過去の時間は明日の同じ時間として再スケジュール
                val tomorrow = today.plusDays(1)
                triggerMillis = toMillis(tomorrow, alarm.hour, alarm.minute)
                android.util.Log.d("AlarmDebug", "BootReceiver: Past detected. Adjusted targetDate to Tomorrow ($tomorrow).")
            }
            
            android.util.Log.d("AlarmDebug", "BootReceiver: Scheduling for $triggerMillis")
            scheduler.schedule(
                alarm.id,
                triggerMillis,
                alarm.eventName,
                alarm.alarmType,
                alarm.timerMinutes
            )
        }
    }
}
