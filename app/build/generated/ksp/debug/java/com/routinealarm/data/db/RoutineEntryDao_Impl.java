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
public final class RoutineEntryDao_Impl implements RoutineEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoutineEntryEntity> __insertionAdapterOfRoutineEntryEntity;

  private final EntityDeletionOrUpdateAdapter<RoutineEntryEntity> __deletionAdapterOfRoutineEntryEntity;

  private final EntityDeletionOrUpdateAdapter<RoutineEntryEntity> __updateAdapterOfRoutineEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RoutineEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoutineEntryEntity = new EntityInsertionAdapter<RoutineEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `routine_entries` (`id`,`hour`,`minute`,`eventName`,`sortOrder`,`alarmType`,`timerMinutes`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getHour());
        statement.bindLong(3, entity.getMinute());
        statement.bindString(4, entity.getEventName());
        statement.bindLong(5, entity.getSortOrder());
        statement.bindString(6, entity.getAlarmType());
        statement.bindLong(7, entity.getTimerMinutes());
      }
    };
    this.__deletionAdapterOfRoutineEntryEntity = new EntityDeletionOrUpdateAdapter<RoutineEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `routine_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRoutineEntryEntity = new EntityDeletionOrUpdateAdapter<RoutineEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `routine_entries` SET `id` = ?,`hour` = ?,`minute` = ?,`eventName` = ?,`sortOrder` = ?,`alarmType` = ?,`timerMinutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getHour());
        statement.bindLong(3, entity.getMinute());
        statement.bindString(4, entity.getEventName());
        statement.bindLong(5, entity.getSortOrder());
        statement.bindString(6, entity.getAlarmType());
        statement.bindLong(7, entity.getTimerMinutes());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM routine_entries";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RoutineEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRoutineEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RoutineEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoutineEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RoutineEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRoutineEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RoutineEntryEntity>> getAll() {
    final String _sql = "SELECT * FROM routine_entries ORDER BY hour, minute, sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routine_entries"}, new Callable<List<RoutineEntryEntity>>() {
      @Override
      @NonNull
      public List<RoutineEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEventName = CursorUtil.getColumnIndexOrThrow(_cursor, "eventName");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final int _cursorIndexOfAlarmType = CursorUtil.getColumnIndexOrThrow(_cursor, "alarmType");
          final int _cursorIndexOfTimerMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timerMinutes");
          final List<RoutineEntryEntity> _result = new ArrayList<RoutineEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutineEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final String _tmpEventName;
            _tmpEventName = _cursor.getString(_cursorIndexOfEventName);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            final String _tmpAlarmType;
            _tmpAlarmType = _cursor.getString(_cursorIndexOfAlarmType);
            final int _tmpTimerMinutes;
            _tmpTimerMinutes = _cursor.getInt(_cursorIndexOfTimerMinutes);
            _item = new RoutineEntryEntity(_tmpId,_tmpHour,_tmpMinute,_tmpEventName,_tmpSortOrder,_tmpAlarmType,_tmpTimerMinutes);
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
