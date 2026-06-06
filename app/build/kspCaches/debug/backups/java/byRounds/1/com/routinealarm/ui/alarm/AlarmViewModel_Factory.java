package com.routinealarm.ui.alarm;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AlarmViewModel_Factory implements Factory<AlarmViewModel> {
  @Override
  public AlarmViewModel get() {
    return newInstance();
  }

  public static AlarmViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AlarmViewModel newInstance() {
    return new AlarmViewModel();
  }

  private static final class InstanceHolder {
    private static final AlarmViewModel_Factory INSTANCE = new AlarmViewModel_Factory();
  }
}
