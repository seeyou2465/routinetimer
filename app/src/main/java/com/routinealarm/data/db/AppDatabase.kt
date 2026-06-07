package com.routinealarm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RoutineEntryEntity::class, WeeklyAlarmEntity::class, TodayAlarmEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineEntryDao(): RoutineEntryDao
    abstract fun weeklyAlarmDao(): WeeklyAlarmDao
    abstract fun todayAlarmDao(): TodayAlarmDao
}
