package com.routinealarm.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.routinealarm.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE today_alarms ADD COLUMN originalHour INTEGER NOT NULL DEFAULT -1")
            db.execSQL("ALTER TABLE today_alarms ADD COLUMN originalMinute INTEGER NOT NULL DEFAULT -1")
            db.execSQL("UPDATE today_alarms SET originalHour = hour WHERE originalHour = -1")
            db.execSQL("UPDATE today_alarms SET originalMinute = minute WHERE originalMinute = -1")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE routine_entries ADD COLUMN alarmType TEXT NOT NULL DEFAULT 'ALARM'")
            db.execSQL("ALTER TABLE routine_entries ADD COLUMN timerMinutes INTEGER NOT NULL DEFAULT 10")
            db.execSQL("ALTER TABLE weekly_alarms ADD COLUMN alarmType TEXT NOT NULL DEFAULT 'ALARM'")
            db.execSQL("ALTER TABLE weekly_alarms ADD COLUMN timerMinutes INTEGER NOT NULL DEFAULT 10")
            db.execSQL("ALTER TABLE today_alarms ADD COLUMN alarmType TEXT NOT NULL DEFAULT 'ALARM'")
            db.execSQL("ALTER TABLE today_alarms ADD COLUMN timerMinutes INTEGER NOT NULL DEFAULT 10")
        }
    }

    private val MIGRATION_1_3 = object : Migration(1, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            MIGRATION_1_2.migrate(db)
            MIGRATION_2_3.migrate(db)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "routine_alarm.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
            .build()

    @Provides fun provideRoutineEntryDao(db: AppDatabase): RoutineEntryDao = db.routineEntryDao()
    @Provides fun provideWeeklyAlarmDao(db: AppDatabase): WeeklyAlarmDao = db.weeklyAlarmDao()
    @Provides fun provideTodayAlarmDao(db: AppDatabase): TodayAlarmDao = db.todayAlarmDao()
}
