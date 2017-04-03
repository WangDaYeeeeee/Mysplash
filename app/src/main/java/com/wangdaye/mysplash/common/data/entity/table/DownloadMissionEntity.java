package com.wangdaye.mysplash.common.data.entity.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

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
public class DownloadMissionEntity {
    @Id
    public long missionId;

    public String title;
    public String photoUri;

    public String downloadUrl;

    @DownloadHelper.DownloadTypeRule
    public int downloadType;

    @DownloadHelper.DownloadResultRule
    public int result;



    public DownloadMissionEntity(Context context,
                                 @NonNull Photo p,
                                 @DownloadHelper.DownloadTypeRule int type) {
        this.title = p.id;
        this.photoUri = p.urls.regular;
        if (SettingsOptionManager.getInstance(context)
                .getDownloadScale()
                .equals("compact")) {
            this.downloadUrl = p.urls.full;
        } else {
            this.downloadUrl = p.urls.raw;
        }
        this.downloadType = type;
        this.result = DownloadHelper.RESULT_DOWNLOADING;
    }

    public DownloadMissionEntity(Collection c) {
        this.title = String.valueOf(c.id);
        this.photoUri = c.cover_photo.urls.regular;
        this.downloadUrl = c.links.download;
        this.downloadType = DownloadHelper.COLLECTION_TYPE;
        this.result = DownloadHelper.RESULT_DOWNLOADING;
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

    /** <br> data. */

    /**
     * Get the file path of the downloaded file.
     *
     * @return file path.
     * */
    public String getFilePath() {
        return Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + title + getFormat();
    }

    /**
     * Get the real title of downloading notification description title text.
     *
     * @return notification description title text.
     * */
    public String getRealTitle() {
        if (downloadType == DownloadHelper.COLLECTION_TYPE) {
            return "COLLECTION #" + title;
        } else {
            return title;
        }
    }

    /**
     * Get the downloaded file's format.
     *
     * @return format of downloaded file.
     * */
    @Mysplash.DownloadFormatRule
    public String getFormat() {
        if (downloadType == DownloadHelper.COLLECTION_TYPE) {
            return Mysplash.DOWNLOAD_COLLECTION_FORMAT;
        } else {
            return Mysplash.DOWNLOAD_PHOTO_FORMAT;
        }
    }

    // insert.

    public static void insertDownloadEntity(SQLiteDatabase database,
                                            @NonNull DownloadMissionEntity entity) {
        DownloadMissionEntity e = searchDownloadEntity(database, entity.missionId);
        if (e != null) {
            deleteDownloadEntity(database, e.missionId);
        }
        new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .insert(entity);
    }

    // delete.

    public static void deleteDownloadEntity(SQLiteDatabase database, long missionId) {
        DownloadMissionEntity entity = searchDownloadEntity(database, missionId);
        if (entity != null) {
            new DaoMaster(database)
                    .newSession()
                    .getDownloadMissionEntityDao()
                    .delete(entity);
        }
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

    public static List<DownloadMissionEntity> readDownloadEntityList(SQLiteDatabase database,
                                                                     @DownloadHelper.DownloadTypeRule int result) {
        return new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.Result.eq(result))
                .list();
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
                .where(DownloadMissionEntityDao.Properties.Title.eq(title))
                .where(DownloadMissionEntityDao.Properties.Result.eq(DownloadHelper.RESULT_DOWNLOADING))
                .list();
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        } else {
            return null;
        }
    }

    public static int searchDownloadingEntityCount(SQLiteDatabase database, String photoId) {
        return new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.Title.eq(photoId))
                .where(DownloadMissionEntityDao.Properties.Result.eq(DownloadHelper.RESULT_DOWNLOADING))
                .list()
                .size();
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
