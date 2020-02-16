package com.wangdaye.downloader.executor;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.common.R;
import com.wangdaye.common.base.widget.FlagRunnable;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.manager.ThreadManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.DownloaderService;

import java.util.ArrayList;
import java.util.List;

public class AndroidDownloaderExecutor extends AbstractDownloaderExecutor {

    private static AndroidDownloaderExecutor instance;
    public static AndroidDownloaderExecutor getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (AndroidDownloaderExecutor.class) {
                if (instance == null) {
                    instance = new AndroidDownloaderExecutor(context);
                }
            }
        }
        return instance;
    }

    @Nullable private DownloadManager downloadManager;
    private Handler handler;

    @Nullable private PollingRunnable runnable;
    private class PollingRunnable extends FlagRunnable {
        @Override
        public void run() {
            while (isRunning()) {
                writeTaskList(list -> {
                    for (int i = list.size() - 1; i >= 0; i --) {
                        if (list.get(i).result != DownloadTask.RESULT_DOWNLOADING) {
                            list.remove(i);
                            if (list.size() == 0) {
                                // stop polling thread.
                                setRunning(false);
                                runnable = null;
                                return;
                            }
                        } else {
                            final PollingResult r = getDownloadInformation(list.get(i).taskId);
                            final DownloadTask t = list.get(i);
                            t.process = r.process;
                            t.result = r.result;
                            handler.post(() -> {
                                for (DownloaderService.OnDownloadListener l : onDownloadListeners) {
                                    if (r.result == DownloadTask.RESULT_DOWNLOADING) {
                                        l.onProcess(t.title, t.downloadType, t.process);
                                    } else {
                                        l.onComplete(t.title, t.downloadType, t.result);
                                    }
                                }
                            });
                        }
                    }
                });
                SystemClock.sleep(300);
            }
        }
    }

    private class PollingResult {

        @DownloadTask.DownloadResultRule int result;
        @FloatRange(from = 0, to = 100) float process;

        PollingResult(@DownloadTask.DownloadResultRule int result,
                      @FloatRange(from = 0, to = 100) float process) {
            this.result = result;
            this.process = process;
        }
    }

    private AndroidDownloaderExecutor(Context context) {
        super();
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public long addTask(Context c, @NonNull DownloadTask task, boolean showSnackbar) {
        if (downloadManager == null) {
            showErrorNotification();
            return -1;
        }

        writeTaskList(list -> {
            if (registerDownloadingTask(list, task)) {
                FileUtils.deleteFile(c, task);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(task.downloadUrl))
                        .setTitle(task.getNotificationTitle())
                        .setDescription(c.getString(R.string.feedback_downloading))
                        .setDestinationInExternalPublicDir(
                                DownloadTask.DOWNLOAD_TYPE_PATH,
                                DownloadTask.DOWNLOAD_SUB_PATH + "/" + task.title + task.getFormat()
                        );
                request.allowScanningByMediaScanner();

                task.taskId = downloadManager.enqueue(request);
                task.result = DownloadTask.RESULT_DOWNLOADING;
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
        if (downloadManager == null) {
            showErrorNotification();
            return -1;
        }

        writeTaskList(list -> {
            unregisterDownloadingTask(list, task);
            downloadManager.remove(task.taskId);
            ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
        });

        return addTask(c, task, true);
    }

    @Override
    public void completeTask(Context c, @NonNull DownloadTask task) {
        if (isMissionSuccess(task.taskId)) {
            if (task.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoSuccess(c, task);
            } else {
                downloadCollectionSuccess(c, task);
            }
            updateTaskResult(c, task, DownloadTask.RESULT_SUCCEED);
        } else {
            if (task.downloadType != DownloadTask.COLLECTION_TYPE) {
                downloadPhotoFailed(c, task);
            } else {
                downloadCollectionFailed(c, task);
            }
            updateTaskResult(c, task, DownloadTask.RESULT_FAILED);
        }
    }

    @Override
    public void removeTask(Context c, @NonNull DownloadTask task, boolean deleteEntity) {
        if (downloadManager == null) {
            showErrorNotification();
            return;
        }

        writeTaskList(list -> {
            unregisterDownloadingTask(list, task);
            if (task.result != DownloadTask.RESULT_SUCCEED) {
                downloadManager.remove(task.taskId);
            }
            if (deleteEntity) {
                ComponentFactory.getDatabaseService().deleteDownloadTask(task.taskId);
            }
        });
    }

    @NonNull
    @Override
    public List<DownloadTask> clearTask(Context c) {
        if (downloadManager == null) {
            showErrorNotification();
            return new ArrayList<>();
        }

        List<DownloadTask> taskList = new ArrayList<>();

        writeTaskList(list -> {
            taskList.addAll(list);

            for (DownloadTask t : list) {
                if (t.result != DownloadTask.RESULT_SUCCEED) {
                    downloadManager.remove(t.taskId);
                }
            }
            ComponentFactory.getDatabaseService().clearDownloadTask();
            clearDwonloadingTask(list);
        });

        return taskList;
    }

    @Override
    public void updateTaskResult(Context c, @NonNull DownloadTask task, int result) {
        writeTaskList(list -> {
            task.result = result;

            unregisterDownloadingTask(list, task);
            ComponentFactory.getDatabaseService().updateDownloadTask(task);
        });
    }

    private void showErrorNotification() {
        NotificationHelper.showSnackbar("Cannot get DownloadManager.");
    }

    private boolean isMissionSuccess(long id) {
        return getDownloadInformation(id).result == DownloadTask.RESULT_SUCCEED;
    }

    private PollingResult getDownloadInformation(long id) {
        Cursor cursor = getMissionCursor(id);
        if (cursor != null) {
            PollingResult result = new PollingResult(
                    getDownloadResult(cursor),
                    getMissionProcess(cursor)
            );
            cursor.close();
            return result;
        }
        return new PollingResult(DownloadTask.RESULT_SUCCEED, 100);
    }

    @Nullable
    private Cursor getMissionCursor(long id) {
        if (downloadManager == null) {
            NotificationHelper.showSnackbar("Cannot get DownloadManager.");
            return null;
        }

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

    @DownloadTask.DownloadResultRule
    private int getDownloadResult(@NonNull Cursor cursor) {
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_SUCCESSFUL:
                return DownloadTask.RESULT_SUCCEED;

            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
                return DownloadTask.RESULT_FAILED;

            default:
                return DownloadTask.RESULT_DOWNLOADING;
        }
    }

    private @FloatRange(from = 0, to = 100) float getMissionProcess(@NonNull Cursor cursor) {
        long soFar = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        long total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int result = (int) (100.0 * soFar / total);
        result = Math.max(0, result);
        result = Math.min(100, result);
        return result;
    }

    private boolean registerDownloadingTask(@NonNull List<DownloadTask> taskList, @NonNull DownloadTask task) {
        if (indexTask(taskList, task.title) < 0) {
            taskList.add(task);

            if (runnable == null || !runnable.isRunning()) {
                runnable = new PollingRunnable();
                ThreadManager.getInstance().execute(runnable);
            }
            return true;
        }
        return false;
    }

    private void unregisterDownloadingTask(@NonNull List<DownloadTask> taskList, @NonNull DownloadTask task) {
        int index = indexTask(taskList, task.title);
        if (index >= 0) {
            taskList.remove(index);
        }

        if (taskList.size() == 0 && runnable != null && runnable.isRunning()) {
            runnable.setRunning(false);
            runnable = null;
        }
    }

    private void clearDwonloadingTask(@NonNull List<DownloadTask> taskList) {
        taskList.clear();
    }
}
