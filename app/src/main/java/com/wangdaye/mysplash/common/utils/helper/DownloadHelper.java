package com.wangdaye.mysplash.common.utils.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.utils.FileUtils;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.List;

/**
 * Download helper.
 *
 * A helper class that makes operations of {@link DownloadManager} easier.
 *
 * */

public class DownloadHelper {

    private static DownloadHelper instance;

    public static DownloadHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper(context);
                }
            }
        }
        return instance;
    }

    private DownloadManager downloadManager;

    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;
    public static final int COLLECTION_TYPE = 4;
    @IntDef({
            DownloadHelper.DOWNLOAD_TYPE,
            DownloadHelper.SHARE_TYPE,
            DownloadHelper.WALLPAPER_TYPE,
            DownloadHelper.COLLECTION_TYPE,
    })
    public @interface DownloadTypeRule {}

    public static final int RESULT_SUCCEED = 1;
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_DOWNLOADING = 0;
    @IntDef({
            DownloadHelper.RESULT_DOWNLOADING,
            DownloadHelper.RESULT_SUCCEED,
            DownloadHelper.RESULT_FAILED})
    public @interface DownloadResultRule {}

    private DownloadHelper(Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    // insert.

    public void addMission(Context c, Photo p, int type) {
        if (FileUtils.createDownloadPath(c)) {
            addMission(c, new DownloadMissionEntity(c, p, type));
        }
    }

    public void addMission(Context c, Collection collection) {
        if (FileUtils.createDownloadPath(c)) {
            addMission(c, new DownloadMissionEntity(collection));
        }
    }

    private long addMission(Context c, DownloadMissionEntity entity) {
        FileUtils.deleteFile(entity);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                .setTitle(entity.getNotificationTitle())
                .setDescription(c.getString(R.string.feedback_downloading))
                .setDestinationInExternalPublicDir(
                        Mysplash.DOWNLOAD_PATH,
                        entity.title + entity.getFormat());
        request.allowScanningByMediaScanner();

        entity.missionId = downloadManager.enqueue(request);
        entity.result = DownloadHelper.RESULT_DOWNLOADING;
        DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

        NotificationHelper.showSnackbar(
                c.getString(R.string.feedback_download_start),
                Snackbar.LENGTH_SHORT);

        return entity.missionId;
    }

    @Nullable
    public DownloadMission restartMission(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(missionId);
        if (entity == null) {
            return null;
        } else {
            downloadManager.remove(missionId);
            DatabaseHelper.getInstance(c).deleteDownloadEntity(missionId);

            DownloadMission mission = new DownloadMission(entity);
            mission.entity.missionId = addMission(c, mission.entity);
            mission.entity.result = RESULT_DOWNLOADING;
            mission.process = 0;
            return mission;
        }
    }

    // delete.

    public void removeMission(Context c, long id) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(id);
        if (entity != null && entity.result != RESULT_SUCCEED) {
            downloadManager.remove(id);
        }
        DatabaseHelper.getInstance(c).deleteDownloadEntity(id);
    }

    public void clearMission(Context c, List<DownloadMissionEntity> entityList) {
        for (int i = 0; i < entityList.size(); i ++) {
            if (entityList.get(i).result != RESULT_SUCCEED) {
                downloadManager.remove(entityList.get(i).missionId);
            }
        }

        DatabaseHelper.getInstance(c).clearDownloadEntity();
    }

    // update.

    public void updateMissionResult(Context c, long id, int result) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(id);
        if (entity != null) {
            entity.result = result;
            DatabaseHelper.getInstance(c).updateDownloadEntity(entity);
        }
    }

    // query.

    @Nullable
    public DownloadMission getDownloadMission(Context context, long id) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(context).readDownloadEntity(id);
        if (entity == null) {
            return null;
        } else {
            Cursor cursor = getMissionCursor(id);
            float process = 0;
            if (cursor != null) {
                entity.result = getDownloadResult(cursor);
                process = getMissionProcess(cursor);
                cursor.close();
            }
            return new DownloadMission(entity, process);
        }
    }

    @Nullable
    private Cursor getMissionCursor(long id) {
        Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(id));
        if (cursor == null) {
            return null;
        } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor;
        } else {
            cursor.close();
            return null;
        }
    }

    private int getDownloadResult(@NotNull Cursor cursor) {
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_SUCCESSFUL:
                return RESULT_SUCCEED;

            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
                return RESULT_FAILED;

            default:
                return RESULT_DOWNLOADING;
        }
    }

    private float getMissionProcess(@NotNull Cursor cursor) {
        long soFar = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        long total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int result = (int) (100.0 * soFar / total);
        result = Math.max(0, result);
        result = Math.min(100, result);
        return result;
    }

    private boolean isMissionSuccess(long id) {
        Cursor cursor = getMissionCursor(id);
        if (cursor != null) {
            int result = getDownloadResult(cursor);
            cursor.close();
            return result == RESULT_SUCCEED;
        } else {
            return false;
        }
    }

    // feedback.

    public static void downloadFinish(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c)
                .readDownloadEntity(missionId);

        if (DownloadHelper.getInstance(c).isMissionSuccess(missionId)) {
            if (entity != null) {
                if (entity.downloadType != DownloadHelper.COLLECTION_TYPE) {
                    downloadPhotoSuccess(c, entity);
                } else {
                    downloadCollectionSuccess(c, entity);
                }
                DownloadHelper.getInstance(c)
                        .updateMissionResult(c, entity.missionId, DownloadHelper.RESULT_SUCCEED);
            }
        } else if (entity != null) {
            if (entity.downloadType != DownloadHelper.COLLECTION_TYPE) {
                downloadPhotoFailed(c, entity);
            } else {
                downloadCollectionFailed(c, entity);
            }
            DownloadHelper.getInstance(c)
                    .updateMissionResult(c, entity.missionId, DownloadHelper.RESULT_FAILED);
        }
    }

    private static void downloadPhotoSuccess(Context c, DownloadMissionEntity entity) {
        c.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + entity.getFilePath())));

        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null) {
            switch (entity.downloadType) {
                case DownloadHelper.DOWNLOAD_TYPE:
                    simpleDownloadSuccess(c, entity);
                    break;

                case DownloadHelper.SHARE_TYPE: {
                    shareDownloadSuccess(c, entity);
                    break;
                }

                case DownloadHelper.WALLPAPER_TYPE: {
                    wallpaperDownloadSuccess(c, entity);
                    break;
                }

                case COLLECTION_TYPE:
                    break;
            }
        } else {
            NotificationHelper.sendDownloadPhotoSuccessNotification(c, entity);
        }
    }

    private static void simpleDownloadSuccess(Context c, DownloadMissionEntity entity) {
        NotificationHelper.showActionSnackbar(
                c.getString(R.string.feedback_download_photo_success),
                c.getString(R.string.check),
                Snackbar.LENGTH_LONG,
                new OnCheckPhotoListener(Mysplash.getInstance().getTopActivity(), entity.title));
    }

    private static void shareDownloadSuccess(Context c, DownloadMissionEntity entity) {
        // Uri file = Uri.parse("file://" + entity.getFilePath());
        Uri file = FileUtils.filePathToUri(c, entity.getFilePath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, file);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(
                        Intent.createChooser(
                                intent,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_share_app)));
    }

    private static void wallpaperDownloadSuccess(Context c, DownloadMissionEntity entity) {
        // Uri file = Uri.parse("file://" + entity.getFilePath());
        Uri file = FileUtils.filePathToUri(c, entity.getFilePath());
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.setDataAndType(file, "image/jpg");
        intent.putExtra("mimeType", "image/jpg");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(
                        Intent.createChooser(
                                intent,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_wallpaper_app)));
    }

    private static void downloadCollectionSuccess(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_success),
                    c.getString(R.string.check),
                    Snackbar.LENGTH_LONG,
                    new OnCheckCollectionListener(c, entity.title));
        } else {
            NotificationHelper.sendDownloadCollectionSuccessNotification(c, entity);
        }
    }

    private static void downloadPhotoFailed(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_photo_failed),
                    c.getString(R.string.check),
                    Snackbar.LENGTH_LONG,
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadPhotoFailedNotification(c, entity);
        }
    }

    private static void downloadCollectionFailed(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_failed),
                    c.getString(R.string.check),
                    Snackbar.LENGTH_LONG,
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadCollectionFailedNotification(c, entity);
        }
    }

    /** <br> interface. */

    private static class OnCheckPhotoListener implements View.OnClickListener {
        // widget
        private Context c;

        // data
        private String title;

        // life cycle.
        OnCheckPhotoListener(Context c, String title) {
            this.c = c;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            IntentHelper.startCheckPhotoActivity(c, title);
        }
    }

    private static class OnCheckCollectionListener implements View.OnClickListener {
        // widget
        private Context c;

        // data
        private String title;

        // life cycle.
        OnCheckCollectionListener(Context c, String title) {
            this.c = c;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            IntentHelper.startCheckCollectionActivity(c, title);
        }
    }

    private static View.OnClickListener onStartManageActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentHelper.startDownloadManageActivity(Mysplash.getInstance().getTopActivity());
        }
    };
}

