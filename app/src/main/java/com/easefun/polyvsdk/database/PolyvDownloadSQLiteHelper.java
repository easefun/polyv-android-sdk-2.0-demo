package com.easefun.polyvsdk.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easefun.polyvsdk.bean.PolyvDownloadInfo;

import java.util.LinkedList;

public class PolyvDownloadSQLiteHelper extends SQLiteOpenHelper {
    private static PolyvDownloadSQLiteHelper sqLiteHelper;
    private static final String DATABASENAME = "downloadlist.db";
    private static final int VERSION = 6;

    public static PolyvDownloadSQLiteHelper getInstance(Context context) {
        if (sqLiteHelper == null) {
            synchronized (PolyvDownloadSQLiteHelper.class) {
                if (sqLiteHelper == null)
                    sqLiteHelper = new PolyvDownloadSQLiteHelper(context.getApplicationContext(), DATABASENAME, null, VERSION);
            }
        }
        return sqLiteHelper;
    }

    private PolyvDownloadSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists downloadlist(vid varchar(20),title varchar(100),duration varchar(20),filesize int,bitrate int,percent int default 0,total int default 0,primary key (vid, bitrate))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists downloadlist");
        onCreate(db);
    }

    /**
     * 添加下载信息至数据库
     * @param info
     */
    public void insert(PolyvDownloadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "insert into downloadlist(vid,title,duration,filesize,bitrate) values(?,?,?,?,?)";
        db.execSQL(sql, new Object[]{info.getVid(), info.getTitle(), info.getDuration(),
                info.getFilesize(), info.getBitrate()});
    }

    /**
     * 移除下载信息从数据库中
     * @param info
     */
    public void delete(PolyvDownloadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete from downloadlist where vid=? and bitrate=?";
        db.execSQL(sql, new Object[]{info.getVid(), info.getBitrate()});
    }

    /**
     * 更新下载的进度至数据库
     * @param info
     * @param percent
     * @param total
     */
    public void update(PolyvDownloadInfo info, long percent, long total) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "update downloadlist set percent=?,total=? where vid=? and bitrate=?";
        db.execSQL(sql, new Object[]{percent, total, info.getVid(), info.getBitrate()});
    }

    /**
     * 判断该下载信息是否已以添加过
     * @param info
     * @return
     */
    public boolean isAdd(PolyvDownloadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select vid ,duration,filesize,bitrate from downloadlist where vid=? and bitrate=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{info.getVid(), info.getBitrate() + ""});
            return cursor.getCount() == 1 ? true : false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * 获取所有的下载信息对象
     * @return
     */
    public LinkedList<PolyvDownloadInfo> getAll() {
        LinkedList<PolyvDownloadInfo> infos = new LinkedList<PolyvDownloadInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select vid,title,duration,filesize,bitrate,percent,total from downloadlist";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String vid = cursor.getString(cursor.getColumnIndex("vid"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                long filesize = cursor.getInt(cursor.getColumnIndex("filesize"));
                int bitrate = cursor.getInt(cursor.getColumnIndex("bitrate"));
                long percent = cursor.getInt(cursor.getColumnIndex("percent"));
                long total = cursor.getInt(cursor.getColumnIndex("total"));
                PolyvDownloadInfo info = new PolyvDownloadInfo(vid, duration, filesize, bitrate, title);
                info.setPercent(percent);
                info.setTotal(total);
                infos.addLast(info);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return infos;
    }
}
