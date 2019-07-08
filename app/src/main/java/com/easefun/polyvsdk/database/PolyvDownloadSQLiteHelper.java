package com.easefun.polyvsdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.log.PolyvCommonLog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PolyvDownloadSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "PolyvDownloadSQLiteHelp";
    private static PolyvDownloadSQLiteHelper sqLiteHelper;
    private static final String DATABASENAME = "downloadlist.db";
    private static final String TABLE_NAME = "downloadlist";
    private static final int VERSION = 7;

    private static final Executor SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();

    public static PolyvDownloadSQLiteHelper getInstance(Context context) {
        if (sqLiteHelper == null) {
            synchronized (PolyvDownloadSQLiteHelper.class) {
                if (sqLiteHelper == null) {
                    //如果开启多账户体系，设置绝对路径
                    File downloadPath = PolyvSDKClient.getInstance().getDownloadDir();
                    sqLiteHelper = new PolyvDownloadSQLiteHelper(context.getApplicationContext(),
                            PolyvSDKClient.getInstance().isMultiDownloadAccount() && downloadPath != null
                                    ? downloadPath.getAbsolutePath() + File.separator + DATABASENAME : DATABASENAME,
                            null, VERSION);
                }
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
                "create table if not exists downloadlist(vid varchar(20),title varchar(100),duration varchar(20),filesize int,bitrate int,fileType int,percent int default 0,total int default 0,primary key (vid, bitrate, fileType))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists downloadlist");
        onCreate(db);
    }

    /**
     * 添加下载信息至数据库
     *
     * @param info
     */
    public void insert(final PolyvDownloadInfo info) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "insert into downloadlist(vid,title,duration,filesize,bitrate, fileType) values(?,?,?,?,?,?)";
                db.execSQL(sql, new Object[]{info.getVid(), info.getTitle(), info.getDuration(),
                        info.getFilesize(), info.getBitrate(), info.getFileType()});
            }
        });
    }

    /**
     * 批量添加数据
     *
     * @param infos
     */
    public void insert(final List<PolyvDownloadInfo> infos) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    db.beginTransaction();
                    for (PolyvDownloadInfo info : infos) {
                        ContentValues contentValues = createValues(info);
                        db.insertWithOnConflict(TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (db != null) {
                        db.endTransaction();
                    }
                }
            }
        });

    }

    private ContentValues createValues(PolyvDownloadInfo info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vid", info.getVid());
        contentValues.put("title", info.getTitle());
        contentValues.put("duration", info.getDuration());
        contentValues.put("filesize", info.getFilesize());
        contentValues.put("bitrate", info.getBitrate());
        contentValues.put("fileType", info.getFileType());
        contentValues.put("percent", info.getPercent());
        contentValues.put("total", info.getTotal());
        return contentValues;
    }

    /**
     * 移除下载信息从数据库中
     *
     * @param info
     */
    public void delete(final PolyvDownloadInfo info) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "delete from downloadlist where vid=? and bitrate=? and fileType=?";
                db.execSQL(sql, new Object[]{info.getVid(), info.getBitrate(), info.getFileType()});
            }
        });
    }

    /**
     * 更新下载的进度至数据库
     *
     * @param info
     * @param percent
     * @param total
     */
    public void update(final PolyvDownloadInfo info, final long percent, final long total) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "update downloadlist set percent=?,total=? where vid=? and bitrate=? and fileType=?";
                db.execSQL(sql, new Object[]{percent, total, info.getVid(), info.getBitrate(), info.getFileType()});
            }
        });
    }

    /**
     * 判断该下载信息是否已以添加过
     *
     * @param info
     * @return
     */
    public boolean isAdd(PolyvDownloadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select vid ,duration,filesize,bitrate,fileType from downloadlist where vid=? and bitrate=? and fileType=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{info.getVid(), info.getBitrate() + "", info.getFileType() + ""});
            return cursor.getCount() == 1 ? true : false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * 获取所有的下载信息对象
     *
     * @return
     */
    public LinkedList<PolyvDownloadInfo> getAll() {
        LinkedList<PolyvDownloadInfo> infos = new LinkedList<PolyvDownloadInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select vid,title,duration,filesize,bitrate,fileType,percent,total from downloadlist";
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
                int fileType = cursor.getInt(cursor.getColumnIndex("fileType"));
                PolyvDownloadInfo info = new PolyvDownloadInfo(vid, duration, filesize, bitrate, title);
                info.setFileType(fileType);
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

    public void release() {
        try {
            if (sqLiteHelper != null) {
                if(sqLiteHelper.getDatabaseName().equals(DATABASENAME)){//原有数据库的数据迁移完成后 需要删除原有数据库
                    PolyvCommonLog.d("db","delete table");
                    sqLiteHelper.getWritableDatabase().delete(TABLE_NAME,null,null);
                }
                sqLiteHelper.close();
                sqLiteHelper = null;
            }
        }catch (Exception e){
            PolyvCommonLog.exception(e);
        }

    }
}
