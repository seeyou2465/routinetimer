package com.routinealarm.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.routinealarm.data.db.RoutineEntryEntity
import com.routinealarm.ui.common.RoutineCard
import com.routinealarm.ui.common.tabLabelSwipe
import com.routinealarm.ui.common.TimePickerDialog
import kotlinx.coroutines.launch

private val DAY_LABELS = listOf("日", "月", "火", "水", "木", "金", "土")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.tabLabelSwipe(
                pagerState = pagerState,
                pageCount = 3,
                coroutineScope = coroutineScope
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            "ルーティン",
                            fontSize = 16.sp,
                            fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            "権限",
                            fontSize = 16.sp,
                            fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            "操作",
                            fontSize = 16.sp,
                            fontWeight = if (pagerState.currentPage == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false,
            pageContent = { page ->
                when (page) {
                    0 -> RoutineSubScreen(viewModel)
                    1 -> PermissionSubScreen(viewModel)
                    2 -> OperationSubScreen(viewModel)
                }
            }
        )
    }
}

@Composable
private fun OperationSubScreen(viewModel: SettingsViewModel) {
    val delayMinutes by viewModel.todayDelayMinutes.collectAsState()
    val alarmSoundEnabled by viewModel.alarmSoundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    var input by remember(delayMinutes) { mutableStateOf(delayMinutes.toString()) }
    val parsedMinutes = input.toIntOrNull()
    val isValid = parsedMinutes != null && parsedMinutes in 1..120

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "発火時の動作",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "アラーム発火時、またはタイマー完了時の通知方法を設定します。",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AlertSettingRow(
                        title = "アラーム音",
                        description = "端末の標準アラーム音を鳴らします。",
                        checked = alarmSoundEnabled,
                        onCheckedChange = viewModel::updateAlarmSoundEnabled
                    )
                    HorizontalDivider()
                    AlertSettingRow(
                        title = "バイブレーション",
                        description = "音と併用、または単体で振動します。",
                        checked = vibrationEnabled,
                        onCheckedChange = viewModel::updateVibrationEnabled
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "本日アラームの右スワイプ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "右スワイプしたときに、本日限りで設定時間を延長する分数です。",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = input,
                        onValueChange = { value ->
                            if (value.length <= 3 && value.all { it.isDigit() }) {
                                input = value
                            }
                        },
                        label = { Text("延長分数") },
                        suffix = { Text("分") },
                        isError = input.isNotBlank() && !isValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (input.isNotBlank() && !isValid) {
                        Text(
                            "1〜120分で入力してください。",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = {
                            parsedMinutes?.let(viewModel::updateTodayDelayMinutes)
                        },
                        enabled = isValid && parsedMinutes != delayMinutes,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// ─── ルーティンページ ──────────────────────────────────────────
@Composable
private fun RoutineSubScreen(viewModel: SettingsViewModel) {
    val routines by viewModel.routines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<RoutineEntryEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<RoutineEntryEntity?>(null) }
    var showCopyDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "追加")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (routines.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ルーティンなし\n右下の＋ボタンで追加",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(routines, key = { it.id }) { entry ->
                    RoutineCard(
                        entry = entry,
                        onDelete = { deleteTarget = entry },
                        onEdit = { editTarget = entry }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showCopyDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = routines.isNotEmpty()
                ) {
                    Text("曜日へコピー")
                }
            }
        }
    }

    if (showAddDialog) {
        TimePickerDialog(
            title = "ルーティンを追加",
            onDismiss = { showAddDialog = false },
            onConfirm = { h, m, name, alarmType, timerMinutes ->
                viewModel.addRoutine(h, m, name, alarmType, timerMinutes)
                showAddDialog = false
            }
        )
    }

    editTarget?.let { target ->
        TimePickerDialog(
            title = "ルーティンを編集",
            initialHour = target.hour,
            initialMinute = target.minute,
            initialName = target.eventName,
            initialAlarmType = target.alarmType,
            initialTimerMinutes = target.timerMinutes,
            onDismiss = { editTarget = null },
            onConfirm = { h, m, name, alarmType, timerMinutes ->
                viewModel.updateRoutine(target, h, m, name, alarmType, timerMinutes)
                editTarget = null
            }
        )
    }

    deleteTarget?.let { target ->
        DeleteRoutineConfirmDialog(
            target = target,
            onDismiss = { deleteTarget = null },
            onConfirm = {
                viewModel.deleteRoutine(target)
                deleteTarget = null
            }
        )
    }

    if (showCopyDialog) {
        CopyRoutineDialog(
            onDismiss = { showCopyDialog = false },
            onCopyToDays = { days ->
                viewModel.copyRoutineToDays(days)
                showCopyDialog = false
            },
            onCopyToAll = {
                viewModel.copyRoutineToAllDays()
                showCopyDialog = false
            }
        )
    }
}

