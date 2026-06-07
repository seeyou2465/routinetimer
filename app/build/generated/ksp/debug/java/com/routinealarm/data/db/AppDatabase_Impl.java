package com.routinealarm.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RoutineEntryDao _routineEntryDao;

  private volatile WeeklyAlarmDao _weeklyAlarmDao;

  private volatile TodayAlarmDao _todayAlarmDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `routine_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `eventName` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, `alarmType` TEXT NOT NULL DEFAULT 'ALARM', `timerMinutes` INTEGER NOT NULL DEFAULT 10)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weekly_alarms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dayOfWeek` INTEGER NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `eventName` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `isFromRoutine` INTEGER NOT NULL, `alarmType` TEXT NOT NULL DEFAULT 'ALARM', `timerMinutes` INTEGER NOT NULL DEFAULT 10)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `today_alarms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `eventName` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `isTodayOnly` INTEGER NOT NULL, `originalHour` INTEGER NOT NULL DEFAULT -1, `originalMinute` INTEGER NOT NULL DEFAULT -1, `alarmType` TEXT NOT NULL DEFAULT 'ALARM', `timerMinutes` INTEGER NOT NULL DEFAULT 10)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8fd19f3ba2ee61429e8f1f44876cc737')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `routine_entries`");
        db.execSQL("DROP TABLE IF EXISTS `weekly_alarms`");
        db.execSQL("DROP TABLE IF EXISTS `today_alarms`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRoutineEntries = new HashMap<String, TableInfo.Column>(7);
        _columnsRoutineEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("hour", new TableInfo.Column("hour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("minute", new TableInfo.Column("minute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("eventName", new TableInfo.Column("eventName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("alarmType", new TableInfo.Column("alarmType", "TEXT", true, 0, "'ALARM'", TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutineEntries.put("timerMinutes", new TableInfo.Column("timerMinutes", "INTEGER", true, 0, "10", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutineEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRoutineEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoutineEntries = new TableInfo("routine_entries", _columnsRoutineEntries, _foreignKeysRoutineEntries, _indicesRoutineEntries);
        final TableInfo _existingRoutineEntries = TableInfo.read(db, "routine_entries");
        if (!_infoRoutineEntries.equals(_existingRoutineEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "routine_entries(com.routinealarm.data.db.RoutineEntryEntity).\n"
                  + " Expected:\n" + _infoRoutineEntries + "\n"
                  + " Found:\n" + _existingRoutineEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsWeeklyAlarms = new HashMap<String, TableInfo.Column>(9);
        _columnsWeeklyAlarms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("dayOfWeek", new TableInfo.Column("dayOfWeek", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("hour", new TableInfo.Column("hour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("minute", new TableInfo.Column("minute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("eventName", new TableInfo.Column("eventName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("isFromRoutine", new TableInfo.Column("isFromRoutine", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("alarmType", new TableInfo.Column("alarmType", "TEXT", true, 0, "'ALARM'", TableInfo.CREATED_FROM_ENTITY));
        _columnsWeeklyAlarms.put("timerMinutes", new TableInfo.Column("timerMinutes", "INTEGER", true, 0, "10", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeeklyAlarms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeeklyAlarms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWeeklyAlarms = new TableInfo("weekly_alarms", _columnsWeeklyAlarms, _foreignKeysWeeklyAlarms, _indicesWeeklyAlarms);
        final TableInfo _existingWeeklyAlarms = TableInfo.read(db, "weekly_alarms");
        if (!_infoWeeklyAlarms.equals(_existingWeeklyAlarms)) {
          return new RoomOpenHelper.ValidationResult(false, "weekly_alarms(com.routinealarm.data.db.WeeklyAlarmEntity).\n"
                  + " Expected:\n" + _infoWeeklyAlarms + "\n"
                  + " Found:\n" + _existingWeeklyAlarms);
        }
        final HashMap<String, TableInfo.Column> _columnsTodayAlarms = new HashMap<String, TableInfo.Column>(11);
        _columnsTodayAlarms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("hour", new TableInfo.Column("hour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("minute", new TableInfo.Column("minute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("eventName", new TableInfo.Column("eventName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("isTodayOnly", new TableInfo.Column("isTodayOnly", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("originalHour", new TableInfo.Column("originalHour", "INTEGER", true, 0, "-1", TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("originalMinute", new TableInfo.Column("originalMinute", "INTEGER", true, 0, "-1", TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("alarmType", new TableInfo.Column("alarmType", "TEXT", true, 0, "'ALARM'", TableInfo.CREATED_FROM_ENTITY));
        _columnsTodayAlarms.put("timerMinutes", new TableInfo.Column("timerMinutes", "INTEGER", true, 0, "10", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTodayAlarms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTodayAlarms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTodayAlarms = new TableInfo("today_alarms", _columnsTodayAlarms, _foreignKeysTodayAlarms, _indicesTodayAlarms);
        final TableInfo _existingTodayAlarms = TableInfo.read(db, "today_alarms");
        if (!_infoTodayAlarms.equals(_existingTodayAlarms)) {
          return new RoomOpenHelper.ValidationResult(false, "today_alarms(com.routinealarm.data.db.TodayAlarmEntity).\n"
                  + " Expected:\n" + _infoTodayAlarms + "\n"
                  + " Found:\n" + _existingTodayAlarms);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8fd19f3ba2ee61429e8f1f44876cc737", "2e3d9efedb0fbe26d14d5f03f8f71c44");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "routine_entries","weekly_alarms","today_alarms");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `routine_entries`");
      _db.execSQL("DELETE FROM `weekly_alarms`");
      _db.execSQL("DELETE FROM `today_alarms`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RoutineEntryDao.class, RoutineEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeeklyAlarmDao.class, WeeklyAlarmDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TodayAlarmDao.class, TodayAlarmDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RoutineEntryDao routineEntryDao() {
    if (_routineEntryDao != null) {
      return _routineEntryDao;
    } else {
      synchronized(this) {
        if(_routineEntryDao == null) {
          _routineEntryDao = new RoutineEntryDao_Impl(this);
        }
        return _routineEntryDao;
      }
    }
  }

  @Override
  public WeeklyAlarmDao weeklyAlarmDao() {
    if (_weeklyAlarmDao != null) {
      return _weeklyAlarmDao;
    } else {
      synchronized(this) {
        if(_weeklyAlarmDao == null) {
          _weeklyAlarmDao = new WeeklyAlarmDao_Impl(this);
        }
        return _weeklyAlarmDao;
      }
    }
  }

  @Override
  public TodayAlarmDao todayAlarmDao() {
    if (_todayAlarmDao != null) {
      return _todayAlarmDao;
    } else {
      synchronized(this) {
        if(_todayAlarmDao == null) {
          _todayAlarmDao = new TodayAlarmDao_Impl(this);
        }
        return _todayAlarmDao;
      }
    }
  }
}
