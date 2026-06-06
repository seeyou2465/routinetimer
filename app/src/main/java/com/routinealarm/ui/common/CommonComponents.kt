package com.routinealarm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.data.db.RoutineEntryEntity

// ─── 本日タブのアラームカード ───────────────────────────────────
@Composable
fun AlarmCardToday(
    alarm: TodayAlarmEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onEdit() })
            },
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%02d:%02d".format(alarm.hour, alarm.minute),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        modifier = Modifier.alignByBaseline()
                    )
                    if (alarm.eventName.isNotBlank()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = alarm.eventName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            color = if (alarm.isEnabled) MaterialTheme.colorScheme.secondary else Color.Gray,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }
                if (alarm.isTodayOnly) {
                    Text("本日のみ", fontSize = 11.sp, color = Color(0xFFF57C00))
                }
            }
            Switch(checked = alarm.isEnabled, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color(0xFFEF5350))
            }
        }
    }
}

// ─── 曜日タブのアラームカード ────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCardWeekly(
    alarm: WeeklyAlarmEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                onToggle()
            }
            false // カードは消さない
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (alarm.isEnabled) "  無効にする" else "  有効にする",
                    color = if (alarm.isEnabled) Color(0xFFE64A19) else Color(0xFF388E3C),
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 14.sp
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { onEdit() })
                },
            colors = CardDefaults.cardColors(
                containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%02d:%02d".format(alarm.hour, alarm.minute),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            modifier = Modifier.alignByBaseline()
                        )
                        if (alarm.eventName.isNotBlank()) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = alarm.eventName,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = if (alarm.isEnabled) MaterialTheme.colorScheme.secondary else Color.Gray,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                    if (alarm.isFromRoutine) {
                        Text("[全体]", fontSize = 11.sp, color = Color(0xFF0097A7))
                    }
                }
                if (!alarm.isFromRoutine) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color(0xFFEF5350))
                    }
                }
            }
        }
    }
}

// ─── 設定タブのルーティンカード ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineCard(
    entry: RoutineEntryEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFEF5350)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { onEdit() })
                },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%02d:%02d".format(entry.hour, entry.minute),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.alignByBaseline()
                        )
                        if (entry.eventName.isNotBlank()) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = entry.eventName,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── 時刻・イベント名入力ダイアログ ─────────────────────────────
@Composable
fun TimePickerDialog(
    title: String,
    initialHour: Int = 7,
    initialMinute: Int = 0,
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, name: String) -> Unit
) {
    var hourStr by remember { mutableStateOf(initialHour.toString().padStart(2, '0')) }
    var minuteStr by remember { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var name by remember { mutableStateOf(initialName) }

    Dialog(onDismissRequest = onDismiss) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = { if (it.length <= 2) hourStr = it },
                        label = { Text("時") },
                        modifier = Modifier.width(72.dp),
                        singleLine = true
                    )
                    Text(":", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 8.dp))
                    OutlinedTextField(
                        value = minuteStr,
                        onValueChange = { if (it.length <= 2) minuteStr = it },
                        label = { Text("分") },
                        modifier = Modifier.width(72.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("イベント名（任意）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("キャンセル") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val h = hourStr.toIntOrNull()?.coerceIn(0, 23) ?: initialHour
                        val m = minuteStr.toIntOrNull()?.coerceIn(0, 59) ?: initialMinute
                        onConfirm(h, m, name.trim())
                    }) { Text("OK") }
                }
            }
        }
    }
}
