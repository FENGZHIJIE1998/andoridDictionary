package com.example.myfinalwork.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 历史记录缓存Dao层
 */
public class HistorySearchDao extends SQLiteOpenHelper {
    public static final String CREATE_HISTORY_WORD = "create table history_word ("
            + "id integer primary key autoincrement, "
            + "word text ,"
            + "UNIQUE (word)"
            + ")";

    private Context mContext;

    public HistorySearchDao(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORY_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_HISTORY_WORD);
    }
}
