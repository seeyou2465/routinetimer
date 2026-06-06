package com.routinealarm.di;

import com.routinealarm.data.db.AppDatabase;
import com.routinealarm.data.db.TodayAlarmDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideTodayAlarmDaoFactory implements Factory<TodayAlarmDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideTodayAlarmDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TodayAlarmDao get() {
    return provideTodayAlarmDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTodayAlarmDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideTodayAlarmDaoFactory(dbProvider);
  }

  public static TodayAlarmDao provideTodayAlarmDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTodayAlarmDao(db));
  }
}
