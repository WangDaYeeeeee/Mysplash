package com.wangdaye.downloader.executor;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.view.View;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.downloader.DownloaderServiceIMP;
import com.wangdaye.downloader.base.RoutingHelper;
import com.wangdaye.downloader.base.NotificationHelper;
import com.wangdaye.downloader.base.OnDownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract downloader executor.
 *
 * Bind with {@link DownloadTask}
 * */

public abstract class AbstractDownloaderExecutor {

    private List<OnDownloadListener> listenerList;
    private final Object synchronizedLocker;

    public static final String ACTION_DOWNLOAD_COMPLETE = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
    public static final String ACTION_NOTIFICATION_CLICKED = DownloadManager.ACTION_NOTIFICATION_CLICKED;

    public static final String EXTRA_DOWNLOAD_ID = DownloadManager.EXTRA_DOWNLOAD_ID;

    public static class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case AbstractDownloaderExecutor.ACTION_DOWNLOAD_COMPLETE:
                    DownloaderServiceIMP.getInstance().completeTask(
                            context,
                            intent.getLongExtra(
                                    AbstractDownloaderExecutor.EXTRA_DOWNLOAD_ID,
                                    -1
                            )
                    );
                    break;

                case AbstractDownloaderExecutor.ACTION_NOTIFICATION_CLICKED:
                    RoutingHelper.startDownloadManageActivityFromNotification(context);
                    break;
            }
        }
    }

    AbstractDownloaderExecutor() {
        listenerList = new ArrayList<>();
        synchronizedLocker = new Object();
    }

    // management.

    public abstract long addTask(Context c, @NonNull DownloadTask task, boolean showSnackbar);

    public abstract long restartTask(Context c, @NonNull DownloadTask task);

    public abstract void completeTask(Context c, @NonNull DownloadTask task);

    public abstract void removeTask(Context c, @NonNull DownloadTask task, boolean deleteEntity);

    public abstract void clearTask(Context c, @NonNull List<DownloadTask> taskList);

    public abstract void updateTaskResult(Context c, @NonNull DownloadTask task,
                                          @DownloadTask.DownloadResultRule int result);

    public abstract float getTaskProcess(Context c, @NonNull DownloadTask task);

    public List<DownloadTask> readDownloadTaskList(Context c) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadTaskList();
        }
    }

    public List<DownloadTask> readDownloadTaskList(Context c,
                                                   @DownloadTask.DownloadResultRule int result) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadTaskList(result);
        }
    }

    @Nullable
    public DownloadTask readDownloadTask(Context c, String title) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadingTask(title);
        }
    }

    public boolean isDownloading(String title) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadingTaskCount(title) > 0;
        }
    }

    // result.

    static void downloadPhotoSuccess(Context c, DownloadTask task) {
        c.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + task.getFilePath())
                )
        );

        if (MysplashApplication.getInstance() != null
                && MysplashApplication.getInstance().getTopActivity() != null
                && MysplashApplication.getInstance().getTopActivity().isForeground()) {
            switch (task.downloadType) {
                case DownloadTask.DOWNLOAD_TYPE:
                    simpleDownloadSuccess(c, task);
                    break;

                case DownloadTask.SHARE_TYPE: {
                    shareDownloadSuccess(c, task);
                    break;
                }

                case DownloadTask.WALLPAPER_TYPE: {
                    wallpaperDownloadSuccess(c, task);
                    break;
                }

                case DownloadTask.COLLECTION_TYPE:
                    break;
            }
        } else {
            NotificationHelper.sendDownloadPhotoSuccessNotification(
                    c, RoutingHelper.getDownloadManageActivityIntentForNotification(), task);
        }
    }

    private static void simpleDownloadSuccess(Context c, DownloadTask task) {
        NotificationHelper.showActionSnackbar(
                c.getString(R.string.feedback_download_photo_success),
                c.getString(R.string.check),
                new OnCheckPhotoListener(MysplashApplication.getInstance().getTopActivity(), task.title));
    }

    private static void shareDownloadSuccess(Context c, DownloadTask entity) {
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
                    MysplashApplication.getInstance()
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
                        MysplashApplication.getInstance()
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

    private static void wallpaperDownloadSuccess(Context c, DownloadTask entity) {
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
                    MysplashApplication.getInstance()
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
                        MysplashApplication.getInstance().getString(R.string.feedback_choose_wallpaper_app)
                );
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

    static void downloadPhotoFailed(Context c, DownloadTask task) {
        if (MysplashApplication.getInstance() != null
                && MysplashApplication.getInstance().getTopActivity() != null
                && MysplashApplication.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_photo_failed),
                    c.getString(R.string.check),
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadPhotoFailedNotification(
                    c, RoutingHelper.getDownloadManageActivityIntentForNotification(), task);
        }
    }

    static void downloadCollectionSuccess(Context c, DownloadTask task) {
        if (MysplashApplication.getInstance() != null
                && MysplashApplication.getInstance().getTopActivity() != null
                && MysplashApplication.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_success),
                    c.getString(R.string.check),
                    new OnCheckCollectionListener(c, task.title));
        } else {
            NotificationHelper.sendDownloadCollectionSuccessNotification(
                    c, RoutingHelper.getDownloadManageActivityIntentForNotification(), task);
        }
    }

    static void downloadCollectionFailed(Context c, DownloadTask task) {
        if (MysplashApplication.getInstance() != null
                && MysplashApplication.getInstance().getTopActivity() != null
                && MysplashApplication.getInstance().getTopActivity().isForeground()) {
            NotificationHelper.showActionSnackbar(
                    c.getString(R.string.feedback_download_collection_failed),
                    c.getString(R.string.check),
                    onStartManageActivityListener);
        } else {
            NotificationHelper.sendDownloadCollectionFailedNotification(
                    c, RoutingHelper.getDownloadManageActivityIntentForNotification(), task);
        }
    }

    // interface.

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

    public void addOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        addOnDownloadListener(list, null);
    }

    void addOnDownloadListener(@NonNull List<? extends OnDownloadListener> list,
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

    public void removeOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        removeOnDownloadListener(list, null);
    }

    void removeOnDownloadListener(@NonNull List<? extends OnDownloadListener> list,
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
            RoutingHelper.startCheckPhotoActivity(c, title);
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
            RoutingHelper.startCheckCollectionActivity(c, title);
        }
    }

    private static View.OnClickListener onStartManageActivityListener = view ->
            RoutingHelper.startDownloadManageActivity(
                    MysplashApplication.getInstance().getTopActivity()
            );
}
