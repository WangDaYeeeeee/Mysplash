package com.wangdaye.mysplash._common.utils.helper;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash._common.data.entity.database.DaoMaster;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntityDao;

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

    void writeDownloadEntity(DownloadMissionEntity entity) {
        deleteDownloadEntity(entity.missionId);
        new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .insert(entity);
    }

    public void deleteDownloadEntity(long missionId) {
        DownloadMissionEntity entity = readDownloadEntity(missionId);
        if (entity != null) {
            new DaoMaster(openHelper.getWritableDatabase())
                    .newSession()
                    .getDownloadMissionEntityDao()
                    .delete(entity);
        }
    }

    void clearDownloadEntity() {
        new DaoMaster(openHelper.getWritableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .deleteAll();
    }

    public List<DownloadMissionEntity> readDownloadEntity() {
        return new DaoMaster(openHelper.getReadableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .list();
    }

    @Nullable
    public DownloadMissionEntity readDownloadEntity(long missionId) {
        List<DownloadMissionEntity> entityList = new DaoMaster(openHelper.getReadableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.MissionId.eq(missionId))
                .list();
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        } else {
            return null;
        }
    }

    public int readDownloadEntityCount(String photoId) {
        return new DaoMaster(openHelper.getReadableDatabase())
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.PhotoId.eq(photoId))
                .list()
                .size();
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
