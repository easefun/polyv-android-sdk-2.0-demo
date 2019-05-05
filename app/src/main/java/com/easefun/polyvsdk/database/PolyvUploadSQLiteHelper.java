package com.easefun.polyvsdk.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easefun.polyvsdk.bean.PolyvUploadInfo;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PolyvUploadSQLiteHelper extends SQLiteOpenHelper{
    private static PolyvUploadSQLiteHelper sqLiteHelper;
    private static final String DATABASENAME = "uploadlist.db";
    private static final int VERSION = 6;

    private static final Executor SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();

    public static PolyvUploadSQLiteHelper getInstance(Context context) {
        if (sqLiteHelper == null) {
            synchronized (PolyvUploadSQLiteHelper.class) {
                if (sqLiteHelper == null)
                    sqLiteHelper = new PolyvUploadSQLiteHelper(context.getApplicationContext(), DATABASENAME, null, VERSION);
            }
        }
        return sqLiteHelper;
    }

    private PolyvUploadSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists uploadlist(title varchar(100),filepath varchar(120),desc varchar(20),filesize int,percent int default 0,total int default 0,cataid varchar(20),primary key (filepath))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists uploadlist");
        onCreate(db);
    }

    /**
     * 添加上传信息至数据库
     * @param info
     */
    public void insert(final PolyvUploadInfo info) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "insert into uploadlist(title,desc,filesize,filepath,cataid) values(?,?,?,?,?)";
                db.execSQL(sql, new Object[] {info.getTitle(), info.getDesc(), info.getFilesize(),
                        info.getFilepath(),info.getCataid() });
            }
        });
    }

    /**
     * 移除上传信息从数据库中
     * @param info
     */
    public void delete(final PolyvUploadInfo info) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "delete from uploadlist where filepath=?";
                db.execSQL(sql, new Object[] { info.getFilepath() });
            }
        });
    }

    /**
     * 更新上传的分类id至数据库
     * @param info
     */
    public void updateCataid(final PolyvUploadInfo info) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "update uploadlist set cataid=? where filepath=?";
                db.execSQL(sql, new Object[]{ info.getCataid(), info.getFilepath() });
            }
        });
    }

    /**
     * 更新上传的进度至数据库
     * @param info
     * @param percent
     * @param total
     */
    public void update(final PolyvUploadInfo info, final long percent, final long total) {
        SINGLE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String sql = "update uploadlist set percent=?,total=? where filepath=?";
                db.execSQL(sql, new Object[]{percent, total, info.getFilepath()});
            }
        });
    }

    /**
     * 判断该上传信息是否已以添加过
     * @param info
     * @return
     */
    public boolean isAdd(PolyvUploadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select filepath ,title,desc from uploadlist where filepath=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[] { info.getFilepath() });
            return cursor.getCount() == 1 ? true : false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * 获取所有的上传信息对象
     * @return
     */
    public LinkedList<PolyvUploadInfo> getAll() {
        LinkedList<PolyvUploadInfo> infos = new LinkedList<PolyvUploadInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select title,filepath,desc,filesize,percent,total,cataid from uploadlist";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String filepath = cursor.getString(cursor.getColumnIndex("filepath"));
                String desc = cursor.getString(cursor.getColumnIndex("desc"));
                long filesize = cursor.getLong(cursor.getColumnIndex("filesize"));
                long percent = cursor.getInt(cursor.getColumnIndex("percent"));
                long total = cursor.getInt(cursor.getColumnIndex("total"));
                String cataid = cursor.getString(cursor.getColumnIndex("cataid"));
                PolyvUploadInfo info = new PolyvUploadInfo(title, desc, filesize, filepath, cataid);
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
