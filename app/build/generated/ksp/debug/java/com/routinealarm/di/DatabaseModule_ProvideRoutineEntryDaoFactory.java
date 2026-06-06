package com.routinealarm.di;

import com.routinealarm.data.db.AppDatabase;
import com.routinealarm.data.db.RoutineEntryDao;
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
public final class DatabaseModule_ProvideRoutineEntryDaoFactory implements Factory<RoutineEntryDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideRoutineEntryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RoutineEntryDao get() {
    return provideRoutineEntryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideRoutineEntryDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideRoutineEntryDaoFactory(dbProvider);
  }

  public static RoutineEntryDao provideRoutineEntryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRoutineEntryDao(db));
  }
}
