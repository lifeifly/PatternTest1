package com.yele.huht.bluetoothsdklib.policy.downloadlib.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yele.baseapp.utils.StringUtils;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.FileInfo;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

public class DownloadDB {

    private final String DB_NAME = "download.db";

    private final String TABLE_NAME = "load_thread";

    private final String TABLE_NAME_FILE = "load_files";

    private final int VERSION = 1;

    private static DownloadDB downloadDB;

    public static DownloadDB getInstance(Context context) {
        if (downloadDB == null) {
            synchronized (DownloadDB.class) {
                if (downloadDB == null) {
                    downloadDB = new DownloadDB(context);
                }
            }
        }
        return downloadDB;
    }

    private SQLiteDatabase db;

    /**
     * 操作下载数据库的构造体
     * @param context 上下文
     */
    private DownloadDB(Context context) {
        DownloadHelper helper = new DownloadHelper(context, DB_NAME, null, VERSION);
        db = helper.getWritableDatabase();
    }

    /**
     * 是否有当前下载的信息
     *
     * @param info 下载的信息
     * @return 是否数据库包含信息
     */
    private boolean hasLoadFileInfo(FileInfo info) {
        Cursor cursor = db.query(TABLE_NAME_FILE, null, "url = ?",
                new String[]{info.url}, null, null, null);
        boolean has = cursor.moveToFirst();
        cursor.close();
        return has;
    }

    /**
     * 更新下载文件的信息
     * @param info 信息
     */
    public void updateLoadFileInfo(FileInfo info) {
        ContentValues values = new ContentValues();
        values.put("url", info.url);
        values.put("local", info.localPath);
        values.put("size", info.size);
        values.put("file_name", info.fileName);
        values.put("finished", info.finished);
        values.put("md5", info.md5);
        if (hasLoadFileInfo(info)) {
            db.update(TABLE_NAME_FILE, values, "url = ?",
                    new String[]{info.url});
        }else{
            db.insert(TABLE_NAME_FILE, null, values);
        }
    }

    /**
     * 获取当前本地是否已经加载完成了对应的文件
     * @param url 目标地址
     * @return
     */
    public FileInfo getLoadFileInfo(String url) {
        FileInfo fileInfo = null;
        Cursor cursor = db.query(TABLE_NAME_FILE, null, "url = ?",
                new String[]{url}, null, null, null);
        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex("local"));
            long size = cursor.getLong(cursor.getColumnIndex("size"));
            String fileName = cursor.getString(cursor.getColumnIndex("file_name"));
            long finished = cursor.getLong(cursor.getColumnIndex("finished"));
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            fileInfo = new FileInfo();
            fileInfo.url = url;
            fileInfo.localPath = path;
            fileInfo.size = size;
            fileInfo.fileName = fileName;
            fileInfo.finished = finished;
            fileInfo.md5 = md5;
        }
        cursor.close();
        return fileInfo;
    }

    /**
     * 获取当前的下载文件的列表
     * @return 在下载的文件列表
     */
    public List<FileInfo> getLoadFileInfoList() {
        List<FileInfo> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME_FILE, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String path = cursor.getString(cursor.getColumnIndex("local"));
                long size = cursor.getLong(cursor.getColumnIndex("size"));
                String fileName = cursor.getString(cursor.getColumnIndex("file_name"));
                long finished = cursor.getLong(cursor.getColumnIndex("finished"));
                String md5 = cursor.getString(cursor.getColumnIndex("md5"));
                FileInfo info = new FileInfo();
                info.url = url;
                info.localPath = path;
                info.size = size;
                info.fileName = fileName;
                info.finished = finished;
                info.md5 = md5;
                list.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 当前的下载的信息是否已经存在数据库了
     * @param info 下载线程信息
     * @return 是否存在数据库
     */
    private boolean hasThreadInfo(ThreadInfo info){
        if (info == null) {
            return false;
        }
        boolean has;
        Cursor cursor = db.query(TABLE_NAME, null, "url = ? and thread_id = ?"
                , new String[]{info.url, String.valueOf(info.id)}, null, null, null);
        has = cursor.moveToFirst();
        cursor.close();
        return has;
    }

    /**
     * 更新下载信息
     * @param info 下载线程的信息
     */
    public void updateLoadThread(ThreadInfo info) {
        ContentValues values = new ContentValues();
        values.put("url", info.url);
        values.put("thread_id", info.id);
        values.put("local", info.path);
        values.put("start_index", info.startIndex);
        values.put("end_index", info.endIndex);
        values.put("size", info.size);
        values.put("finished", info.finished);
        if (hasThreadInfo(info)) {
            db.update(TABLE_NAME, values, "url = ? and thread_id = ?"
                    , new String[]{info.url, String.valueOf(info.id)});
        }else{
            db.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * 移除指定下载链接的所有下载内容
     * @param url 下载链接
     */
    public void delAppointLoadThread(String url) {
        if (StringUtils.isEmpty(url) || !db.isOpen()) {
            return;
        }
        db.delete(TABLE_NAME, "url = ?", new String[]{url});
    }

    /**
     * 清除指定的下载内容
     * @param info 线程信息
     */
    public void delLoadThread(ThreadInfo info) {
        if (info == null || !db.isOpen()) {
            return;
        }
        db.delete(TABLE_NAME, "url = ? and thread_id = ?",
                new String[]{info.url, String.valueOf(info.id)});
    }

    /**
     * 获取指定的文件下载的下载缓存
     *
     * @param url 目标链接地址
     * @return
     */
    public List<ThreadInfo> getThreads(String url) {
        List<ThreadInfo> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, "url = ?",
                new String[]{url}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("thread_id"));
                String path = cursor.getString(cursor.getColumnIndex("local"));
                long startIndex = cursor.getLong(cursor.getColumnIndex("start_index"));
                long endIndex = cursor.getLong(cursor.getColumnIndex("end_index"));
                long size = cursor.getLong(cursor.getColumnIndex("size"));
                long finished = cursor.getLong(cursor.getColumnIndex("finished"));
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.id = id;
                threadInfo.url = url;
                threadInfo.startIndex = startIndex;
                threadInfo.endIndex = endIndex;
                threadInfo.finished = finished;
                threadInfo.size = size;
                threadInfo.path = path;
                list.add(threadInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 清空对应的下载地址的信息
     *
     * @param url 目标下载地址
     */
    public void dropTreads(String url) {
        if (!db.isOpen()) {
            return;
        }
        db.delete(TABLE_NAME, "url = ?", new String[]{url});
    }

    /**
     * 清空所有的下载信息数据库
     */
    public void dropThreads() {
        db.execSQL("drop table if exists load_thread");
    }

    /**
     * 移除指定的下载文件内容
     * @param localInfo 当前下载的文件内容
     */
    public void delFileInfo(FileInfo localInfo) {
        if (localInfo == null) {
            return;
        }
        db.delete(TABLE_NAME_FILE, "url = ?", new String[]{localInfo.url});
    }
}
