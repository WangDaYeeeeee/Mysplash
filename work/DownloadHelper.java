package com.wangdaye.mysplash._common.utils.helper;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash._common.utils.FileUtils;

import java.util.List;

/**
 * Download helper.
 * */

public class DownloadHelper {
    // data
    private boolean foreground;

    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;
    public static final int COLLECTION_TYPE = 4;

    public static final int RESULT_SUCCEED = 1;
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_DOWNLOADING = 0;

    /** <br> singleton. */

    private static DownloadHelper instance;

    public static DownloadHelper getInstance() {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper();
                }
            }
        }
        return instance;
    }

    /** <br> life cycle. */

    private DownloadHelper() {
        foreground = false;
    }

    public void init(Context context) {
        FileDownloader.init(context);
    }

    public void setServiceAlive(boolean alive) {
        if (alive) {
            FileDownloader.getImpl().bindService();
        } else {
            FileDownloader.getImpl().unBindServiceIfIdle();
        }
    }

    void setForeground(Notification notification) {
        if (notification != null) {
            if (!foreground) {
                foreground = true;
                FileDownloader.getImpl()
                        .startForeground(
                                NotificationHelper.DOWNLOAD_NOTIFICATION_ID,
                                notification);
            }
        }
    }

    void stopForeground() {
        foreground = false;
        FileDownloader.getImpl().stopForeground(true);
    }

    /** <br> data. */

    // insert.

    public void addMission(Context c, Photo p, int type) {
        if (FileUtils.createDownloadPath(c)) {
            addMission(c, new DownloadMissionEntity(p, type));
        }
    }

    public void addMission(Context c, Collection collection) {
        if (FileUtils.createDownloadPath(c)) {
            addMission(c, new DownloadMissionEntity(collection));
        }
    }

    private long addMission(Context c, DownloadMissionEntity entity) {
        FileUtils.deleteFile(entity);

        final OnDownloadListener listener = new OnDownloadListener(c.getApplicationContext(), entity);
        entity.missionId = FileDownloader.getImpl()
                .create(entity.downloadUrl)
                .setPath(entity.getFilePath())
                .setCallbackProgressMinInterval(NotificationHelper.REFRESH_RATE)
                .setListener(listener)
                .asInQueueTask()
                .enqueue();
        FileDownloader.getImpl().start(listener, false);

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
            FileDownloader.getImpl()
                    .clear((int) missionId, entity.getFilePath());
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
            FileDownloader.getImpl()
                    .clear((int) id, entity.getFilePath());
        }
        DatabaseHelper.getInstance(c).deleteDownloadEntity(id);
    }

    public void clearMission(Context c, List<DownloadMissionEntity> entityList, boolean clearDatabase) {
        for (int i = 0; i < entityList.size(); i ++) {
            if (entityList.get(i).result != RESULT_SUCCEED) {
                FileDownloader.getImpl()
                        .clear(
                                (int) entityList.get(i).missionId,
                                entityList.get(i).getFilePath());
            }
        }
        FileDownloader.getImpl().clearAllTaskData();

        if (clearDatabase) {
            DatabaseHelper.getInstance(c).clearDownloadEntity();
        }
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
            entity.result = getDownloadResult(entity);
            float process = getMissionProcess(entity);
            return new DownloadMission(entity, process);
        }
    }

    private int getDownloadResult(DownloadMissionEntity entity) {
        switch (FileDownloader.getImpl().getStatus((int) entity.missionId, entity.getFilePath())) {
            case FileDownloadStatus.completed:
                return RESULT_SUCCEED;

            case FileDownloadStatus.error:
            case FileDownloadStatus.warn:
            case FileDownloadStatus.paused:
                return RESULT_FAILED;

            default:
                return RESULT_DOWNLOADING;
        }
    }

    private float getMissionProcess(DownloadMissionEntity entity) {
        long soFar = FileDownloader.getImpl().getSoFar((int) entity.missionId);
        long total = FileDownloader.getImpl().getTotal((int) entity.missionId);
        return (int) (100.0 * soFar / total);
    }

    boolean isMissionSuccess(Context context, long id) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(context).readDownloadEntity(id);
        return entity == null || getDownloadResult(entity) == RESULT_SUCCEED;
    }
}

class OnDownloadListener extends FileDownloadListener {
    // data
    private Context context;
    private String title;
    private long soFar;
    private long total;

    /** <br> life cycle. */

