package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash._common.data.entity.table.DaoMaster;
import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;

import java.util.List;

/**
 * Database helper.
 * */

public class DatabaseHelper {
    // widget
    private DaoMaster.DevOpenHelper openHelper;

    // data
    private static final String BD_NAME = "Mysplash_db";

    /** <br> singleton. */

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

    /** <br> life cycle. */

    private DatabaseHelper(Context c) {
        openHelper = new DaoMaster.DevOpenHelper(c, BD_NAME, null);
    }

    /** <br> data. */

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
