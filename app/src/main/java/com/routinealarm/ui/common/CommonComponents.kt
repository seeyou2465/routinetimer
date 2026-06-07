package com.routinealarm.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.data.db.RoutineEntryEntity
import com.routinealarm.data.db.ALARM_TYPE_ALARM
import com.routinealarm.data.db.ALARM_TYPE_TIMER
import com.routinealarm.data.db.DEFAULT_TIMER_MINUTES
import kotlin.math.abs
import kotlin.math.roundToInt

private fun alarmNameFontSize(name: String): TextUnit = when {
    name.length <= 4 -> 32.sp
    name.length <= 8 -> 28.sp
    name.length <= 12 -> 24.sp
    name.length <= 16 -> 21.sp
    else -> 18.sp
}

// ─── 本日タブのアラームカード ───────────────────────────────────
@Composable
fun AlarmCardToday(
    alarm: TodayAlarmEntity,
    delayMinutes: Int,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onDelay: () -> Unit,
    onEdit: () -> Unit
) {
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 44.dp.toPx() }
    val maxSwipeOffset = with(density) { 128.dp.toPx() }
    var swipeOffset by remember(alarm.id) { mutableFloatStateOf(0f) }
    val swipeProgress = (-swipeOffset / swipeThreshold).coerceIn(0f, 1f)
    val delaySwipeProgress = (swipeOffset / swipeThreshold).coerceIn(0f, 1f)
    val currentOnToggle by rememberUpdatedState(onToggle)
    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentOnDelay by rememberUpdatedState(onDelay)
    val currentOnEdit by rememberUpdatedState(onEdit)

    LaunchedEffect(alarm.isEnabled) {
        swipeOffset = 0f
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        val leftActionColor = when {
            alarm.isTodayOnly -> MaterialTheme.colorScheme.error
            alarm.isEnabled -> Color(0xFFE64A19)
            else -> Color(0xFF388E3C)
        }
        val leftActionText = when {
            alarm.isTodayOnly -> "削除  "
            alarm.isEnabled -> "無効にする  "
            else -> "有効にする  "
        }
        val rightActionColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(leftActionColor.copy(alpha = 0.12f * swipeProgress)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = leftActionText,
                color = leftActionColor.copy(alpha = swipeProgress),
                modifier = Modifier.padding(end = 16.dp),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(rightActionColor.copy(alpha = 0.14f * delaySwipeProgress)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "+${delayMinutes}分  ",
                color = rightActionColor.copy(alpha = delaySwipeProgress),
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(swipeOffset.roundToInt(), 0) }
                .fillMaxWidth()
                .pointerInput(alarm.id) {
                    awaitEachGesture {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial
                        )
                        var pointerId = down.id
                        var totalDragX = 0f
                        var totalDragY = 0f
                        var isHorizontalDrag = false

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.firstOrNull()
                                ?: break

                            pointerId = change.id
                            if (change.changedToUpIgnoreConsumed()) break

                            val dragAmount = change.positionChange()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y

                            if (!isHorizontalDrag) {
                                isHorizontalDrag =
                                    abs(totalDragX) > viewConfiguration.touchSlop &&
                                    abs(totalDragX) > abs(totalDragY)
                            }

                            if (isHorizontalDrag) {
                                change.consume()
                                swipeOffset = (swipeOffset + dragAmount.x)
                                    .coerceIn(-maxSwipeOffset, maxSwipeOffset)
                            }
                        }

                        if (swipeOffset <= -swipeThreshold) {
                            if (alarm.isTodayOnly) {
                                currentOnDelete()
                            } else {
                                currentOnToggle()
                            }
                        } else if (swipeOffset >= swipeThreshold && alarm.isEnabled) {
                            currentOnDelay()
                        }
                        swipeOffset = 0f
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { currentOnEdit() },
                        onLongPress = { currentOnEdit() },
                        onDoubleTap = { currentOnEdit() }
                    )
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
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
                                fontSize = alarmNameFontSize(alarm.eventName),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = if (alarm.isEnabled) MaterialTheme.colorScheme.secondary else Color.Gray,
                                modifier = Modifier.alignByBaseline(),
                                maxLines = 1
                            )
                        }
                    }
                    if (alarm.isTodayOnly) {
                        Text("本日のみ", fontSize = 11.sp, color = Color(0xFFF57C00))
                    }
                    if (alarm.alarmType == ALARM_TYPE_TIMER) {
                        Text("タイマー ${alarm.timerMinutes}分", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Switch(checked = alarm.isEnabled, onCheckedChange = { currentOnToggle() })
                if (alarm.isTodayOnly) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { currentOnDelete() }) {
                        Icon(Icons.Default.Delete, contentDescription = "削除", tint = Color(0xFFEF5350))
                    }
                }
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
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 44.dp.toPx() }
    val maxSwipeOffset = with(density) { 128.dp.toPx() }
    var swipeOffset by remember(alarm.id) { mutableFloatStateOf(0f) }
    val swipeProgress = (-swipeOffset / swipeThreshold).coerceIn(0f, 1f)
    val currentOnToggle by rememberUpdatedState(onToggle)
    val currentOnEdit by rememberUpdatedState(onEdit)

    LaunchedEffect(alarm.isEnabled) {
        swipeOffset = 0f
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        val actionColor = if (alarm.isEnabled) Color(0xFFE64A19) else Color(0xFF388E3C)

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(actionColor.copy(alpha = 0.12f * swipeProgress)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = if (alarm.isEnabled) "無効にする  " else "有効にする  ",
                color = actionColor.copy(alpha = swipeProgress),
                modifier = Modifier.padding(end = 16.dp),
                fontSize = 14.sp
            )
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(swipeOffset.roundToInt(), 0) }
                .fillMaxWidth()
                .pointerInput(alarm.id) {
                    awaitEachGesture {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial
                        )
                        var pointerId = down.id
                        var totalDragX = 0f
                        var totalDragY = 0f
                        var isHorizontalDrag = false

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == pointerId }
                                ?: event.changes.firstOrNull()
                                ?: break

                            pointerId = change.id
                            if (change.changedToUpIgnoreConsumed()) break

                            val dragAmount = change.positionChange()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y

                            if (!isHorizontalDrag) {
                                isHorizontalDrag =
                                    abs(totalDragX) > viewConfiguration.touchSlop &&
                                    abs(totalDragX) > abs(totalDragY)
                            }

                            if (isHorizontalDrag) {
                                change.consume()
                                swipeOffset = (swipeOffset + dragAmount.x)
                                    .coerceIn(-maxSwipeOffset, 0f)
                            }
                        }

                        if (swipeOffset <= -swipeThreshold) {
                            currentOnToggle()
                        }
                        swipeOffset = 0f
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { currentOnEdit() },
                        onLongPress = { currentOnEdit() },
                        onDoubleTap = { currentOnEdit() }
                    )
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
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
                                fontSize = alarmNameFontSize(alarm.eventName),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = if (alarm.isEnabled) MaterialTheme.colorScheme.secondary else Color.Gray,
                                modifier = Modifier.alignByBaseline(),
                                maxLines = 1
                            )
                        }
                    }
                    if (alarm.isFromRoutine) {
                        Text("[全体]", fontSize = 11.sp, color = Color(0xFF0097A7))
                    }
                    if (alarm.alarmType == ALARM_TYPE_TIMER) {
                        Text("タイマー ${alarm.timerMinutes}分", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
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
            val deleteBackgroundAlpha =
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    dismissState.progress.coerceIn(0f, 1f)
                } else {
                    0f
                }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error.copy(alpha = deleteBackgroundAlpha)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError.copy(alpha = deleteBackgroundAlpha),
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onEdit() },
                        onLongPress = { onEdit() },
                        onDoubleTap = { onEdit() }
                    )
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                                fontSize = alarmNameFontSize(entry.eventName),
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.alignByBaseline(),
                                maxLines = 1
                            )
                        }
                    }
                    if (entry.alarmType == ALARM_TYPE_TIMER) {
                        Text("タイマー ${entry.timerMinutes}分", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

// ─── 時刻・イベント名入力ダイアログ ─────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    initialHour: Int = 7,
    initialMinute: Int = 0,
    initialName: String = "",
    initialAlarmType: String = ALARM_TYPE_ALARM,
    initialTimerMinutes: Int = DEFAULT_TIMER_MINUTES,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, name: String, alarmType: String, timerMinutes: Int) -> Unit
) {
    var hourStr by remember { mutableStateOf(initialHour.toString().padStart(2, '0')) }
    var minuteStr by remember { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var name by remember { mutableStateOf(initialName) }
    var useClockInput by remember { mutableStateOf(true) }
    var alarmType by remember {
        mutableStateOf(if (initialAlarmType == ALARM_TYPE_TIMER) ALARM_TYPE_TIMER else ALARM_TYPE_ALARM)
    }
    var timerMinutesStr by remember {
        mutableStateOf(initialTimerMinutes.coerceIn(1, 999).toString())
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(min = 360.dp, max = 420.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = useClockInput,
                        onClick = { useClockInput = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("時計")
                    }
                    SegmentedButton(
                        selected = !useClockInput,
                        onClick = {
                            if (useClockInput) {
                                hourStr = timePickerState.hour.toString().padStart(2, '0')
                                minuteStr = timePickerState.minute.toString().padStart(2, '0')
                            }
                            useClockInput = false
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("数字")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 384.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (useClockInput) {
                        TimePicker(state = timePickerState)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(384.dp)
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("イベント名（任意）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = alarmType == ALARM_TYPE_ALARM,
                        onClick = { alarmType = ALARM_TYPE_ALARM },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("アラーム")
                    }
                    SegmentedButton(
                        selected = alarmType == ALARM_TYPE_TIMER,
                        onClick = { alarmType = ALARM_TYPE_TIMER },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("タイマー")
                    }
                }

                if (alarmType == ALARM_TYPE_TIMER) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = timerMinutesStr,
                        onValueChange = { value ->
                            if (value.length <= 3 && value.all { it.isDigit() }) {
                                timerMinutesStr = value
                            }
                        },
                        label = { Text("タイマー時間") },
                        suffix = { Text("分") },
                        singleLine = true,
                        isError = timerMinutesStr.toIntOrNull()?.let { it !in 1..999 } ?: true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("キャンセル") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val h = if (useClockInput) {
                            timePickerState.hour
                        } else {
                            hourStr.toIntOrNull()?.coerceIn(0, 23) ?: initialHour
                        }
                        val m = if (useClockInput) {
                            timePickerState.minute
                        } else {
                            minuteStr.toIntOrNull()?.coerceIn(0, 59) ?: initialMinute
                        }
                        val timerMinutes = timerMinutesStr.toIntOrNull()
                            ?.coerceIn(1, 999)
                            ?: DEFAULT_TIMER_MINUTES
                        onConfirm(h, m, name.trim(), alarmType, timerMinutes)
                    }) { Text("OK") }
                }
            }
        }
    }
}
