package com.routinealarm.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.ui.common.AlarmCardToday
import com.routinealarm.ui.common.TimePickerDialog
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel = hiltViewModel()) {
    val alarms by viewModel.alarms.collectAsState()
    val today by viewModel.today.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<TodayAlarmEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<TodayAlarmEntity?>(null) }

    val dateText = remember(today) {
        today.format(DateTimeFormatter.ofPattern("M月d日", Locale.JAPANESE))
    }
    val weekdayText = remember(today) {
        today.format(DateTimeFormatter.ofPattern("E", Locale.JAPANESE))
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            TodayDateHeader(
                dateText = dateText,
                weekdayText = weekdayText,
                onRefresh = { viewModel.refresh() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "本日のみ追加")
            }
        }
    ) { paddingValues ->
        if (alarms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("本日のアラームはありません\n更新ボタンで曜日設定を反映できます",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCardToday(
                        alarm = alarm,
                        onToggle = { viewModel.toggleEnabled(alarm) },
                        onDelete = { deleteTarget = alarm },
                        onEdit = { editTarget = alarm }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        TimePickerDialog(
            title = "本日のみアラームを追加",
            onDismiss = { showAddDialog = false },
            onConfirm = { h, m, name ->
                viewModel.addTodayOnly(h, m, name)
                showAddDialog = false
            }
        )
    }

    editTarget?.let { target ->
        TimePickerDialog(
            title = "アラームを編集",
            initialHour = target.hour,
            initialMinute = target.minute,
            initialName = target.eventName,
            onDismiss = { editTarget = null },
            onConfirm = { h, m, name ->
                viewModel.updateAlarm(target, h, m, name)
                editTarget = null
            }
        )
    }

    deleteTarget?.let { target ->
        DeleteTodayAlarmConfirmDialog(
            target = target,
            onDismiss = { deleteTarget = null },
            onConfirm = {
                viewModel.delete(target)
                deleteTarget = null
            }
        )
    }
}

@Composable
private fun TodayDateHeader(
    dateText: String,
    weekdayText: String,
    onRefresh: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = weekdayText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            FilledTonalIconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "更新")
            }
        }
    }
}

@Composable
private fun DeleteTodayAlarmConfirmDialog(
    target: TodayAlarmEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("アラームを削除") },
        text = {
            Text(
                "「%02d:%02d %s」を削除しますか？".format(
                    target.hour,
                    target.minute,
                    target.eventName.ifBlank { "名称なし" }
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("削除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}
