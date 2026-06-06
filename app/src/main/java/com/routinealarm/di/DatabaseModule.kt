package com.routinealarm.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "routine_alarm.db").build()

    @Provides fun provideRoutineEntryDao(db: AppDatabase): RoutineEntryDao = db.routineEntryDao()
    @Provides fun provideWeeklyAlarmDao(db: AppDatabase): WeeklyAlarmDao = db.weeklyAlarmDao()
    @Provides fun provideTodayAlarmDao(db: AppDatabase): TodayAlarmDao = db.todayAlarmDao()
}
