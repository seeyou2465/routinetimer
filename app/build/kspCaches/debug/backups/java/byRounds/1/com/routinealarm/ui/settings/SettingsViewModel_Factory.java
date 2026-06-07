package com.routinealarm.ui.settings;

import android.content.Context;
import com.routinealarm.data.repository.AlarmSettingsRepository;
import com.routinealarm.data.repository.RoutineRepository;
import com.routinealarm.data.repository.WeeklyAlarmRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<RoutineRepository> routineRepoProvider;

  private final Provider<WeeklyAlarmRepository> weeklyRepoProvider;

  private final Provider<AlarmSettingsRepository> settingsRepoProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<RoutineRepository> routineRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmSettingsRepository> settingsRepoProvider) {
    this.contextProvider = contextProvider;
    this.routineRepoProvider = routineRepoProvider;
    this.weeklyRepoProvider = weeklyRepoProvider;
    this.settingsRepoProvider = settingsRepoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), routineRepoProvider.get(), weeklyRepoProvider.get(), settingsRepoProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<RoutineRepository> routineRepoProvider,
      Provider<WeeklyAlarmRepository> weeklyRepoProvider,
      Provider<AlarmSettingsRepository> settingsRepoProvider) {
    return new SettingsViewModel_Factory(contextProvider, routineRepoProvider, weeklyRepoProvider, settingsRepoProvider);
  }

  public static SettingsViewModel newInstance(Context context, RoutineRepository routineRepo,
      WeeklyAlarmRepository weeklyRepo, AlarmSettingsRepository settingsRepo) {
    return new SettingsViewModel(context, routineRepo, weeklyRepo, settingsRepo);
  }
}
