package com.routinealarm.ui.week;

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
public final class WeekViewModel_Factory implements Factory<WeekViewModel> {
  private final Provider<WeeklyAlarmRepository> repoProvider;

  private final Provider<AlarmScheduler> schedulerProvider;

  public WeekViewModel_Factory(Provider<WeeklyAlarmRepository> repoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    this.repoProvider = repoProvider;
    this.schedulerProvider = schedulerProvider;
  }

  @Override
  public WeekViewModel get() {
    return newInstance(repoProvider.get(), schedulerProvider.get());
  }

  public static WeekViewModel_Factory create(Provider<WeeklyAlarmRepository> repoProvider,
      Provider<AlarmScheduler> schedulerProvider) {
    return new WeekViewModel_Factory(repoProvider, schedulerProvider);
  }

  public static WeekViewModel newInstance(WeeklyAlarmRepository repo, AlarmScheduler scheduler) {
    return new WeekViewModel(repo, scheduler);
  }
}
