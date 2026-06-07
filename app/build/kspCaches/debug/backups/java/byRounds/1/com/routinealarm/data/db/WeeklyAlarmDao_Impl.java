package com.routinealarm.data.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WeeklyAlarmDao_Impl implements WeeklyAlarmDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WeeklyAlarmEntity> __insertionAdapterOfWeeklyAlarmEntity;

  private final EntityDeletionOrUpdateAdapter<WeeklyAlarmEntity> __deletionAdapterOfWeeklyAlarmEntity;

  private final EntityDeletionOrUpdateAdapter<WeeklyAlarmEntity> __updateAdapterOfWeeklyAlarmEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRoutineCopiesByDay;

  public WeeklyAlarmDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWeeklyAlarmEntity = new EntityInsertionAdapter<WeeklyAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weekly_alarms` (`id`,`dayOfWeek`,`hour`,`minute`,`eventName`,`isEnabled`,`isFromRoutine`,`alarmType`,`timerMinutes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeeklyAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDayOfWeek());
        statement.bindLong(3, entity.getHour());
        statement.bindLong(4, entity.getMinute());
        statement.bindString(5, entity.getEventName());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isFromRoutine() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindString(8, entity.getAlarmType());
        statement.bindLong(9, entity.getTimerMinutes());
      }
    };
    this.__deletionAdapterOfWeeklyAlarmEntity = new EntityDeletionOrUpdateAdapter<WeeklyAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `weekly_alarms` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeeklyAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWeeklyAlarmEntity = new EntityDeletionOrUpdateAdapter<WeeklyAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `weekly_alarms` SET `id` = ?,`dayOfWeek` = ?,`hour` = ?,`minute` = ?,`eventName` = ?,`isEnabled` = ?,`isFromRoutine` = ?,`alarmType` = ?,`timerMinutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeeklyAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDayOfWeek());
        statement.bindLong(3, entity.getHour());
        statement.bindLong(4, entity.getMinute());
        statement.bindString(5, entity.getEventName());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isFromRoutine() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindString(8, entity.getAlarmType());
        statement.bindLong(9, entity.getTimerMinutes());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRoutineCopiesByDay = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM weekly_alarms WHERE dayOfWeek = ? AND isFromRoutine = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final WeeklyAlarmEntity alarm,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWeeklyAlarmEntity.insertAndReturnId(alarm);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final WeeklyAlarmEntity alarm,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWeeklyAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final WeeklyAlarmEntity alarm,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWeeklyAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRoutineCopiesByDay(final int dayOfWeek,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRoutineCopiesByDay.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, dayOfWeek);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRoutineCopiesByDay.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WeeklyAlarmEntity>> getByDay(final int dayOfWeek) {
    final String _sql = "SELECT * FROM weekly_alarms WHERE dayOfWeek = ? ORDER BY hour, minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayOfWeek);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weekly_alarms"}, new Callable<List<WeeklyAlarmEntity>>() {
      @Override
      @NonNull
      public List<WeeklyAlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEventName = CursorUtil.getColumnIndexOrThrow(_cursor, "eventName");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfIsFromRoutine = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromRoutine");
          final int _cursorIndexOfAlarmType = CursorUtil.getColumnIndexOrThrow(_cursor, "alarmType");
          final int _cursorIndexOfTimerMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timerMinutes");
          final List<WeeklyAlarmEntity> _result = new ArrayList<WeeklyAlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WeeklyAlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final String _tmpEventName;
            _tmpEventName = _cursor.getString(_cursorIndexOfEventName);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final boolean _tmpIsFromRoutine;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFromRoutine);
            _tmpIsFromRoutine = _tmp_1 != 0;
            final String _tmpAlarmType;
            _tmpAlarmType = _cursor.getString(_cursorIndexOfAlarmType);
            final int _tmpTimerMinutes;
            _tmpTimerMinutes = _cursor.getInt(_cursorIndexOfTimerMinutes);
            _item = new WeeklyAlarmEntity(_tmpId,_tmpDayOfWeek,_tmpHour,_tmpMinute,_tmpEventName,_tmpIsEnabled,_tmpIsFromRoutine,_tmpAlarmType,_tmpTimerMinutes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WeeklyAlarmEntity>> getAll() {
    final String _sql = "SELECT * FROM weekly_alarms ORDER BY dayOfWeek, hour, minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weekly_alarms"}, new Callable<List<WeeklyAlarmEntity>>() {
      @Override
      @NonNull
      public List<WeeklyAlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEventName = CursorUtil.getColumnIndexOrThrow(_cursor, "eventName");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfIsFromRoutine = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromRoutine");
          final int _cursorIndexOfAlarmType = CursorUtil.getColumnIndexOrThrow(_cursor, "alarmType");
          final int _cursorIndexOfTimerMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timerMinutes");
          final List<WeeklyAlarmEntity> _result = new ArrayList<WeeklyAlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WeeklyAlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final String _tmpEventName;
            _tmpEventName = _cursor.getString(_cursorIndexOfEventName);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final boolean _tmpIsFromRoutine;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFromRoutine);
            _tmpIsFromRoutine = _tmp_1 != 0;
            final String _tmpAlarmType;
            _tmpAlarmType = _cursor.getString(_cursorIndexOfAlarmType);
            final int _tmpTimerMinutes;
            _tmpTimerMinutes = _cursor.getInt(_cursorIndexOfTimerMinutes);
            _item = new WeeklyAlarmEntity(_tmpId,_tmpDayOfWeek,_tmpHour,_tmpMinute,_tmpEventName,_tmpIsEnabled,_tmpIsFromRoutine,_tmpAlarmType,_tmpTimerMinutes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
