package com.routinealarm.data.repository

import com.routinealarm.data.db.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(
    private val dao: RoutineEntryDao
) {
    fun getAll(): Flow<List<RoutineEntryEntity>> = dao.getAll()

    suspend fun add(hour: Int, minute: Int, eventName: String) {
        dao.insert(RoutineEntryEntity(hour = hour, minute = minute, eventName = eventName))
    }

    suspend fun update(entry: RoutineEntryEntity) = dao.update(entry)

    suspend fun delete(entry: RoutineEntryEntity) = dao.delete(entry)
}

@Singleton
class WeeklyAlarmRepository @Inject constructor(
    private val dao: WeeklyAlarmDao
) {
    fun getAll(): Flow<List<WeeklyAlarmEntity>> = dao.getAll()

    fun getByDay(dayOfWeek: Int): Flow<List<WeeklyAlarmEntity>> = dao.getByDay(dayOfWeek)

    suspend fun add(
        dayOfWeek: Int, hour: Int, minute: Int,
        eventName: String, isFromRoutine: Boolean = false
    ): Long = dao.insert(
        WeeklyAlarmEntity(
            dayOfWeek = dayOfWeek, hour = hour, minute = minute,
            eventName = eventName, isEnabled = true, isFromRoutine = isFromRoutine
        )
    )

    suspend fun update(alarm: WeeklyAlarmEntity) = dao.update(alarm)

    suspend fun delete(alarm: WeeklyAlarmEntity) = dao.delete(alarm)

    /** 指定曜日の [全体]タグ付きを削除してからルーティンをコピー */
    suspend fun copyRoutineToDay(dayOfWeek: Int, routines: List<RoutineEntryEntity>) {
        dao.deleteRoutineCopiesByDay(dayOfWeek)
        routines.forEach { r ->
            dao.insert(
                WeeklyAlarmEntity(
                    dayOfWeek = dayOfWeek, hour = r.hour, minute = r.minute,
                    eventName = r.eventName, isEnabled = true, isFromRoutine = true
                )
            )
        }
    }
}

@Singleton
class TodayAlarmRepository @Inject constructor(
    private val dao: TodayAlarmDao
) {
    fun getByDate(date: String): Flow<List<TodayAlarmEntity>> = dao.getByDate(date)

    suspend fun getByDateOnce(date: String): List<TodayAlarmEntity> = dao.getByDateOnce(date)

    suspend fun add(
        date: String, hour: Int, minute: Int,
        eventName: String, isTodayOnly: Boolean = false
    ): Long = dao.insert(
        TodayAlarmEntity(
            date = date, hour = hour, minute = minute,
            eventName = eventName, isEnabled = true, isTodayOnly = isTodayOnly,
            originalHour = hour, originalMinute = minute
        )
    )

    suspend fun update(alarm: TodayAlarmEntity) = dao.update(alarm)

    suspend fun delete(alarm: TodayAlarmEntity) = dao.delete(alarm)

    /** 旧日付の非「本日のみ」アラームを削除 */
    suspend fun deleteOldNonTodayOnly(keepDate: String) =
        dao.deleteOldNonTodayOnly(keepDate)

    /** 指定日付の非「本日のみ」アラームを削除（再生成前に実行） */
    suspend fun deleteTodayNonOnlyByDate(date: String) =
        dao.deleteTodayNonOnlyByDate(date)
}
