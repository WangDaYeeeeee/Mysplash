package com.wangdaye.mysplash._common.utils.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;

import java.util.List;

/**
 * Download helper.
 * */

public class DownloadHelper {
    // widget
    private DownloadManager downloadManager;

    // data
    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;
    public static final int COLLECTION_TYPE = 4;

    /** <br> life cycle. */

    private DownloadHelper(Context c) {
        this.downloadManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /** <br> data. */

    // photo.

    public void addMission(Context c, Photo p, int type) {
        if (FileUtils.createDownloadPath(c)) {
            DownloadMissionEntity entity = new DownloadMissionEntity(p, type);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                    .setTitle(entity.getRealTitle())
                    .setDescription(c.getString(R.string.feedback_downloading))
                    .setDestinationInExternalPublicDir(
                            Mysplash.DOWNLOAD_PATH,
                            entity.title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
            request.allowScanningByMediaScanner();

            entity.missionId = downloadManager.enqueue(request);
            DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

            NotificationUtils.showSnackbar(
                    c.getString(R.string.feedback_download_start),
                    Snackbar.LENGTH_SHORT);
        }
    }

    public void addMission(Context c, Collection collection) {
        if (FileUtils.createDownloadPath(c)) {
            DownloadMissionEntity entity = new DownloadMissionEntity(collection);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                    .setTitle(entity.getRealTitle())
                    .setDescription(c.getString(R.string.feedback_downloading))
                    .setDestinationInExternalPublicDir(
                            Mysplash.DOWNLOAD_PATH,
                            entity.title + Mysplash.DOWNLOAD_COLLECTION_FORMAT);
            request.allowScanningByMediaScanner();

            entity.missionId = downloadManager.enqueue(request);
            DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

            NotificationUtils.showSnackbar(
                    c.getString(R.string.feedback_download_start),
                    Snackbar.LENGTH_SHORT);
        }
    }

    private long addMission(Context c, DownloadMissionEntity entity) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                .setTitle(entity.getRealTitle())
                .setDescription(c.getString(R.string.feedback_downloading))
                .setDestinationInExternalPublicDir(
                        Mysplash.DOWNLOAD_PATH,
                        entity.title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
        request.allowScanningByMediaScanner();

        entity.missionId = downloadManager.enqueue(request);
        DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

        NotificationUtils.showSnackbar(
                c.getString(R.string.feedback_download_start),
                Snackbar.LENGTH_SHORT);

        return entity.missionId;
    }

    @Nullable
    public DownloadMissionEntity restartMission(Context c, long missionId) {
        DownloadMissionEntity entity = removeMissionAndGetMission(c, missionId);
        if (entity != null) {
            entity.missionId = addMission(c, entity);
            entity.result = DownloadMissionEntity.RESULT_DOWNLOADING;
        }
        return entity;
    }

    public void removeMission(Context c, long id) {
        downloadManager.remove(id);
        DatabaseHelper.getInstance(c).deleteDownloadEntity(id);
    }

    @Nullable
    private DownloadMissionEntity removeMissionAndGetMission(Context c, long id) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(id);
        removeMission(c, id);
        return entity;
    }

    public void clearMission(Context c, List<DownloadMissionEntity> entityList) {
        for (int i = 0; i < entityList.size(); i ++) {
            if (entityList.get(i).result == DownloadMissionEntity.RESULT_DOWNLOADING) {
                downloadManager.remove(entityList.get(i).missionId);
            }
        }
        DatabaseHelper.getInstance(c).clearDownloadEntity();
    }

    // option.

    @Nullable
    public Cursor getMissionCursor(long id) {
        Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(id));
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }

    public static boolean isMissionSuccess(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                == DownloadManager.STATUS_SUCCESSFUL;
    }

    public static boolean isMissionFailed(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                == DownloadManager.STATUS_FAILED;
    }

    public static float getMissionProcess(Cursor cursor) {
        long soFar = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        long total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        return (int) (100.0 * soFar / total);
    }

    /** <br> singleton. */

    private static DownloadHelper instance;

    public static DownloadHelper getInstance(Context c) {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper(c);
                }
            }
        }
        return instance;
    }
}