    OnDownloadListener(Context context, DownloadMissionEntity entity) {
        this.context = context;
        this.title = entity.getRealTitle();
        this.soFar = this.total = 0;
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Notification notification = NotificationHelper.getInstance(context)
                .sendDownloadProgressNotification(title, soFarBytes - soFar, totalBytes - total, true, false);
        DownloadHelper.getInstance().setForeground(notification);
        soFar = soFarBytes;
        total = totalBytes;
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Notification notification = NotificationHelper.getInstance(context)
                .sendDownloadProgressNotification(title, soFarBytes - soFar, totalBytes - total, false, false);
        DownloadHelper.getInstance().setForeground(notification);
        soFar = soFarBytes;
        total = totalBytes;
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        downloadFinish(task.getId());
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        downloadFinish(task.getId());
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        downloadFinish(task.getId());
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        downloadFinish(task.getId());
    }

    /** <br> data. */

    private void downloadFinish(int missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(context)
                .readDownloadEntity(missionId);

        if (DownloadHelper.getInstance().isMissionSuccess(context, missionId)) {
            if (entity != null) {
                if (entity.downloadType != DownloadHelper.COLLECTION_TYPE) {
                    downloadPhotoSuccess(context, entity);
                } else {
                    downloadCollectionSuccess(context, entity);
                }
                DownloadHelper.getInstance()
                        .updateMissionResult(context, entity.missionId, DownloadHelper.RESULT_SUCCEED);
            }
        } else if (entity != null) {
            if (entity.downloadType != DownloadHelper.COLLECTION_TYPE) {
                downloadPhotoFailed(context, entity);
            } else {
                downloadCollectionFailed(context, entity);
            }
            DownloadHelper.getInstance()
                    .updateMissionResult(context, entity.missionId, DownloadHelper.RESULT_FAILED);
        }

        Notification notification = NotificationHelper.getInstance(context)
                .sendDownloadProgressNotification(title, -soFar, -total, true, true);
        if (notification != null) {
            DownloadHelper.getInstance().setForeground(notification);
        } else {
            DownloadHelper.getInstance().stopForeground();
        }
        DownloadHelper.getInstance().setServiceAlive(false);
    }

    private void downloadPhotoSuccess(Context c, DownloadMissionEntity entity) {
        c.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + entity.getFilePath())));

        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null) {
            switch (entity.downloadType) {
                case DownloadHelper.DOWNLOAD_TYPE:
                    simpleDownloadSuccess(entity);
                    break;

                case DownloadHelper.SHARE_TYPE: {
                    shareDownloadSuccess(entity);
                    break;
                }

                case DownloadHelper.WALLPAPER_TYPE: {
                    wallpaperDownloadSuccess(entity);
                    break;
                }
            }
        } else {
            NotificationHelper.sendDownloadPhotoSuccessNotification(c, entity);
        }
    }

    private void simpleDownloadSuccess(DownloadMissionEntity entity) {
        Context c = Mysplash.getInstance().getTopActivity();
        NotificationHelper.showActionSnackbar(
                c.getString(R.string.feedback_download_photo_success),
                c.getString(R.string.check),
                Snackbar.LENGTH_LONG,
                new OnCheckPhotoListener(Mysplash.getInstance().getTopActivity(), entity.title));
    }

    private void shareDownloadSuccess(DownloadMissionEntity entity) {
        // Uri file = Uri.parse("file://" + entity.getFilePath());
        Uri file = FileUtils.filePathToUri(context, entity.getFilePath());
        Intent action = new Intent(Intent.ACTION_SEND);
        action.putExtra(Intent.EXTRA_STREAM, file);
        action.setType("image/*");
        Mysplash.getInstance()
                .getTopActivity()
                .startActivity(
                        Intent.createChooser(
                                action,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_share_app)));
    }

    private void wallpaperDownloadSuccess(DownloadMissionEntity entity) {
        // Uri file = Uri.parse("file://" + entity.getFilePath());
        Uri file = FileUtils.filePathToUri(context, entity.getFilePath());
        Intent action = new Intent(Intent.ACTION_ATTACH_DATA);
        action.setDataAndType(file, "image/jpg");
        action.putExtra("mimeType", "image/jpg");
        Mysplash.getInstance()
                .getTopActivity()
                .startActivity(
                        Intent.createChooser(
                                action,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_wallpaper_app)));
    }

    private void downloadCollectionSuccess(Context c, DownloadMissionEntity entity) {
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

    private void downloadPhotoFailed(Context c, DownloadMissionEntity entity) {
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

    private void downloadCollectionFailed(Context c, DownloadMissionEntity entity) {
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

    private class OnCheckPhotoListener implements View.OnClickListener {
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

    private class OnCheckCollectionListener implements View.OnClickListener {
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

    private View.OnClickListener onStartManageActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentHelper.startDownloadManageActivity(Mysplash.getInstance().getTopActivity());
        }
    };
}

