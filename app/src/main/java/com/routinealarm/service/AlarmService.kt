package com.routinealarm.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.*
import android.os.*
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.routinealarm.R
import com.routinealarm.ui.alarm.AlarmActivity

class AlarmService : Service() {

    private var ringtone: android.media.Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("AlarmDebug", "AlarmService: onStartCommand called. action=$action")
        
        if (action == AlarmActivity.ACTION_STOP_SOUND) {
            stopAlarmSound()
            return START_NOT_STICKY
        } else if (action == AlarmActivity.ACTION_RESTART_SOUND) {
            startRingtone()
            startVibration()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L) ?: -1L
        val eventName = intent?.getStringExtra(AlarmScheduler.EXTRA_EVENT_NAME) ?: ""
        val triggerTime = intent?.getLongExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, 0L) ?: 0L

        Log.d("AlarmDebug", "AlarmService: Building notification...")
        createNotificationChannel()
        val notification = buildFullScreenNotification(alarmId, eventName, triggerTime)
        
        Log.d("AlarmDebug", "AlarmService: Calling startForeground...")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIF_ID, 
                    notification, 
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIF_ID, notification)
            }
            Log.d("AlarmDebug", "AlarmService: startForeground successful")
        } catch (e: Exception) {
            Log.e("AlarmDebug", "AlarmService: startForeground FAILED: ${e.message}", e)
        }

        Log.d("AlarmDebug", "AlarmService: Starting ringtone and vibration...")
        startRingtone()
        startVibration()

        Log.d("AlarmDebug", "AlarmService: Starting AlarmActivity...")
        // AlarmActivityを起動 (既存フラグを使用)
        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_EVENT_NAME, eventName)
            putExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, triggerTime)
        }
        startActivity(activityIntent)

        return START_NOT_STICKY
    }

    private fun startRingtone() {
        try {
            val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, uri)
            ringtone?.isLooping = true
            ringtone?.play()
            Log.d("AlarmDebug", "AlarmService: Ringtone playing")
        } catch (e: Exception) {
            Log.e("AlarmDebug", "AlarmService: Failed to play ringtone", e)
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 500, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    fun stopAlarmSound() {
        ringtone?.stop()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopAlarmSound()
        super.onDestroy()
    }

    private fun buildFullScreenNotification(alarmId: Long, eventName: String, triggerTime: Long): Notification {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_EVENT_NAME, eventName)
            putExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, triggerTime)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, alarmId.toInt(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(if (eventName.isBlank()) "アラーム" else eventName)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "アラーム", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "アラーム通知チャンネル"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIF_ID = 1001
        const val CHANNEL_ID = "alarm_channel"
    }
}
