package com.routinealarm.ui.today;

import com.routinealarm.data.repository.TodayAlarmRepository;
import com.routinealarm.data.repository.WeeklyAlarmRepository;
import com.routinealarm.service.AlarmScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TodayViewModel_Factory implements Factory<TodayViewModel> {
  private final Provider<TodayAlarmRepository> todayRepoProvider;

  private final Provider<WeeklyAlarmRepository> weeklyRepoProvider;

  private final Provider<AlarmScheduler> schedulerProvider;

  public TodayViewModel_Factory(Provider<TodayAlarmRepository> todayRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    this.todayRepoProvider = todayRepoProvider;
    this.weeklyRepoProvider = weeklyRepoProvider;
    this.schedulerProvider = schedulerProvider;
  }

  @Override
  public TodayViewModel get() {
    return newInstance(todayRepoProvider.get(), weeklyRepoProvider.get(), schedulerProvider.get());
  }

  public static TodayViewModel_Factory create(Provider<TodayAlarmRepository> todayRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    return new TodayViewModel_Factory(todayRepoProvider, weeklyRepoProvider, schedulerProvider);
  }

  public static TodayViewModel newInstance(TodayAlarmRepository todayRepo,
      WeeklyAlarmRepository weeklyRepo, AlarmScheduler scheduler) {
    return new TodayViewModel(todayRepo, weeklyRepo, scheduler);
  }
}
