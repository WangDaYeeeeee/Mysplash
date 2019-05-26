package com.wangdaye.mysplash.common.download.imp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract downloader service.
 *
 * Bind with {@link DownloadMissionEntity}
 * */

public abstract class AbstractDownloaderService {

    private List<OnDownloadListener> listenerList;

    private final Object synchronizedLocker;

    public static final String ACTION_DOWNLOAD_COMPLETE = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
    public static final String ACTION_NOTIFICATION_CLICKED = DownloadManager.ACTION_NOTIFICATION_CLICKED;

    public static final String EXTRA_DOWNLOAD_ID = DownloadManager.EXTRA_DOWNLOAD_ID;

    AbstractDownloaderService() {
        listenerList = new ArrayList<>();

        synchronizedLocker = new Object();
    }

    // management.

    public abstract long addMission(Context c, @NonNull DownloadMissionEntity entity, boolean showSnackbar);

    public abstract long restartMission(Context c, @NonNull DownloadMissionEntity entity);

    public abstract void completeMission(Context c, @NonNull DownloadMissionEntity entity);

    public abstract void removeMission(Context c, @NonNull DownloadMissionEntity entity, boolean deleteEntity);

    public abstract void clearMission(Context c, @NonNull List<DownloadMissionEntity> entityList);

    public abstract void updateMissionResult(Context c, @NonNull DownloadMissionEntity entity,
                                             @DownloadMissionEntity.DownloadResultRule int result);

    public abstract float getMissionProcess(Context c, @NonNull DownloadMissionEntity entity);

    public List<DownloadMissionEntity> readDownloadEntityList(Context c) {
        synchronized (synchronizedLocker) {
            return DatabaseHelper.getInstance(c).readDownloadEntityList();
        }
    }

    public List<DownloadMissionEntity> readDownloadEntityList(Context c,
                                                              @DownloadMissionEntity.DownloadResultRule int result) {
        synchronized (synchronizedLocker) {
            return DatabaseHelper.getInstance(c).readDownloadEntityList(result);
        }
    }

    @Nullable
    public DownloadMissionEntity readDownloadMissionEntity(Context c, String title) {
        synchronized (synchronizedLocker) {
            return DatabaseHelper.getInstance(c).readDownloadingEntity(title);
        }
    }

    public int readDownloadingEntityCount(Context c, String title) {
        synchronized (synchronizedLocker) {
            return DatabaseHelper.getInstance(c).readDownloadingEntityCount(title);
        }
    }

    // result.

