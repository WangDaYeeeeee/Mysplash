package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.DaoMaster;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntityDao;

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
        List<DownloadMissionEntity> missionList = searchDownloadEntity(entity);
        DownloadMissionEntityDao dao = new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao();
        for (DownloadMissionEntity e : missionList) {
            dao.delete(e);
        }
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

    private List<DownloadMissionEntity> searchDownloadEntity(DownloadMissionEntity entity) {
        return new DaoMaster(openHelper.getReadableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.PhotoId.eq(entity.photoId))
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
