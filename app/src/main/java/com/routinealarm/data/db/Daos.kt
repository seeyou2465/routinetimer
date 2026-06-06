package com.routinealarm.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineEntryDao {
    @Query("SELECT * FROM routine_entries ORDER BY hour, minute, sortOrder")
    fun getAll(): Flow<List<RoutineEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: RoutineEntryEntity): Long

    @Update
    suspend fun update(entry: RoutineEntryEntity)

    @Delete
    suspend fun delete(entry: RoutineEntryEntity)

    @Query("DELETE FROM routine_entries")
    suspend fun deleteAll()
}

@Dao
interface WeeklyAlarmDao {
    @Query("SELECT * FROM weekly_alarms WHERE dayOfWeek = :dayOfWeek ORDER BY hour, minute")
    fun getByDay(dayOfWeek: Int): Flow<List<WeeklyAlarmEntity>>

    @Query("SELECT * FROM weekly_alarms ORDER BY dayOfWeek, hour, minute")
    fun getAll(): Flow<List<WeeklyAlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: WeeklyAlarmEntity): Long

    @Update
    suspend fun update(alarm: WeeklyAlarmEntity)

    @Delete
    suspend fun delete(alarm: WeeklyAlarmEntity)

    @Query("DELETE FROM weekly_alarms WHERE dayOfWeek = :dayOfWeek AND isFromRoutine = 1")
    suspend fun deleteRoutineCopiesByDay(dayOfWeek: Int)
}

@Dao
interface TodayAlarmDao {
    @Query("SELECT * FROM today_alarms WHERE date = :date ORDER BY hour, minute")
    fun getByDate(date: String): Flow<List<TodayAlarmEntity>>

    @Query("SELECT * FROM today_alarms WHERE date = :date ORDER BY hour, minute")
    suspend fun getByDateOnce(date: String): List<TodayAlarmEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: TodayAlarmEntity): Long

    @Update
    suspend fun update(alarm: TodayAlarmEntity)

    @Delete
    suspend fun delete(alarm: TodayAlarmEntity)

    @Query("DELETE FROM today_alarms WHERE date != :keepDate AND isTodayOnly = 0")
    suspend fun deleteOldNonTodayOnly(keepDate: String)

    @Query("DELETE FROM today_alarms WHERE date = :date AND isTodayOnly = 0")
    suspend fun deleteTodayNonOnlyByDate(date: String)
}
