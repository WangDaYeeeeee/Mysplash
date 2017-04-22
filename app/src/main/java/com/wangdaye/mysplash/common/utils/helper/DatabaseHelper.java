package com.wangdaye.mysplash.common.utils.helper;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.table.DaoMaster;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;

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

    private DaoMaster.DevOpenHelper openHelper;
    private static final String BD_NAME = "Mysplash_db";

    private DatabaseHelper(Context c) {
        openHelper = new DaoMaster.DevOpenHelper(c, BD_NAME, null);
    }

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
}
