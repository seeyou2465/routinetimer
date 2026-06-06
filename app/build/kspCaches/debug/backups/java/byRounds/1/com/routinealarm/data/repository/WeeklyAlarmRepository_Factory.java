package com.routinealarm.data.repository;

import com.routinealarm.data.db.WeeklyAlarmDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class WeeklyAlarmRepository_Factory implements Factory<WeeklyAlarmRepository> {
  private final Provider<WeeklyAlarmDao> daoProvider;

  public WeeklyAlarmRepository_Factory(Provider<WeeklyAlarmDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public WeeklyAlarmRepository get() {
    return newInstance(daoProvider.get());
  }

  public static WeeklyAlarmRepository_Factory create(Provider<WeeklyAlarmDao> daoProvider) {
    return new WeeklyAlarmRepository_Factory(daoProvider);
  }

  public static WeeklyAlarmRepository newInstance(WeeklyAlarmDao dao) {
    return new WeeklyAlarmRepository(dao);
  }
}
