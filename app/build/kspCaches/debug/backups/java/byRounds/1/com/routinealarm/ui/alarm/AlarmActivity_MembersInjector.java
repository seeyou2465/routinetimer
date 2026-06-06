package com.routinealarm.ui.alarm;

import com.routinealarm.service.AlarmScheduler;
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
public final class AlarmActivity_MembersInjector implements MembersInjector<AlarmActivity> {
  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public AlarmActivity_MembersInjector(Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  public static MembersInjector<AlarmActivity> create(
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new AlarmActivity_MembersInjector(alarmSchedulerProvider);
  }

  @Override
  public void injectMembers(AlarmActivity instance) {
    injectAlarmScheduler(instance, alarmSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.routinealarm.ui.alarm.AlarmActivity.alarmScheduler")
  public static void injectAlarmScheduler(AlarmActivity instance, AlarmScheduler alarmScheduler) {
    instance.alarmScheduler = alarmScheduler;
  }
}
