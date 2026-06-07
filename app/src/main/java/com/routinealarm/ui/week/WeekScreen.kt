package com.routinealarm.ui.week

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.routinealarm.data.db.WeeklyAlarmEntity
import com.routinealarm.ui.common.AlarmCardWeekly
import com.routinealarm.ui.common.TimePickerDialog
import kotlinx.coroutines.launch

private val DAY_LABELS = listOf("日", "月", "火", "水", "木", "金", "土")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeekScreen(viewModel: WeekViewModel = hiltViewModel()) {
    val allAlarms by viewModel.allAlarms.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<WeeklyAlarmEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 曜日タブ表示
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                DAY_LABELS.forEachIndexed { idx, label ->
                    Tab(
                        selected = pagerState.currentPage == idx,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(idx) } },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                text = label,
                                fontSize = 16.sp,
                                fontWeight = if (pagerState.currentPage == idx) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        }
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            val dayOfWeek = page + 1 // 1=日, ..7=土
            val dayAlarms = allAlarms.filter { it.dayOfWeek == dayOfWeek }
                .sortedWith(compareBy({ it.hour }, { it.minute }))

            Box(modifier = Modifier.fillMaxSize()) {
                if (dayAlarms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "アラームなし\n右下の＋ボタンで追加",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dayAlarms, key = { it.id }) { alarm ->
                            AlarmCardWeekly(
                                alarm = alarm,
                                onToggle = { viewModel.toggleEnabled(alarm) },
                                onDelete = { viewModel.deleteAlarm(alarm) },
                                onEdit = { editTarget = alarm }
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
                }
            }

            // 編集ダイアログ
            editTarget?.let { target ->
                if (target.dayOfWeek == dayOfWeek) {
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
        }
    }

    if (showAddDialog) {
        // 現在表示しているページの曜日に追加
        val currentDay = pagerState.currentPage + 1
        TimePickerDialog(
            title = "${DAY_LABELS[pagerState.currentPage]}曜日のアラームを追加",
            onDismiss = { showAddDialog = false },
            onConfirm = { h, m, name ->
                viewModel.addAlarm(currentDay, h, m, name)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberPagerState(pageCount: () -> Int) =
    androidx.compose.foundation.pager.rememberPagerState(pageCount = pageCount)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalPager(
    state: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    content: @Composable androidx.compose.foundation.pager.PagerScope.(page: Int) -> Unit
) {
    androidx.compose.foundation.pager.HorizontalPager(
        state = state,
        modifier = modifier,
        userScrollEnabled = userScrollEnabled,
        pageContent = content
    )
}
