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
import com.wangdaye.component.service.DownloaderService;
import com.wangdaye.downloader.DownloaderServiceIMP;
import com.wangdaye.downloader.base.RoutingHelper;
import com.wangdaye.downloader.base.NotificationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract downloader executor.
 *
 * Bind with {@link DownloadTask}
 * */

public abstract class AbstractDownloaderExecutor {

    private List<DownloadTask> taskList;
    private final ReadWriteLock readWriteLock;
    protected List<DownloaderService.OnDownloadListener> onDownloadListeners;

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
        taskList = ComponentFactory.getDatabaseService().readDownloadTaskList(DownloadTask.RESULT_DOWNLOADING);
        readWriteLock = new ReentrantReadWriteLock();
        onDownloadListeners = new ArrayList<>();
    }

    // management.

    public void addOnDownloadListener(@NonNull DownloaderService.OnDownloadListener l) {
        if (!onDownloadListeners.contains(l)) {
            onDownloadListeners.add(l);
        }
    }

    public void removeOnDownloadListener(@NonNull DownloaderService.OnDownloadListener l) {
        onDownloadListeners.remove(l);
    }

    public abstract long addTask(Context c, @NonNull DownloadTask task, boolean showSnackbar);

    public abstract long restartTask(Context c, @NonNull DownloadTask task);

    public abstract void completeTask(Context c, @NonNull DownloadTask task);

    public abstract void removeTask(Context c, @NonNull DownloadTask task, boolean deleteEntity);

    @NonNull
    public abstract List<DownloadTask> clearTask(Context c);

    public abstract void updateTaskResult(Context c, @NonNull DownloadTask task,
                                          @DownloadTask.DownloadResultRule int result);

    public float getTaskProcess(Context c, @NonNull DownloadTask task) {
        Float[] process = new Float[] {0f};
        readTaskList(list -> {
            for (DownloadTask t : list) {
                if (t.title.equals(task.title)) {
                    process[0] = t.process;
                    break;
                }
            }
        });
        return process[0];
    }

    public List<DownloadTask> readDownloadTaskList(Context c,
                                                   @DownloadTask.DownloadResultRule int result) {
        if (result == DownloadTask.RESULT_DOWNLOADING) {
            List<DownloadTask> taskList = new ArrayList<>();
            readTaskList(list -> {
                for (DownloadTask t : list) {
                    taskList.add(t.clone());
                }
            });
            return taskList;
        } else {
            return ComponentFactory.getDatabaseService().readDownloadTaskList(result);
        }
    }

    @Nullable
    public DownloadTask readDownloadTask(Context c, String title) {
        DownloadTask[] task = new DownloadTask[] {null};
        readTaskList(list -> {
            for (DownloadTask t : list) {
                if (t.title.equals(title)) {
                    task[0] = t.clone();
                    break;
                }
            }
        });
        if (task[0] == null) {
            task[0] = ComponentFactory.getDatabaseService().readDownloadingTask(title);
        }
        return task[0];
    }

    public boolean isDownloading(String title) {
        Boolean[] result = new Boolean[] {false};
        readTaskList(list -> {
            for (DownloadTask t : list) {
                if (t.title.equals(title)) {
                    result[0] = true;
                    break;
                }
            }
        });
        return result[0];
    }

    // result.

    static void downloadPhotoSuccess(Context c, DownloadTask task) {
        c.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + task.getFilePath(c))
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
            intent.putExtra(Intent.EXTRA_STREAM, FileUtils.filePathToUri(c, entity.getFilePath(c)));
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
                        new File(entity.getFilePath(c))
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
            intent.setDataAndType(FileUtils.filePathToUri(c, entity.getFilePath(c)), "image/jpg");
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
                        new File(entity.getFilePath(c))
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

    public interface TaskListReader {
        void execute(List<DownloadTask> list);
    }

    public interface TaskListWriter {
        void execute(List<DownloadTask> list);
    }

    protected void readTaskList(TaskListReader reader) {
        readWriteLock.readLock().lock();
        reader.execute(taskList);
        readWriteLock.readLock().unlock();
    }

    protected void writeTaskList(TaskListWriter writer) {
        readWriteLock.writeLock().lock();
        writer.execute(taskList);
        readWriteLock.writeLock().unlock();
    }

    protected static int indexTask(List<DownloadTask> list, String title) {
        for (int i = 0; i < list.size(); i ++) {
            if (list.get(i).title.equals(title)) {
                return i;
            }
        }
        return -1;
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
