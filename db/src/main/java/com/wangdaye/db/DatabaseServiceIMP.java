package com.wangdaye.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.component.service.DatabaseService;
import com.wangdaye.db.entitiy.DaoMaster;
import com.wangdaye.db.entitiy.DownloadMissionEntity;
import com.wangdaye.db.entitiy.WallpaperSource;
import com.wangdaye.db.entitiy.WallpaperSourceDao;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper.
 *
 * A helper class that makes the operation of database easier.
 *
 * */

public class DatabaseServiceIMP implements DatabaseService {

    private static volatile DatabaseServiceIMP instance;

    public static DatabaseServiceIMP getInstance(Context c) {
        if (instance == null) {
            synchronized (DatabaseServiceIMP.class) {
                if (instance == null) {
                    instance = new DatabaseServiceIMP(c);
                }
            }
        }
        return instance;
    }

    private MysplashOpenHelper openHelper;

    private static final String BD_NAME = "Mysplash_db";

    private DatabaseServiceIMP(Context c) {
        openHelper = new MysplashOpenHelper(c, BD_NAME, null);
    }

    // download task.

    @Override
    public void writeDownloadTask(DownloadTask task) {
        DownloadMissionEntity.insertDownloadEntity(
                openHelper.getWritableDatabase(), new DownloadMissionEntity(task));
    }

    @Override
    public void deleteDownloadTask(long missionId) {
        DownloadMissionEntity.deleteDownloadEntity(openHelper.getWritableDatabase(), missionId);
    }

    @Override
    public void clearDownloadTask() {
        DownloadMissionEntity.clearDownloadEntity(openHelper.getWritableDatabase());
    }

    @Override
    public void updateDownloadTask(DownloadTask task) {
        DownloadMissionEntity.updateDownloadEntity(
                openHelper.getWritableDatabase(), new DownloadMissionEntity(task));
    }

    @Override
    public List<DownloadTask> readDownloadTaskList() {
        return toDownloadTaskList(
                DownloadMissionEntity.readDownloadEntityList(openHelper.getReadableDatabase())
        );
    }

    @Override
    public List<DownloadTask> readDownloadTaskList(@DownloadTask.DownloadResultRule int result) {
        return toDownloadTaskList(
                DownloadMissionEntity.readDownloadEntityList(openHelper.getReadableDatabase(), result)
        );
    }

    @Override
    public int readDownloadTaskCount(@DownloadTask.DownloadResultRule int result) {
        return DownloadMissionEntity.readDownloadEntityCount(openHelper.getReadableDatabase(), result);
    }

    @Nullable
    @Override
    public DownloadTask readDownloadTask(long missionId) {
        return toDownloadTask(
                DownloadMissionEntity.searchDownloadEntity(openHelper.getReadableDatabase(), missionId)
        );
    }

    @Nullable
    @Override
    public DownloadTask readDownloadingTask(String title) {
        return toDownloadTask(
                DownloadMissionEntity.searchDownloadingEntity(openHelper.getReadableDatabase(), title)
        );
    }

    @Override
    public int readDownloadingTaskCount(String title) {
        return DownloadMissionEntity.searchDownloadingEntityCount(openHelper.getReadableDatabase(), title);
    }

    @NonNull
    private List<DownloadTask> toDownloadTaskList(@NonNull List<DownloadMissionEntity> entityList) {
        List<DownloadTask> taskList = new ArrayList<>(entityList.size());
        for (DownloadMissionEntity e : entityList) {
            taskList.add(e.toDownloadTask());
        }
        return taskList;
    }

    @Nullable
    private DownloadTask toDownloadTask(@Nullable DownloadMissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.toDownloadTask();
    }

    // muzie wallpaper source.

    @Override
    public void writeMuzeiWallpaperSource(MuzeiWallpaperSource source) {
        WallpaperSource.insertWallpaperSource(
                openHelper.getWritableDatabase(), new WallpaperSource(source));
    }

    @Override
    public void writeMuzeiWallpaperSource(List<MuzeiWallpaperSource> list) {
        List<WallpaperSource> sourceList = new ArrayList<>();
        for (MuzeiWallpaperSource s : list) {
            sourceList.add(new WallpaperSource(s));
        }

        WallpaperSource.insertWallpaperSource(openHelper.getWritableDatabase(), sourceList);
    }

    @Override
    public void deleteMuzeiWallpaperSource(long collectionId) {
        WallpaperSource.deleteWallpaperSource(openHelper.getWritableDatabase(), collectionId);
    }

    @Override
    public void clearMuzeiWallpaperSource() {
        WallpaperSource.clearWallpaperSource(openHelper.getWritableDatabase());
    }

    @Override
    public void updateMuzeiWallpaperSource(MuzeiWallpaperSource source) {
        WallpaperSource.updateWallpaperSource(
                openHelper.getWritableDatabase(), new WallpaperSource(source));
    }

    @Override
    public List<MuzeiWallpaperSource> readMuzeiWallpaperSourceList() {
        return toMuzeiWallpaperSourceList(
                WallpaperSource.readWallpaperSourceList(openHelper.getReadableDatabase())
        );
    }

    @Nullable
    @Override
    public MuzeiWallpaperSource readMuzeiWallpaperSource(long collectionId) {
        return toMuzeiWallpaperSource(
                WallpaperSource.searchWallpaperSource(openHelper.getReadableDatabase(), collectionId)
        );
    }

    @NonNull
    private List<MuzeiWallpaperSource> toMuzeiWallpaperSourceList(@NonNull List<WallpaperSource> sourceList) {
        List<MuzeiWallpaperSource> muzeiSourceList = new ArrayList<>(sourceList.size());
        for (WallpaperSource s : sourceList) {
            muzeiSourceList.add(s.toMuzeiWallpaperSource());
        }
        return muzeiSourceList;
    }

    @Nullable
    private MuzeiWallpaperSource toMuzeiWallpaperSource(@Nullable WallpaperSource source) {
        if (source == null) {
            return null;
        }
        return source.toMuzeiWallpaperSource();
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