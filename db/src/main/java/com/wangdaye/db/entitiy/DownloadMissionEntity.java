package com.wangdaye.db.entitiy;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.i.Downloadable;
import com.wangdaye.base.DownloadTask;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Download mission entity.
 *
 * The SQLite database table entity class for download missions.
 *
 * */

@Entity
public class DownloadMissionEntity
        implements Downloadable {
    @Id public long missionId;

    public String title;
    public String photoUri;
    public String downloadUrl;

    public int downloadType;
    public int result;

    public DownloadMissionEntity(DownloadTask task) {
        this.missionId = task.taskId;
        this.title = task.title;
        this.photoUri = task.photoUri;
        this.downloadUrl = task.downloadUrl;
        this.downloadType = task.downloadType;
        this.result = task.result;
    }

    @Generated(hash = 616575969)
    public DownloadMissionEntity(long missionId, String title, String photoUri, String downloadUrl,
            int downloadType, int result) {
        this.missionId = missionId;
        this.title = title;
        this.photoUri = photoUri;
        this.downloadUrl = downloadUrl;
        this.downloadType = downloadType;
        this.result = result;
    }

    @Generated(hash = 1239001066)
    public DownloadMissionEntity() {
    }

    public DownloadTask toDownloadTask() {
        return new DownloadTask(missionId, title, photoUri, downloadUrl, downloadType, result);
    }

    // insert.

    public static void insertDownloadEntity(SQLiteDatabase database,
                                            @NonNull DownloadMissionEntity entity) {
        deleteDownloadEntity(database, entity.missionId);
        new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .insert(entity);
    }

    // delete.

    public static void deleteDownloadEntity(SQLiteDatabase database, long missionId) {
        new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .deleteByKey(missionId);
    }

    public static void clearDownloadEntity(SQLiteDatabase database) {
        new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .deleteAll();
    }

    // update.

    public static void updateDownloadEntity(SQLiteDatabase database,
                                            @NonNull DownloadMissionEntity entity) {
        new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .update(entity);
    }

    // search.

    public static List<DownloadMissionEntity> readDownloadEntityList(SQLiteDatabase database) {
        return new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .list();
    }

    public static List<DownloadMissionEntity> readDownloadEntityList(SQLiteDatabase database, int result) {
        return new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.Result.eq(result))
                .list();
    }

    public static int readDownloadEntityCount(SQLiteDatabase database, int result) {
        return (int) new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.Result.eq(result))
                .count();
    }

    @Nullable
    public static DownloadMissionEntity searchDownloadEntity(SQLiteDatabase database, long missionId) {
        List<DownloadMissionEntity> entityList = new DaoMaster(database)
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

    @Nullable
    public static DownloadMissionEntity searchDownloadingEntity(SQLiteDatabase database, String title) {
        List<DownloadMissionEntity> entityList = new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(
                        DownloadMissionEntityDao.Properties.Title.eq(title),
                        DownloadMissionEntityDao.Properties.Result.eq(DownloadTask.RESULT_DOWNLOADING)
                ).list();
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        } else {
            return null;
        }
    }

    public static int searchDownloadingEntityCount(SQLiteDatabase database, String photoId) {
        return (int) new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(
                        DownloadMissionEntityDao.Properties.Title.eq(photoId),
                        DownloadMissionEntityDao.Properties.Result.eq(DownloadTask.RESULT_DOWNLOADING)
                ).count();
    }

    public long getMissionId() {
        return this.missionId;
    }

    public void setMissionId(long missionId) {
        this.missionId = missionId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUri() {
        return this.photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getDownloadType() {
        return this.downloadType;
    }

    public void setDownloadType(int downloadType) {
        this.downloadType = downloadType;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
