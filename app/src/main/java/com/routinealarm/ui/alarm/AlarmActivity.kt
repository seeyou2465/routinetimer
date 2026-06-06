package com.routinealarm.ui.alarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.routinealarm.service.AlarmScheduler
import com.routinealarm.service.AlarmService
import com.routinealarm.ui.theme.RoutineAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private val viewModel: AlarmViewModel by viewModels()

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ロック画面上で全画面表示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
        val eventName = intent.getStringExtra(AlarmScheduler.EXTRA_EVENT_NAME) ?: ""
        val triggerTime = intent.getLongExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, 0L)

        // スヌーズ期限で音を再開する
        viewModel.onSnoozeExpired = {
            restartAlarmSound(alarmId, eventName, triggerTime)
        }

        setContent {
            RoutineAlarmTheme {
                AlarmScreen(
                    eventName = eventName,
                    triggerTime = triggerTime,
                    viewModel = viewModel,
                    onStop = { stopAlarm(finish = false) },
                    onDone = { stopAlarm(finish = true) },
                    onSnooze = { minutes ->
                        stopAlarmSound()
                        viewModel.startSnooze(minutes * 60)
                    }
                )
            }
        }
    }

    private fun stopAlarm(finish: Boolean) {
        viewModel.cancelSnooze()
        stopAlarmService()
        alarmScheduler.cancel(intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L))
        if (finish) this.finish()
    }

    private fun stopAlarmSound() {
        // AlarmServiceに音停止を要求（IntentでActionを送る）
        val intent = Intent(this, AlarmService::class.java)
        intent.action = ACTION_STOP_SOUND
        startService(intent)
    }

    private fun stopAlarmService() {
        stopService(Intent(this, AlarmService::class.java))
    }

    private fun restartAlarmSound(alarmId: Long, eventName: String, triggerTime: Long) {
        val serviceIntent = Intent(this, AlarmService::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_EVENT_NAME, eventName)
            putExtra(AlarmScheduler.EXTRA_TRIGGER_TIME, triggerTime)
            action = ACTION_RESTART_SOUND
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    companion object {
        const val ACTION_STOP_SOUND = "com.routinealarm.STOP_SOUND"
        const val ACTION_RESTART_SOUND = "com.routinealarm.RESTART_SOUND"
    }
}

@Composable
fun AlarmScreen(
    eventName: String,
    triggerTime: Long,
    viewModel: AlarmViewModel,
    onStop: () -> Unit,
    onDone: () -> Unit,
    onSnooze: (Int) -> Unit
) {
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val isSnoozing by viewModel.isSnoozing.collectAsState()

    val timeStr = remember(triggerTime) {
        if (triggerTime == 0L) "" else
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(triggerTime))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // アラーム時刻
            Text(
                text = timeStr,
                fontSize = 72.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // イベント名
            if (eventName.isNotBlank()) {
                Text(
                    text = eventName,
                    fontSize = 24.sp,
                    color = Color(0xFFBBDEFB),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // スヌーズカウントダウン
            if (isSnoozing && countdownSeconds != null) {
                val mins = countdownSeconds!! / 60
                val secs = countdownSeconds!! % 60
                Text(
                    text = "スヌーズ %02d:%02d".format(mins, secs),
                    fontSize = 32.sp,
                    color = Color(0xFFFFF176),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }

            // ＋1分 / ＋5分
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                OutlinedButton(
                    onClick = { onSnooze(1) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFF176))
                ) {
                    Text("＋1分", fontSize = 18.sp)
                }
                OutlinedButton(
                    onClick = { onSnooze(5) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFF176))
                ) {
                    Text("＋5分", fontSize = 18.sp)
                }
            }

            // 停止 / 完了
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onStop,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161))
                ) {
                    Text("停止", fontSize = 18.sp, color = Color.White)
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
                ) {
                    Text("完了", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}
