package com.wangdaye.mysplash.common.utils.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wangdaye.mysplash.common.data.entity.table.DaoMaster;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSourceDao;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * Database helper.
 *
 * A helper class that makes the operation of database easier.
 *
 * */

public class DatabaseHelper {

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context c) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(c);
                }
            }
        }
        return instance;
    }

    private MysplashOpenHelper openHelper;

    private static final String BD_NAME = "Mysplash_db";

    private DatabaseHelper(Context c) {
        openHelper = new MysplashOpenHelper(c, BD_NAME, null);
    }

    // download entity.

    public void writeDownloadEntity(DownloadMissionEntity entity) {
        DownloadMissionEntity.insertDownloadEntity(openHelper.getWritableDatabase(), entity);
    }

    public void deleteDownloadEntity(long missionId) {
        DownloadMissionEntity.deleteDownloadEntity(openHelper.getWritableDatabase(), missionId);
    }

    public void clearDownloadEntity() {
        DownloadMissionEntity.clearDownloadEntity(openHelper.getWritableDatabase());
    }

    public void updateDownloadEntity(DownloadMissionEntity entity) {
        DownloadMissionEntity.updateDownloadEntity(openHelper.getWritableDatabase(), entity);
    }

    public List<DownloadMissionEntity> readDownloadEntityList() {
        return DownloadMissionEntity.readDownloadEntityList(openHelper.getReadableDatabase());
    }

    public List<DownloadMissionEntity> readDownloadEntityList(int result) {
        return DownloadMissionEntity.readDownloadEntityList(openHelper.getReadableDatabase(), result);
    }

    @Nullable
    public DownloadMissionEntity readDownloadEntity(long missionId) {
        return DownloadMissionEntity.searchDownloadEntity(openHelper.getReadableDatabase(), missionId);
    }

    @Nullable
    public DownloadMissionEntity readDownloadingEntity(String title) {
        return DownloadMissionEntity.searchDownloadingEntity(openHelper.getReadableDatabase(), title);
    }

    public int readDownloadingEntityCount(String title) {
        return DownloadMissionEntity.searchDownloadingEntityCount(openHelper.getReadableDatabase(), title);
    }

    // wallpaper source.

    public void writeWallpaperSource(WallpaperSource source) {
        WallpaperSource.insertWallpaperSource(openHelper.getWritableDatabase(), source);
    }

    public void writeWallpaperSource(List<WallpaperSource> list) {
        WallpaperSource.insertWallpaperSource(openHelper.getWritableDatabase(), list);
    }

    public void deleteWallpaperSource(long collectionId) {
        WallpaperSource.deleteWallpaperSource(openHelper.getWritableDatabase(), collectionId);
    }

    public void clearWallpaperSource() {
        WallpaperSource.clearWallpaperSource(openHelper.getWritableDatabase());
    }

    public void updateWallpaperSource(WallpaperSource source) {
        WallpaperSource.updateWallpaperSource(openHelper.getWritableDatabase(), source);
    }

    public List<WallpaperSource> readWallpaperSourceList() {
        return WallpaperSource.readWallpaperSourceList(openHelper.getReadableDatabase());
    }

    @Nullable
    public WallpaperSource readWallpaperSource(long collectionId) {
        return WallpaperSource.searchWallpaperSource(openHelper.getReadableDatabase(), collectionId);
    }
}

class MysplashOpenHelper extends DaoMaster.DevOpenHelper {

    private static final int VERSION_ADD_WALLPAPER_SOURCE = 17;

    MysplashOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        if (newVersion >= VERSION_ADD_WALLPAPER_SOURCE) {
            if (oldVersion < VERSION_ADD_WALLPAPER_SOURCE) {
                WallpaperSourceDao.createTable(db, true);
            }
        } else {
            super.onUpgrade(db, oldVersion, newVersion);
        }
    }
}