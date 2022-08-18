package com.yele.downloadlib.policy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DownloadHelper extends SQLiteOpenHelper {

    private final String CREATE_LOAD_THREAD = "create table load_thread(" +
            "_id integer primary key autoincrement," +
            "thread_id integer," +      // 线程ID
            "url text," +               // 需要下载的地址
            "local text," +             // 保存的地址
            "start_index integer," +    // 分到的目标段的起始坐标
            "end_index integer," +      // 分到的目标段的结束坐标
            "size integer," +           // 目标下载的大小
            "finished integer);";       // 目标下载完成的大小


    private final String CREATE_LOAD_FILE = "create table load_files(" +
            "_id integer primary key autoincrement," +
            "url text," +               // 需要下载的地址
            "file_name text," +         // 文件的名称
            "local text," +             // 保存的地址
            "size integer," +           // 目标的大小
            "finished integer," +       // 目标当前下载的大小
            "md5 text);";               // 目标下载的MD5


    public DownloadHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOAD_THREAD);
        db.execSQL(CREATE_LOAD_FILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