@Composable
private fun DeleteRoutineConfirmDialog(
    target: RoutineEntryEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ルーティンを削除") },
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

@Composable
private fun CopyRoutineDialog(
    onDismiss: () -> Unit,
    onCopyToDays: (Set<Int>) -> Unit,
    onCopyToAll: () -> Unit
) {
    var selectedDays by remember { mutableStateOf<Set<Int>>(emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("曜日へコピー") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("コピー先の曜日を選択してください。\n選択した曜日の[全体]タグ付きアラームが置き換わります。",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                DAY_LABELS.forEachIndexed { idx, label ->
                    val day = idx + 1
                    val selected = day in selectedDays
                    Surface(
                        onClick = {
                            selectedDays = if (selected) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        tonalElevation = if (selected) 2.dp else 0.dp,
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = { checked ->
                                    selectedDays = if (checked) {
                                        selectedDays + day
                                    } else {
                                        selectedDays - day
                                    }
                                }
                            )
                            Text(
                                "${label}曜日",
                                fontSize = 15.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
                HorizontalDivider()
                Button(
                    onClick = { onCopyToDays(selectedDays) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDays.isNotEmpty()
                ) {
                    Text("選択した曜日へコピー")
                }
                Button(onClick = onCopyToAll, modifier = Modifier.fillMaxWidth()) {
                    Text("全曜日へコピー")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

// ─── 権限ページ ────────────────────────────────────────────────
@Composable
private fun PermissionSubScreen(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) }

    val permissions = remember(refreshKey) {
        listOf(
            Triple("通知", viewModel.hasNotificationPermission()) {
                viewModel.openNotificationSettings(context)
            },
            Triple("正確なアラーム", viewModel.hasExactAlarmPermission()) {
                viewModel.openExactAlarmSettings(context)
            },
            Triple("全画面Intent", viewModel.hasFullScreenIntentPermission()) {
                viewModel.openFullScreenIntentSettings(context)
            },
            Triple("重ねて表示", viewModel.hasOverlayPermission()) {
                viewModel.openOverlaySettings(context)
            },
            Triple("バッテリー最適化除外", viewModel.isBatteryOptimizationIgnored()) {
                viewModel.openBatteryOptimizationSettings(context)
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "権限の付与状態",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(permissions) { (label, granted, action) ->
            PermissionItem(label = label, granted = granted, onTap = {
                action()
                refreshKey++
            })
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { refreshKey++ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("権限状態を更新")
            }
        }
    }
}

@Composable
private fun PermissionItem(label: String, granted: Boolean, onTap: () -> Unit) {
    Card(
        onClick = if (!granted) onTap else ({ }),
        colors = CardDefaults.cardColors(
            containerColor = if (granted) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (granted) "✓" else "✗",
                fontSize = 20.sp,
                color = if (granted) Color(0xFF66BB6A) else Color(0xFFEF5350),
                modifier = Modifier.width(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                if (!granted) {
                    Text("タップして設定画面へ", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