    static void downloadPhotoSuccess(Context c, DownloadMissionEntity entity) {
        c.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + entity.getFilePath())
                )
        );

        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null
                && Mysplash.getInstance().getTopActivity().isForeground()) {
            switch (entity.downloadType) {
                case DownloadMissionEntity.DOWNLOAD_TYPE:
                    simpleDownloadSuccess(c, entity);
                    break;

                case DownloadMissionEntity.SHARE_TYPE: {
                    shareDownloadSuccess(c, entity);
                    break;
                }

                case DownloadMissionEntity.WALLPAPER_TYPE: {
                    wallpaperDownloadSuccess(c, entity);
                    break;
                }

                case DownloadMissionEntity.COLLECTION_TYPE:
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
                new OnCheckPhotoListener(Mysplash.getInstance().getTopActivity(), entity.title));
    }

    private static void shareDownloadSuccess(Context c, DownloadMissionEntity entity) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, FileUtils.filePathToUri(c, entity.getFilePath()));
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    Mysplash.getInstance()
                            .getString(R.string.feedback_choose_share_app));
            chooser.addCategory(Intent.CATEGORY_DEFAULT);
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = FileProvider.getUriForFile(
                        c,
                        FileUtils.getFileProviderAuthorities(),
                        new File(entity.getFilePath())
                );
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Intent chooser = Intent.createChooser(
                        intent,
                        Mysplash.getInstance()
                                .getString(R.string.feedback_choose_share_app));
                chooser.addCategory(Intent.CATEGORY_DEFAULT);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                c.startActivity(chooser);
            } catch (Exception e1) {
                e1.printStackTrace();
                NotificationHelper.showSnackbar("Share Photo Failed.");
            }
        }
    }

    private static void wallpaperDownloadSuccess(Context c, DownloadMissionEntity entity) {
        try {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(FileUtils.filePathToUri(c, entity.getFilePath()), "image/jpg");
            intent.putExtra("mimeType", "image/jpg");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    Mysplash.getInstance()
                            .getString(R.string.feedback_choose_wallpaper_app));
            chooser.addCategory(Intent.CATEGORY_DEFAULT);
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = FileProvider.getUriForFile(
                        c,
                        FileUtils.getFileProviderAuthorities(),
                        new File(entity.getFilePath())
                );
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(uri, "image/jpg");
                intent.putExtra("mimeType", "image/jpg");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Intent chooser = Intent.createChooser(
                        intent,
                        Mysplash.getInstance()
                                .getString(R.string.feedback_choose_wallpaper_app));
                chooser.addCategory(Intent.CATEGORY_DEFAULT);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                c.startActivity(chooser);
            } catch (Exception e1) {
                e1.printStackTrace();
                NotificationHelper.showSnackbar("Set Wallpaper Failed.");
            }
        }
    }

    static void downloadPhotoFailed(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null
                && Mysplash.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_photo_failed),
                    c.getString(R.string.check),
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadPhotoFailedNotification(c, entity);
        }
    }

    static void downloadCollectionSuccess(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null
                && Mysplash.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_success),
                    c.getString(R.string.check),
                    new OnCheckCollectionListener(c, entity.title));
        } else {
            NotificationHelper.sendDownloadCollectionSuccessNotification(c, entity);
        }
    }

    static void downloadCollectionFailed(Context c, DownloadMissionEntity entity) {
        if (Mysplash.getInstance() != null
                && Mysplash.getInstance().getTopActivity() != null
                && Mysplash.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_failed),
                    c.getString(R.string.check),
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadCollectionFailedNotification(c, entity);
        }
    }

    // interface.

    public static abstract class OnDownloadListener {

        protected long missionId;
        protected String missionTitle;

        @DownloadMissionEntity.DownloadResultRule
        protected int result;

        public OnDownloadListener(long missionId, String missionTitle,
                                  @DownloadMissionEntity.DownloadResultRule int result) {
            this.missionId = missionId;
            this.missionTitle = missionTitle;
            this.result = result;
        }

        public abstract void onProcess(float process);
        public abstract void onComplete(@DownloadMissionEntity.DownloadResultRule int result);
    }

    public interface SynchronizedRunnable {
        void synchronizedRun(@NonNull List<OnDownloadListener> listenerList);
    }

    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        addOnDownloadListener(l, null);
    }

    void addOnDownloadListener(@NonNull OnDownloadListener l,
                               @Nullable SynchronizedRunnable runnable) {
        synchronized (synchronizedLocker) {
            listenerList.add(l);

            if (runnable != null) {
                runnable.synchronizedRun(listenerList);
            }
        }
    }

    public void addOnDownloadListener(@NonNull List<OnDownloadListener> list) {
        addOnDownloadListener(list, null);
    }

    void addOnDownloadListener(@NonNull List<OnDownloadListener> list,
                               @Nullable SynchronizedRunnable runnable) {
        synchronized (synchronizedLocker) {
            listenerList.addAll(list);

            if (runnable != null) {
                runnable.synchronizedRun(listenerList);
            }
        }
    }

    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        removeOnDownloadListener(l, null);
    }

    void removeOnDownloadListener(@NonNull OnDownloadListener l,
                                  @Nullable SynchronizedRunnable runnable) {
        synchronized (synchronizedLocker) {
            listenerList.remove(l);

            if (runnable != null) {
                runnable.synchronizedRun(listenerList);
            }
        }
    }

    public void removeOnDownloadListener(@NonNull List<OnDownloadListener> list) {
        removeOnDownloadListener(list, null);
    }

    void removeOnDownloadListener(@NonNull List<OnDownloadListener> list,
                                  @Nullable SynchronizedRunnable runnable) {
        synchronized (synchronizedLocker) {
            listenerList.removeAll(list);

            if (runnable != null) {
                runnable.synchronizedRun(listenerList);
            }
        }
    }

    void synchronizedExecuteRunnable(@NonNull SynchronizedRunnable runnable) {
        synchronized (synchronizedLocker) {
            runnable.synchronizedRun(listenerList);
        }
    }

    private static class OnCheckPhotoListener implements View.OnClickListener {

        private Context c;
        private String title;

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

        private Context c;
        private String title;

        OnCheckCollectionListener(Context c, String title) {
            this.c = c;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            IntentHelper.startCheckCollectionActivity(c, title);
        }
    }

    private static View.OnClickListener onStartManageActivityListener = view ->
            IntentHelper.startDownloadManageActivity(Mysplash.getInstance().getTopActivity());
}
