package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash._common.data.entity.database.DaoMaster;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;

import java.util.List;

/**
 * Database helper.
 * */

public class DatabaseHelper {
    // widget
    private DaoMaster.DevOpenHelper openHelper;

    // data
    private static final String BD_NAME = "Mysplash_db";

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

    public List<DownloadMissionEntity> readDownloadEntity() {
        return DownloadMissionEntity.searchDownloadEntityList(openHelper.getReadableDatabase());
    }

    @Nullable
    public DownloadMissionEntity readDownloadEntity(long missionId) {
        return DownloadMissionEntity.searchDownloadEntity(openHelper.getReadableDatabase(), missionId);
    }

    public int readDownloadEntityCount(String title) {
        return DownloadMissionEntity.searchDownloadEntityCount(openHelper.getReadableDatabase(), title);
    }

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
}
