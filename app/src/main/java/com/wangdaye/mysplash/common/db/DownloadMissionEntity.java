package com.wangdaye.mysplash.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.table.DaoMaster;
import com.wangdaye.mysplash.common.data.table.DownloadMissionEntityDao;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
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

    @DownloaderService.DownloadTypeRule
    public int downloadType;

    @DownloaderService.DownloadResultRule
    public int result;

    public DownloadMissionEntity(Context context,
                                 @NonNull Photo p,
                                 @DownloaderService.DownloadTypeRule int type) {
        this.title = p.id;
        this.photoUri = p.getRegularSizeUrl(context);
        switch (SettingsOptionManager.getInstance(context).getDownloadScale()) {
            case "tiny":
                this.downloadUrl = p.getWallpaperSizeUrl(context);
                break;

            case "compact":
                this.downloadUrl = p.urls.full;
                break;

            default:
                this.downloadUrl = p.urls.raw;
                break;
        }
        this.downloadType = type;
        this.result = DownloaderService.RESULT_DOWNLOADING;
    }

    public DownloadMissionEntity(Collection c) {
        this.title = String.valueOf(c.id);
        this.photoUri = c.cover_photo.urls.regular;
        this.downloadUrl = c.links.download;
        this.downloadType = DownloaderService.COLLECTION_TYPE;
        this.result = DownloaderService.RESULT_DOWNLOADING;
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

    // data.

    /**
     * Get the file path of the downloaded file.
     *
     * @return file path.
     * */
    public String getFilePath() {
        return getParentPath() + getFileName();
    }

    /**
     * Get the file path of the downloaded file's parent.
     *
     * @return parent path.
     * */
    public String getParentPath() {
        return Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH;
    }

    /**
     * Get the file name.
     *
     * @return file name.
     * */
    public String getFileName() {
        return title + getFormat();
    }

    /**
     * Get the title of downloading notification description title text.
     *
     * @return notification description title text.
     * */
    public String getNotificationTitle() {
        if (downloadType == DownloaderService.COLLECTION_TYPE) {
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
        if (downloadType == DownloaderService.COLLECTION_TYPE) {
            return Mysplash.DOWNLOAD_COLLECTION_FORMAT;
        } else {
            return Mysplash.DOWNLOAD_PHOTO_FORMAT;
        }
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

    public static List<DownloadMissionEntity> readDownloadEntityList(SQLiteDatabase database,
                                                                     @DownloaderService.DownloadTypeRule int result) {
        return new DaoMaster(database)
                .newSession()
                .getDownloadMissionEntityDao()
                .queryBuilder()
                .where(DownloadMissionEntityDao.Properties.Result.eq(result))
                .list();
    }

    public static int readDownloadEntityCount(SQLiteDatabase database,
                                              @DownloaderService.DownloadTypeRule int result) {
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
                        DownloadMissionEntityDao.Properties.Result.eq(DownloaderService.RESULT_DOWNLOADING))
                .list();
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
                        DownloadMissionEntityDao.Properties.Result.eq(DownloaderService.RESULT_DOWNLOADING))
                .count();
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
