package com.wangdaye.downloader.executor;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.wangdaye.common.R;
import com.wangdaye.common.base.widget.FlagRunnable;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.utils.manager.ThreadManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.downloader.base.NotificationHelper;
import com.wangdaye.downloader.base.OnDownloadListener;
import com.wangdaye.downloader.base.RoutingHelper;

import java.util.ArrayList;
import java.util.List;

public class FileDownloaderExecutor extends AbstractDownloaderExecutor {

    private static FileDownloaderExecutor instance;

    public static FileDownloaderExecutor getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (FileDownloaderExecutor.class) {
                if (instance == null) {
                    instance = new FileDownloaderExecutor(context);
                }
            }
        }
        return instance;
    }

    private Context context;
    private List<TaskDownloadListener> listenerList;

    private final Object synchronizedLocker;

    private class TaskDownloadListener extends FileDownloadListener {

        long id;
        String title;
        long soFar;
        long total;

        private List<OnDownloadListener> outsideListeners;

        TaskDownloadListener(String title) {
            this.title = title;
            this.outsideListeners = new ArrayList<>();
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            // do nothing.
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            this.soFar = soFarBytes;
            this.total = totalBytes;

            synchronized (synchronizedLocker) {
                for (OnDownloadListener l : outsideListeners) {
                    l.onProcess(
                            (float) Math.max(
                                    0,
                                    Math.min(100, 100.0 * soFarBytes / totalBytes)
                            )
                    );
                }
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            context.sendBroadcast(
                    new Intent(AbstractDownloaderExecutor.ACTION_DOWNLOAD_COMPLETE)
                            .putExtra(AbstractDownloaderExecutor.EXTRA_DOWNLOAD_ID, id)
                            .setComponent(
                                    new ComponentName(
                                            context,
                                            AndroidDownloaderExecutor.DownloadReceiver.class
                                    )
                            )
            );
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            synchronized (synchronizedLocker) {
                outsideListeners.clear();
            }
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            context.sendBroadcast(
                    new Intent(AbstractDownloaderExecutor.ACTION_DOWNLOAD_COMPLETE)
                            .putExtra(AbstractDownloaderExecutor.EXTRA_DOWNLOAD_ID, id)
                            .setComponent(
                                    new ComponentName(
                                            context,
                                            AndroidDownloaderExecutor.DownloadReceiver.class
                                    )
                            )
            );
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            // do nothing.
        }
    }

    @Nullable
    private NotificationRunnable runnable;
    private class NotificationRunnable extends FlagRunnable {

        private NotificationCompat.Builder builder;

        NotificationRunnable(NotificationCompat.Builder builder) {
            this.builder = builder;
        }

        @Override
        public void run() {
            List<String> titleList = new ArrayList<>();
            long total;
            long soFar;
            int process;

            int size;

            while (isRunning()) {
                titleList.clear();
                total = 0;
                soFar = 0;

                synchronized (synchronizedLocker) {
                    size = listenerList.size();

                    if (size != 0) {
                        for (TaskDownloadListener l : listenerList) {
                            titleList.add(l.title);
                            total += l.total;
                            soFar += l.soFar;
                        }
                    }
                }

                if (size == 0) {
                    break;
                }

                process = (int) Math.max(0, Math.min(100, 100.0 * soFar / total));

                NotificationCompat.InboxStyle inboxStyle = buildInboxStyle(titleList, process);

                builder.setSubText(process + "%");
                builder.setProgress(100, process, false);
                builder.setStyle(inboxStyle);
                NotificationHelper.sendDownloadingNotification(context, builder.build());

                SystemClock.sleep(1000);
            }
        }
    }

    private FileDownloaderExecutor(Context context) {
        super();

        this.context = context.getApplicationContext();
        this.listenerList = new ArrayList<>();

        this.synchronizedLocker = new Object();

        FileDownloader.setup(context);
        FileDownloader.setGlobalPost2UIInterval(50);

        synchronized (synchronizedLocker) {
            List<DownloadTask> downloadingList = ComponentFactory.getDatabaseService()
                    .readDownloadTaskList(DownloadTask.RESULT_DOWNLOADING);
            for (DownloadTask t : downloadingList) {
                downloadFinish(context, t);
            }
        }
    }

    @Override
    public long addTask(Context c, @NonNull DownloadTask task, boolean showSnackbar) {
        TaskDownloadListener listener = new TaskDownloadListener(task.getNotificationTitle());

        synchronized (synchronizedLocker) {
            task.taskId = FileDownloader.getImpl()
                    .create(task.downloadUrl)
                    .setPath(task.getFilePath())
                    .setCallbackProgressMinInterval(50)
                    .setForceReDownload(true)
                    .setListener(listener)
                    .start();
            task.result = DownloadTask.RESULT_DOWNLOADING;
            registerDownloadListener(task.taskId, listener);

            ComponentFactory.getDatabaseService().writeDownloadTask(task);
        }

        if (showSnackbar) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_download_start));
        }

        return task.taskId;
    }

    @Override
    public long restartTask(Context c, @NonNull DownloadTask task) {
        synchronized (synchronizedLocker) {
            unregisterDownloadListener(task.taskId);

            FileDownloader.getImpl().clear((int) task.taskId, "");
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(task.getFilePath()));

            ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
        }

        return addTask(c, task, true);
    }

    @Override
    public void completeTask(Context c, @NonNull DownloadTask task) {
        synchronized (synchronizedLocker) {
            downloadFinish(context, task);
            TaskDownloadListener listener = unregisterDownloadListener(task.taskId);
            if (listener != null) {
                for (OnDownloadListener ol : listener.outsideListeners) {
                    ol.onComplete(task.result);
                }
                listener.outsideListeners.clear();
            }
        }
    }

    @Override
    public void removeTask(Context c, @NonNull DownloadTask task, boolean deleteEntity) {
        synchronized (synchronizedLocker) {
            if (task.result != DownloadTask.RESULT_SUCCEED) {
                unregisterDownloadListener(task.taskId);
                FileDownloader.getImpl().clear((int) task.taskId, "");
                FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(task.getFilePath()));
            }
            if (deleteEntity) {
                ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
            }
        }
    }

    @Override
    public void clearTask(Context c, @NonNull List<DownloadTask> taskList) {
        synchronized (synchronizedLocker) {
            FileDownloader.getImpl().clearAllTaskData();
            ComponentFactory.getDatabaseService().clearDownloadTask();
        }
    }

    @Override
    public void updateTaskResult(Context c, @NonNull DownloadTask task, int result) {
        task.result = result;
        synchronized (synchronizedLocker) {
            ComponentFactory.getDatabaseService().updateDownloadTask(task);
        }
    }

    @Override
    public float getTaskProcess(Context c, @NonNull DownloadTask task) {
        long soFar = FileDownloader.getImpl().getSoFar((int) task.taskId);
        long total = FileDownloader.getImpl().getTotal((int) task.taskId);
        float result = (float) (100.0 * soFar / total);
        result = Math.max(0, result);
        result = Math.min(100, result);
        return result;
    }

    @Override
    public List<DownloadTask> readDownloadTaskList(Context c) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadTaskList();
        }
    }

    @Override
    public List<DownloadTask> readDownloadTaskList(Context c,
                                                   @DownloadTask.DownloadResultRule int result) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadTaskList(result);
        }
    }

    @Nullable
    @Override
    public DownloadTask readDownloadTask(Context c, String title) {
        synchronized (synchronizedLocker) {
            return ComponentFactory.getDatabaseService().readDownloadingTask(title);
        }
    }

    @Override
    public boolean isDownloading(String title) {
        synchronized (synchronizedLocker) {
            if (listenerList.size() == 0) {
                return false;
            }

            for (int i = 0; i < listenerList.size(); i ++) {
                if (listenerList.get(i).title.equals(title)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        synchronized (synchronizedLocker) {
            for (int i = 0; i < listenerList.size(); i ++) {
                if (listenerList.get(i).id == l.taskId) {
                    listenerList.get(i).outsideListeners.add(l);
                    break;
                }
            }
        }
    }

    @Override
    public void addOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        synchronized (synchronizedLocker) {
            for (OnDownloadListener l : list) {
                for (int i = 0; i < listenerList.size(); i ++) {
                    if (listenerList.get(i).id == l.taskId) {
                        listenerList.get(i).outsideListeners.add(l);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        synchronized (synchronizedLocker) {
            for (int i = 0; i < listenerList.size(); i ++) {
                if (listenerList.get(i).id == l.taskId) {
                    listenerList.get(i).outsideListeners.remove(l);
                    break;
                }
            }
        }
    }

    @Override
    public void removeOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        synchronized (synchronizedLocker) {
            for (OnDownloadListener l : list) {
                for (int i = 0; i < listenerList.size(); i ++) {
                    if (listenerList.get(i).id == l.taskId) {
                        listenerList.get(i).outsideListeners.remove(l);
                        break;
                    }
                }
            }
        }
    }

    private void downloadFinish(Context context, @NonNull DownloadTask entity) {
        boolean complete = FileDownloader.getImpl()
                .getStatus((int) entity.taskId, entity.getFilePath()) == FileDownloadStatus.completed;
        if (complete) {
            updateTaskResult(context, entity, DownloadTask.RESULT_SUCCEED);
            context.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse(entity.getFilePath())
                    )
            );
            if (entity.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoSuccess(context, entity);
            } else {
                downloadCollectionSuccess(context, entity);
            }
        } else {
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(entity.getFilePath()));
            updateTaskResult(context, entity, DownloadTask.RESULT_FAILED);
            if (entity.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoFailed(context, entity);
            } else {
                downloadCollectionFailed(context, entity);
            }
        }
    }

    private void registerDownloadListener(long missionId, @NonNull TaskDownloadListener l) {
        l.id = missionId;
        listenerList.add(l);

        if (runnable == null) {
            NotificationCompat.Builder builder = getInitDownloadingNotificationBuilder(context);

            FileDownloader.getImpl().startForeground(
                    NotificationHelper.NOTIFICATION_DOWNLOADING_ID,
                    builder.build()
            );

            runnable = new NotificationRunnable(builder);
            ThreadManager.getInstance().execute(runnable);
        }
    }

    @Nullable
    private TaskDownloadListener unregisterDownloadListener(long missionId) {
        TaskDownloadListener result = null;

        for (int i = 0; i < listenerList.size(); i ++) {
            if (listenerList.get(i).id == missionId) {
                result = listenerList.remove(i);
                break;
            }
        }

        if (listenerList.size() == 0 && runnable != null && runnable.isRunning()) {
            runnable.setRunning(false);
            runnable = null;

            FileDownloader.getImpl().stopForeground(true);
            NotificationHelper.removeDownloadingNotification(context);
        }

        return result;
    }

    private NotificationCompat.Builder getInitDownloadingNotificationBuilder(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            NotificationHelper.createDownloadChannel(context, manager);
        }

        return new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_DOWNLOAD)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentText(context.getString(R.string.feedback_downloading))
                .setSubText(((int) (float) 0) + "%")
                .setColor(ContextCompat.getColor(context, R.color.colorNotification))
                .setSound(null)
                .setShowWhen(false)
                .setProgress(0, 0, true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true)
                .setContentIntent(
                        NotificationHelper.buildPendingIntent(
                                context,
                                RoutingHelper.getDownloadManageActivityIntentForNotification()
                        )
                );
    }

    @Nullable
    private NotificationCompat.InboxStyle buildInboxStyle(@Nullable List<String> titleList,
                                                          float process) {
        if (titleList != null) {
            NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
            inbox.setBigContentTitle(context.getString(R.string.feedback_downloading));
            inbox.setSummaryText(((int) process) + "%");
            for (int i = 0; i < titleList.size(); i ++) {
                if (i < 7) {
                    inbox.addLine(titleList.get(i));
                } else {
                    inbox.addLine("...");
                    break;
                }
            }
            return inbox;
        } else {
            return null;
        }
    }
}