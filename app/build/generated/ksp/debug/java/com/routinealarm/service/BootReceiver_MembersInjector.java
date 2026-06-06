package com.routinealarm.service;

import com.routinealarm.data.repository.TodayAlarmRepository;
import com.routinealarm.data.repository.WeeklyAlarmRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<TodayAlarmRepository> todayRepoProvider;

  private final Provider<WeeklyAlarmRepository> weeklyRepoProvider;

  private final Provider<AlarmScheduler> schedulerProvider;

  public BootReceiver_MembersInjector(Provider<TodayAlarmRepository> todayRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    this.todayRepoProvider = todayRepoProvider;
    this.weeklyRepoProvider = weeklyRepoProvider;
    this.schedulerProvider = schedulerProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<TodayAlarmRepository> todayRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    return new BootReceiver_MembersInjector(todayRepoProvider, weeklyRepoProvider, schedulerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectTodayRepo(instance, todayRepoProvider.get());
    injectWeeklyRepo(instance, weeklyRepoProvider.get());
    injectScheduler(instance, schedulerProvider.get());
  }

  @InjectedFieldSignature("com.routinealarm.service.BootReceiver.todayRepo")
  public static void injectTodayRepo(BootReceiver instance, TodayAlarmRepository todayRepo) {
    instance.todayRepo = todayRepo;
  }

  @InjectedFieldSignature("com.routinealarm.service.BootReceiver.weeklyRepo")
  public static void injectWeeklyRepo(BootReceiver instance, WeeklyAlarmRepository weeklyRepo) {
    instance.weeklyRepo = weeklyRepo;
  }

  @InjectedFieldSignature("com.routinealarm.service.BootReceiver.scheduler")
  public static void injectScheduler(BootReceiver instance, AlarmScheduler scheduler) {
    instance.scheduler = scheduler;
  }
}
