package com.routinealarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
        val eventName = intent.getStringExtra(AlarmScheduler.EXTRA_EVENT_NAME) ?: ""
        val triggerTime = intent.getLongExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, 0L)

        Log.d("AlarmDebug", "AlarmBroadcastReceiver: onReceive called! id=$alarmId, event=$eventName, time=$triggerTime")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_EVENT_NAME, eventName)
            putExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, triggerTime)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("AlarmDebug", "AlarmBroadcastReceiver: Starting Foreground Service")
                context.startForegroundService(serviceIntent)
            } else {
                Log.d("AlarmDebug", "AlarmBroadcastReceiver: Starting Service (Legacy)")
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("AlarmDebug", "AlarmBroadcastReceiver: Failed to start service: ${e.message}", e)
            e.printStackTrace()
            // フォールバックとしてNotificationだけ出すなどの処理が望ましいですが、
            // まずはクラッシュを防ぐことを優先します
        }
    }
}
