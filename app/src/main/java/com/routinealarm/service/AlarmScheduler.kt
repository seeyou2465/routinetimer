package com.routinealarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarmId: Long, triggerAtMillis: Long, eventName: String) {
        Log.d("AlarmDebug", "AlarmScheduler: schedule requested. id=$alarmId, time=$triggerAtMillis, event=$eventName")
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_EVENT_NAME, eventName)
            putExtra(EXTRA_TRIGGER_TIME, triggerAtMillis)
        }
        val pi = PendingIntent.getBroadcast(
            context, alarmId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                Log.d("AlarmDebug", "AlarmScheduler: Using setExactAndAllowWhileIdle (Android 12+)")
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
            } else {
                Log.e("AlarmDebug", "AlarmScheduler: Exact alarm permission missing! Using setAndAllowWhileIdle fallback.")
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
            }
        } else {
            Log.d("AlarmDebug", "AlarmScheduler: Using setExactAndAllowWhileIdle (Legacy)")
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }
        Log.d("AlarmDebug", "AlarmScheduler: Scheduling complete.")
    }

    fun cancel(alarmId: Long) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, alarmId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }

    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_EVENT_NAME = "event_name"
        const val EXTRA_TRIGGER_TIME = "trigger_time"
    }
}
