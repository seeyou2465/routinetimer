package com.routinealarm.data.repository;

import com.routinealarm.data.db.TodayAlarmDao;
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
public final class TodayAlarmRepository_Factory implements Factory<TodayAlarmRepository> {
  private final Provider<TodayAlarmDao> daoProvider;

  public TodayAlarmRepository_Factory(Provider<TodayAlarmDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TodayAlarmRepository get() {
    return newInstance(daoProvider.get());
  }

  public static TodayAlarmRepository_Factory create(Provider<TodayAlarmDao> daoProvider) {
    return new TodayAlarmRepository_Factory(daoProvider);
  }

  public static TodayAlarmRepository newInstance(TodayAlarmDao dao) {
    return new TodayAlarmRepository(dao);
  }
}
