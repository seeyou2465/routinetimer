package com.routinealarm.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_entries")
data class RoutineEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val eventName: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "weekly_alarms")
data class WeeklyAlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int,        // 1=日, 2=月, ..., 7=土
    val hour: Int,
    val minute: Int,
    val eventName: String,
    val isEnabled: Boolean,
    val isFromRoutine: Boolean // [全体]タグ: 設定タブからコピーされたアラームか否か
)

@Entity(tableName = "today_alarms")
data class TodayAlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // "yyyy-MM-dd" 形式
    val hour: Int,
    val minute: Int,
    val eventName: String,
    val isEnabled: Boolean,
    val isTodayOnly: Boolean   // 本日限定フラグ
)
