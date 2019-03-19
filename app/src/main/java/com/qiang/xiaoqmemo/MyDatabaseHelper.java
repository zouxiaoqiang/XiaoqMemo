package com.qiang.xiaoqmemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author xiaoqiang
 * @date 19-3-18
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_MEMO = "create table memo("
            + "id integer primary key autoincrement, "
            + "date text not null, "
            + "title text not null, "
            + "content text)";

    MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEMO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists memo");
        onCreate(db);
    }
}
