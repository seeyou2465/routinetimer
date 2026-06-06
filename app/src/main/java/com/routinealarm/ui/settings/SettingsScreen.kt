package com.routinealarm.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
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
import com.routinealarm.ui.common.TimePickerDialog
import kotlinx.coroutines.launch

private val DAY_LABELS = listOf("日", "月", "火", "水", "木", "金", "土")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("ルーティン") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("権限") }
            )
        }

        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageContent = { page ->
                when (page) {
                    0 -> RoutineSubScreen(viewModel)
                    1 -> PermissionSubScreen(viewModel)
                }
            }
        )
    }
}

// ─── ルーティンページ ──────────────────────────────────────────
@Composable
private fun RoutineSubScreen(viewModel: SettingsViewModel) {
    val routines by viewModel.routines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<RoutineEntryEntity?>(null) }
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
                        onDelete = { viewModel.deleteRoutine(entry) },
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
            onConfirm = { h, m, name ->
                viewModel.addRoutine(h, m, name)
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
            onDismiss = { editTarget = null },
            onConfirm = { h, m, name ->
                viewModel.updateRoutine(target, h, m, name)
                editTarget = null
            }
        )
    }

    if (showCopyDialog) {
        CopyRoutineDialog(
            onDismiss = { showCopyDialog = false },
            onCopyToDay = { day ->
                viewModel.copyRoutineToDay(day)
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
private fun CopyRoutineDialog(
    onDismiss: () -> Unit,
    onCopyToDay: (Int) -> Unit,
    onCopyToAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("曜日へコピー") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("コピー先の曜日を選択してください。\n[全体]タグ付きアラームが置き換わります。",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                DAY_LABELS.forEachIndexed { idx, label ->
                    OutlinedButton(
                        onClick = { onCopyToDay(idx + 1) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${label}曜日")
                    }
                }
                HorizontalDivider()
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
                color = Color.White,
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
                Text(label, fontSize = 15.sp, color = Color.White)
                if (!granted) {
                    Text("タップして設定画面へ", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
