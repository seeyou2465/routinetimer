package com.routinealarm.data.repository;

import com.routinealarm.data.db.RoutineEntryDao;
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
public final class RoutineRepository_Factory implements Factory<RoutineRepository> {
  private final Provider<RoutineEntryDao> daoProvider;

  public RoutineRepository_Factory(Provider<RoutineEntryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RoutineRepository get() {
    return newInstance(daoProvider.get());
  }

  public static RoutineRepository_Factory create(Provider<RoutineEntryDao> daoProvider) {
    return new RoutineRepository_Factory(daoProvider);
  }

  public static RoutineRepository newInstance(RoutineEntryDao dao) {
    return new RoutineRepository(dao);
  }
}
