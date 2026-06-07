package com.routinealarm.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
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
public final class TodayAlarmDao_Impl implements TodayAlarmDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TodayAlarmEntity> __insertionAdapterOfTodayAlarmEntity;

  private final EntityDeletionOrUpdateAdapter<TodayAlarmEntity> __deletionAdapterOfTodayAlarmEntity;

  private final EntityDeletionOrUpdateAdapter<TodayAlarmEntity> __updateAdapterOfTodayAlarmEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldNonTodayOnly;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTodayNonOnlyByDate;

  public TodayAlarmDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodayAlarmEntity = new EntityInsertionAdapter<TodayAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `today_alarms` (`id`,`date`,`hour`,`minute`,`eventName`,`isEnabled`,`isTodayOnly`,`originalHour`,`originalMinute`,`alarmType`,`timerMinutes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TodayAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindLong(3, entity.getHour());
        statement.bindLong(4, entity.getMinute());
        statement.bindString(5, entity.getEventName());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isTodayOnly() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getOriginalHour());
        statement.bindLong(9, entity.getOriginalMinute());
        statement.bindString(10, entity.getAlarmType());
        statement.bindLong(11, entity.getTimerMinutes());
      }
    };
    this.__deletionAdapterOfTodayAlarmEntity = new EntityDeletionOrUpdateAdapter<TodayAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `today_alarms` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TodayAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTodayAlarmEntity = new EntityDeletionOrUpdateAdapter<TodayAlarmEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `today_alarms` SET `id` = ?,`date` = ?,`hour` = ?,`minute` = ?,`eventName` = ?,`isEnabled` = ?,`isTodayOnly` = ?,`originalHour` = ?,`originalMinute` = ?,`alarmType` = ?,`timerMinutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TodayAlarmEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindLong(3, entity.getHour());
        statement.bindLong(4, entity.getMinute());
        statement.bindString(5, entity.getEventName());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isTodayOnly() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getOriginalHour());
        statement.bindLong(9, entity.getOriginalMinute());
        statement.bindString(10, entity.getAlarmType());
        statement.bindLong(11, entity.getTimerMinutes());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOldNonTodayOnly = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM today_alarms WHERE date != ? AND isTodayOnly = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTodayNonOnlyByDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM today_alarms WHERE date = ? AND isTodayOnly = 0";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TodayAlarmEntity alarm, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTodayAlarmEntity.insertAndReturnId(alarm);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final TodayAlarmEntity alarm, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTodayAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TodayAlarmEntity alarm, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTodayAlarmEntity.handle(alarm);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldNonTodayOnly(final String keepDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldNonTodayOnly.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, keepDate);
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
          __preparedStmtOfDeleteOldNonTodayOnly.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTodayNonOnlyByDate(final String date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTodayNonOnlyByDate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
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
          __preparedStmtOfDeleteTodayNonOnlyByDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TodayAlarmEntity>> getByDate(final String date) {
    final String _sql = "SELECT * FROM today_alarms WHERE date = ? ORDER BY hour, minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"today_alarms"}, new Callable<List<TodayAlarmEntity>>() {
      @Override
      @NonNull
      public List<TodayAlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEventName = CursorUtil.getColumnIndexOrThrow(_cursor, "eventName");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfIsTodayOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isTodayOnly");
          final int _cursorIndexOfOriginalHour = CursorUtil.getColumnIndexOrThrow(_cursor, "originalHour");
          final int _cursorIndexOfOriginalMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "originalMinute");
          final int _cursorIndexOfAlarmType = CursorUtil.getColumnIndexOrThrow(_cursor, "alarmType");
          final int _cursorIndexOfTimerMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timerMinutes");
          final List<TodayAlarmEntity> _result = new ArrayList<TodayAlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TodayAlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
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
            final boolean _tmpIsTodayOnly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsTodayOnly);
            _tmpIsTodayOnly = _tmp_1 != 0;
            final int _tmpOriginalHour;
            _tmpOriginalHour = _cursor.getInt(_cursorIndexOfOriginalHour);
            final int _tmpOriginalMinute;
            _tmpOriginalMinute = _cursor.getInt(_cursorIndexOfOriginalMinute);
            final String _tmpAlarmType;
            _tmpAlarmType = _cursor.getString(_cursorIndexOfAlarmType);
            final int _tmpTimerMinutes;
            _tmpTimerMinutes = _cursor.getInt(_cursorIndexOfTimerMinutes);
            _item = new TodayAlarmEntity(_tmpId,_tmpDate,_tmpHour,_tmpMinute,_tmpEventName,_tmpIsEnabled,_tmpIsTodayOnly,_tmpOriginalHour,_tmpOriginalMinute,_tmpAlarmType,_tmpTimerMinutes);
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
  public Object getByDateOnce(final String date,
      final Continuation<? super List<TodayAlarmEntity>> $completion) {
    final String _sql = "SELECT * FROM today_alarms WHERE date = ? ORDER BY hour, minute";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TodayAlarmEntity>>() {
      @Override
      @NonNull
      public List<TodayAlarmEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEventName = CursorUtil.getColumnIndexOrThrow(_cursor, "eventName");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfIsTodayOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isTodayOnly");
          final int _cursorIndexOfOriginalHour = CursorUtil.getColumnIndexOrThrow(_cursor, "originalHour");
          final int _cursorIndexOfOriginalMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "originalMinute");
          final int _cursorIndexOfAlarmType = CursorUtil.getColumnIndexOrThrow(_cursor, "alarmType");
          final int _cursorIndexOfTimerMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timerMinutes");
          final List<TodayAlarmEntity> _result = new ArrayList<TodayAlarmEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TodayAlarmEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
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
            final boolean _tmpIsTodayOnly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsTodayOnly);
            _tmpIsTodayOnly = _tmp_1 != 0;
            final int _tmpOriginalHour;
            _tmpOriginalHour = _cursor.getInt(_cursorIndexOfOriginalHour);
            final int _tmpOriginalMinute;
            _tmpOriginalMinute = _cursor.getInt(_cursorIndexOfOriginalMinute);
            final String _tmpAlarmType;
            _tmpAlarmType = _cursor.getString(_cursorIndexOfAlarmType);
            final int _tmpTimerMinutes;
            _tmpTimerMinutes = _cursor.getInt(_cursorIndexOfTimerMinutes);
            _item = new TodayAlarmEntity(_tmpId,_tmpDate,_tmpHour,_tmpMinute,_tmpEventName,_tmpIsEnabled,_tmpIsTodayOnly,_tmpOriginalHour,_tmpOriginalMinute,_tmpAlarmType,_tmpTimerMinutes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
