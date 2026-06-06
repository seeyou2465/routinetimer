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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.routinealarm.data.db.TodayAlarmEntity
import com.routinealarm.ui.common.AlarmCardToday
import com.routinealarm.ui.common.TimePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel = hiltViewModel()) {
    val alarms by viewModel.alarms.collectAsState()
    val today by viewModel.today.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<TodayAlarmEntity?>(null) }

    val dateStr = remember(today) {
        today.format(DateTimeFormatter.ofPattern("M月d日(E)", Locale.JAPANESE))
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("本日 $dateStr", fontSize = 18.sp) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "更新")
                    }
                }
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
                        onDelete = { viewModel.delete(alarm) },
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
}
