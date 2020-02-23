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
import com.wangdaye.component.service.DownloaderService;
import com.wangdaye.downloader.base.NotificationHelper;
import com.wangdaye.downloader.base.RoutingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

    private List<TaskDownloadListener> innerListenerList;
    private class TaskDownloadListener extends FileDownloadListener {

        DownloadTask task;
        long soFar;
        long total;

        TaskDownloadListener(DownloadTask task) {
            this.task = task;
            this.soFar = 0;
            this.total = 0;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            // do nothing.
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            this.soFar = soFarBytes;
            this.total = totalBytes;
            for (DownloaderService.OnDownloadListener l : onDownloadListeners) {
                l.onProcess(
                        this.task.title,
                        this.task.downloadType,
                        (float) Math.max(
                                0,
                                Math.min(100, 100.0 * soFarBytes / totalBytes)
                        )
                );
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            context.sendBroadcast(
                    new Intent(AbstractDownloaderExecutor.ACTION_DOWNLOAD_COMPLETE)
                            .putExtra(AbstractDownloaderExecutor.EXTRA_DOWNLOAD_ID, this.task.taskId)
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
            // do nothing.
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            context.sendBroadcast(
                    new Intent(AbstractDownloaderExecutor.ACTION_DOWNLOAD_COMPLETE)
                            .putExtra(AbstractDownloaderExecutor.EXTRA_DOWNLOAD_ID, this.task.taskId)
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

        NotificationRunnable() {
            builder = getInitDownloadingNotificationBuilder(context);
        }

        @Override
        public void run() {
            List<String> titleList = new ArrayList<>();
            AtomicLong total = new AtomicLong();
            AtomicLong soFar = new AtomicLong();
            int process;

            AtomicInteger size = new AtomicInteger();

            FileDownloader.getImpl().startForeground(
                    NotificationHelper.NOTIFICATION_DOWNLOADING_ID,
                    builder.build()
            );

            while (isRunning()) {
                titleList.clear();
                total.set(0);
                soFar.set(0);

                lockableTaskList.read(list -> {
                    size.set(innerListenerList.size());

                    if (size.get() != 0) {
                        for (TaskDownloadListener l : innerListenerList) {
                            titleList.add(l.task.getNotificationTitle());
                            total.addAndGet(l.total);
                            soFar.addAndGet(l.soFar);
                        }
                    }
                });

                if (size.get() == 0) {
                    break;
                }

                process = (int) Math.max(0, Math.min(100, 100.0 * soFar.get() / total.get()));

                NotificationCompat.InboxStyle inboxStyle = buildInboxStyle(titleList, process);

                builder.setSubText(process + "%");
                builder.setProgress(100, process, false);
                builder.setStyle(inboxStyle);
                NotificationHelper.sendDownloadingNotification(context, builder.build());

                SystemClock.sleep(1000);
            }

            FileDownloader.getImpl().stopForeground(true);
            NotificationHelper.removeDownloadingNotification(context);
        }
    }

    private FileDownloaderExecutor(Context context) {
        super();

        this.context = context.getApplicationContext();
        this.innerListenerList = new ArrayList<>();

        FileDownloader.setup(context);
        FileDownloader.setGlobalPost2UIInterval(100);

        List<DownloadTask> downloadingList = ComponentFactory.getDatabaseService().readDownloadTaskList(
                DownloadTask.RESULT_DOWNLOADING);
        for (DownloadTask t : downloadingList) {
            finishTask(context, t);
        }
    }

    @Override
    public long addTask(Context c, @NonNull DownloadTask task, boolean showSnackbar) {
        TaskDownloadListener l = new TaskDownloadListener(task);
        task.result = DownloadTask.RESULT_DOWNLOADING;
        task.process = 0;

        lockableTaskList.write((list, setter) -> {
            if (registerDownloadingTask(list, task, l)) {
                setter.setList(list);

                task.taskId = FileDownloader.getImpl()
                        .create(task.downloadUrl)
                        .setPath(task.getFilePath(c))
                        .setCallbackProgressMinInterval(50)
                        .setForceReDownload(true)
                        .setListener(l)
                        .start();
                ComponentFactory.getDatabaseService().writeDownloadTask(task);
            }
        });

        if (showSnackbar) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_download_start));
        }

        return task.taskId;
    }

    @Override
    public long restartTask(Context c, @NonNull DownloadTask task) {
        lockableTaskList.write((list, setter) -> {
            unregisterDownloadingTask(list, task);
            setter.setList(list);

            FileDownloader.getImpl().clear((int) task.taskId, "");
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(task.getFilePath(c)));

            ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
        });
        return addTask(c, task, true);
    }

    @Override
    public void completeTask(Context c, @NonNull DownloadTask task) {
        AtomicBoolean valid = new AtomicBoolean(false);
        lockableTaskList.write((list, setter) -> {
            finishTask(context, task);
            valid.set(unregisterDownloadingTask(list, task));
            setter.setList(list);
        });

        if (valid.get()) {
            for (DownloaderService.OnDownloadListener l : onDownloadListeners) {
                l.onComplete(task.title, task.downloadType, task.result);
            }
        }
    }

    @Override
    public void removeTask(Context c, @NonNull DownloadTask task, boolean deleteEntity) {
        lockableTaskList.write((list, setter) -> {
            unregisterDownloadingTask(list, task);
            setter.setList(list);

            if (task.result != DownloadTask.RESULT_SUCCEED) {
                FileDownloader.getImpl().clear((int) task.taskId, "");
                FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(task.getFilePath(c)));
            }
            if (deleteEntity) {
                ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
            }
        });
    }

    @NonNull
    @Override
    public List<DownloadTask> clearTask(Context c) {
        List<DownloadTask> taskList = new ArrayList<>();

        lockableTaskList.write((list, setter) -> {
            taskList.addAll(list);
            clearDownloadingTask(list);
            setter.setList(list);

            FileDownloader.getImpl().clearAllTaskData();
            ComponentFactory.getDatabaseService().clearDownloadTask();
        });

        return taskList;
    }

    @Override
    public void updateTaskResult(Context c, @NonNull DownloadTask task, int result) {
        lockableTaskList.write((list, setter) -> innerUpdateTaskResult(task, result));
    }

    private static void finishTask(Context context, @NonNull DownloadTask entity) {
        boolean complete = FileDownloader.getImpl()
                .getStatus((int) entity.taskId, entity.getFilePath(context)) == FileDownloadStatus.completed;
        if (complete) {
            innerUpdateTaskResult(entity, DownloadTask.RESULT_SUCCEED);
            context.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse(entity.getFilePath(context))
                    )
            );
            if (entity.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoSuccess(context, entity);
            } else {
                downloadCollectionSuccess(context, entity);
            }
        } else {
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(entity.getFilePath(context)));
            innerUpdateTaskResult(entity, DownloadTask.RESULT_FAILED);
            if (entity.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoFailed(context, entity);
            } else {
                downloadCollectionFailed(context, entity);
            }
        }
    }

    private static void innerUpdateTaskResult(@NonNull DownloadTask task, int result) {
        task.result = result;
        ComponentFactory.getDatabaseService().updateDownloadTask(task);
    }

    private boolean registerDownloadingTask(@NonNull List<DownloadTask> taskList, @NonNull DownloadTask task,
                                            @NonNull TaskDownloadListener l) {
        if (indexTask(taskList, task.title) < 0
                && indexTaskListener(innerListenerList, task.title) < 0) {
            taskList.add(task);
            innerListenerList.add(l);

            if (runnable == null || !runnable.isRunning()) {
                runnable = new NotificationRunnable();
                ThreadManager.getInstance().execute(runnable);
            }
            return true;
        }
        return false;
    }

    private boolean unregisterDownloadingTask(@NonNull List<DownloadTask> taskList, @NonNull DownloadTask task) {
        boolean result = false;
        int index = indexTask(taskList, task.title);
        if (index >= 0) {
            taskList.remove(index);
            result = true;
        }
        index = indexTaskListener(innerListenerList, task.title);
        if (index >= 0) {
            innerListenerList.remove(index);
        }

        if (innerListenerList.size() == 0 && runnable != null && runnable.isRunning()) {
            runnable.setRunning(false);
            runnable = null;
        }
        return result;
    }

    private static int indexTaskListener(List<TaskDownloadListener> list, String title) {
        for (int i = 0; i < list.size(); i ++) {
            if (list.get(i).task.title.equals(title)) {
                return i;
            }
        }
        return -1;
    }

    private void clearDownloadingTask(@NonNull List<DownloadTask> taskList) {
        taskList.clear();
        innerListenerList.clear();

        if (runnable != null && runnable.isRunning()) {
            runnable.setRunning(false);
            runnable = null;
        }
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