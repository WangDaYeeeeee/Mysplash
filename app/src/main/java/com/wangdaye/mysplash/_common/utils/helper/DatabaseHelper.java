package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.DaoMaster;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;

import java.util.List;

/**
 * Database helper.
 * */

class DatabaseHelper {
    // widget
    private DaoMaster.DevOpenHelper openHelper;

    // data
    private static final String BD_NAME = "Mysplash_db";

    /** <br> life cycle. */

    private DatabaseHelper(Context c) {
        openHelper = new DaoMaster.DevOpenHelper(c, BD_NAME, null);
    }

    /** <br> data. */

    void writeDownloadEntity(DownloadMissionEntity entity) {
        deleteDownloadEntity(entity);
        new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .insert(entity);
    }

    void deleteDownloadEntity(DownloadMissionEntity entity) {
        new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .delete(entity);
    }

    void updateDownloadEntity(DownloadMissionEntity entity) {
        new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .update(entity);
    }

    List<DownloadMissionEntity> readDownloadEntity() {
        return new DaoMaster(openHelper.getReadableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .list();
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
