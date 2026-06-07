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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.routinealarm.service.AlarmScheduler
import com.routinealarm.service.AlarmService
import com.routinealarm.data.db.ALARM_TYPE_ALARM
import com.routinealarm.data.db.ALARM_TYPE_TIMER
import com.routinealarm.data.db.DEFAULT_TIMER_MINUTES
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
        val alarmType = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TYPE) ?: ALARM_TYPE_ALARM
        val timerMinutes = intent.getIntExtra(AlarmScheduler.EXTRA_TIMER_MINUTES, DEFAULT_TIMER_MINUTES)

        // スヌーズ期限で音を再開する
        viewModel.onSnoozeExpired = {
            if (alarmType != ALARM_TYPE_TIMER) {
                restartAlarmSound(alarmId, eventName, triggerTime)
            }
        }
        viewModel.onTimerFinished = {
            restartAlarmSound(alarmId, eventName, triggerTime)
        }

        if (alarmType == ALARM_TYPE_TIMER) {
            viewModel.startTimer(timerMinutes.coerceAtLeast(1) * 60)
        }

        setContent {
            RoutineAlarmTheme {
                AlarmScreen(
                    eventName = eventName,
                    triggerTime = triggerTime,
                    alarmType = alarmType,
                    viewModel = viewModel,
                    onStop = {
                        if (alarmType == ALARM_TYPE_TIMER) {
                            if ((viewModel.timerRemainingSeconds.value ?: 0) > 0) {
                                viewModel.pauseTimer()
                            } else {
                                stopAlarm(finish = false)
                            }
                        } else {
                            stopAlarm(finish = false)
                        }
                    },
                    onResumeTimer = { viewModel.resumeTimer() },
                    onDone = { stopAlarm(finish = true) },
                    onSnooze = { minutes ->
                        if (alarmType == ALARM_TYPE_TIMER) {
                            if ((viewModel.timerRemainingSeconds.value ?: 0) <= 0) {
                                stopAlarmSound()
                                viewModel.startTimer(minutes * 60)
                            } else {
                                viewModel.addTimerMinutes(minutes)
                            }
                        } else {
                            stopAlarmSound()
                            viewModel.startSnooze(minutes * 60)
                        }
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
            putExtra(AlarmScheduler.EXTRA_ALARM_TYPE, ALARM_TYPE_ALARM)
            putExtra(AlarmScheduler.EXTRA_TIMER_MINUTES, DEFAULT_TIMER_MINUTES)
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

private fun alarmScreenEventNameFontSize(name: String): TextUnit = when {
    name.length <= 4 -> 72.sp
    name.length <= 8 -> 64.sp
    name.length <= 12 -> 56.sp
    name.length <= 16 -> 48.sp
    name.length <= 24 -> 40.sp
    else -> 34.sp
}

private fun timerScreenEventNameFontSize(name: String): TextUnit = when {
    name.length <= 4 -> 76.sp
    name.length <= 8 -> 68.sp
    name.length <= 12 -> 60.sp
    name.length <= 16 -> 52.sp
    name.length <= 24 -> 44.sp
    else -> 36.sp
}

@Composable
fun AlarmScreen(
    eventName: String,
    triggerTime: Long,
    alarmType: String,
    viewModel: AlarmViewModel,
    onStop: () -> Unit,
    onResumeTimer: () -> Unit,
    onDone: () -> Unit,
    onSnooze: (Int) -> Unit
) {
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val isSnoozing by viewModel.isSnoozing.collectAsState()
    val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
    val isTimerPaused by viewModel.isTimerPaused.collectAsState()
    val isTimerFinished = alarmType == ALARM_TYPE_TIMER && (timerRemainingSeconds ?: 0) <= 0

    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000L)
        }
    }

    val timeStr = remember(currentTimeMillis) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentTimeMillis))
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
                fontSize = if (alarmType == ALARM_TYPE_TIMER) 42.sp else 72.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                modifier = Modifier.padding(bottom = if (alarmType == ALARM_TYPE_TIMER) 14.dp else 8.dp)
            )

            if (alarmType == ALARM_TYPE_TIMER) {
                val remaining = timerRemainingSeconds ?: 0
                val mins = remaining / 60
                val secs = remaining % 60
                Text(
                    text = "%02d:%02d".format(mins, secs),
                    fontSize = 78.sp,
                    color = Color(0xFFFFE082),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // イベント名
            if (eventName.isNotBlank()) {
                Text(
                    text = eventName,
                    fontSize = if (alarmType == ALARM_TYPE_TIMER) {
                        timerScreenEventNameFontSize(eventName)
                    } else {
                        alarmScreenEventNameFontSize(eventName)
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBBDEFB),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (alarmType == ALARM_TYPE_TIMER) 8.dp else 24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(if (alarmType == ALARM_TYPE_TIMER) 8.dp else 24.dp))
            }

            if (alarmType == ALARM_TYPE_TIMER) {
                Text(
                    text = when {
                        isTimerFinished -> "タイマー完了"
                        isTimerPaused -> "停止中"
                        else -> "タイマー実行中"
                    },
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.72f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // スヌーズカウントダウン
            if (alarmType != ALARM_TYPE_TIMER && isSnoozing && countdownSeconds != null) {
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
                    onClick = {
                        if (alarmType == ALARM_TYPE_TIMER && isTimerPaused && !isTimerFinished) {
                            onResumeTimer()
                        } else {
                            onStop()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alarmType == ALARM_TYPE_TIMER && isTimerPaused && !isTimerFinished) {
                            Color(0xFF43A047)
                        } else {
                            Color(0xFF616161)
                        }
                    )
                ) {
                    Text(
                        if (alarmType == ALARM_TYPE_TIMER && isTimerPaused && !isTimerFinished) "再開" else "停止",
                        fontSize = 18.sp,
                        color = Color.White
                    )
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
