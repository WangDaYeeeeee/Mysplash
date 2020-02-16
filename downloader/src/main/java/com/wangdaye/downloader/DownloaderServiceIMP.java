package com.wangdaye.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.DownloaderService;
import com.wangdaye.downloader.base.RoutingHelper;
import com.wangdaye.downloader.di.DaggerNetworkServiceComponent;
import com.wangdaye.downloader.executor.AndroidDownloaderExecutor;
import com.wangdaye.downloader.executor.AbstractDownloaderExecutor;
import com.wangdaye.downloader.executor.FileDownloaderExecutor;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.bus.MessageBus;

import java.util.List;

import javax.inject.Inject;

/**
 * Downloader service implementor.
 *
 * A helper class that makes operations of {@link DownloadManager} easier.
 *
 * */

public class DownloaderServiceIMP implements DownloaderService {

    private static volatile DownloaderServiceIMP instance;
    public static DownloaderServiceIMP getInstance(Context context,
                                                   @DownloaderRule String downloader) {
        if (instance == null) {
            synchronized (DownloaderServiceIMP.class) {
                if (instance == null) {
                    instance = new DownloaderServiceIMP(context, downloader);
                }
            }
        }
        return instance;
    }

    public static DownloaderServiceIMP getInstance() {
        return (DownloaderServiceIMP) ComponentFactory.getDownloaderService();
    }

    private AbstractDownloaderExecutor downloaderService;
    @Inject PhotoService photoService;

    private DownloaderServiceIMP(Context context, @DownloaderRule String downloader) {
        bindDownloader(context, downloader);
        DaggerNetworkServiceComponent.create().inject(this);
    }

    @Override
    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        downloaderService.addOnDownloadListener(l);
    }

    @Override
    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        downloaderService.removeOnDownloadListener(l);
    }

    @Override
    public boolean switchDownloader(Context context, @DownloaderRule String downloader) {
        if (ComponentFactory.getDatabaseService().readDownloadTaskCount(
                DownloadTask.RESULT_DOWNLOADING) > 0) {
            return false;
        }
        bindDownloader(context, downloader);
        return true;
    }

    private void bindDownloader(Context context, @DownloaderRule String downloader) {
        if (downloader.equals(DOWNLOADER_MYSPLASH)) {
            this.downloaderService = FileDownloaderExecutor.getInstance(context);
        } else {
            this.downloaderService = AndroidDownloaderExecutor.getInstance(context);
        }
    }

    @Override
    public void addTask(Context context, Photo photo, @DownloadTask.DownloadTypeRule int type, String downloadScale) {
        if (FileUtils.createDownloadPath(context)) {
            long taskId = downloaderService.addTask(
                    context, new DownloadTask(context, photo, type, downloadScale), true);
            photoService.downloadPhoto(photo.id);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            taskId,
                            photo.id,
                            type,
                            DownloadTask.RESULT_DOWNLOADING
                    )
            );
        }
    }

    @Override
    public void addTask(Context context, Collection collection) {
        if (FileUtils.createDownloadPath(context)) {
            long taskId = downloaderService.addTask(
                    context, new DownloadTask(collection), true);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            taskId,
                            String.valueOf(collection.id),
                            DownloadTask.COLLECTION_TYPE,
                            DownloadTask.RESULT_DOWNLOADING
                    )
            );
        }
    }

    @Nullable
    public DownloadTask restartTask(Context context, long taskId) {
        DownloadTask task = ComponentFactory.getDatabaseService().readDownloadTask(taskId);
        if (task != null) {
            task.taskId = downloaderService.restartTask(context, task);
            task.result = DownloadTask.RESULT_DOWNLOADING;
            task.process = 0;

            if (task.taskId != -1) {
                MessageBus.getInstance().post(
                        new DownloadEvent(
                                task.taskId,
                                task.title,
                                task.downloadType,
                                DownloadTask.RESULT_DOWNLOADING
                        )
                );
                return task;
            }
        }
        return null;
    }

    public void completeTask(Context context, long taskId) {
        DownloadTask entity = ComponentFactory.getDatabaseService().readDownloadTask(taskId);
        if (entity != null) {
            downloaderService.completeTask(context, entity);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            entity.taskId,
                            entity.title,
                            entity.downloadType,
                            entity.result
                    )
            );
        }
    }

    @Override
    public void removeTask(Context context, @NonNull DownloadTask task) {
        downloaderService.removeTask(context, task, true);

        if (task.result == DownloadTask.RESULT_DOWNLOADING) {
            MessageBus.getInstance().post(
                    new DownloadEvent(
                            task.taskId,
                            task.title,
                            task.downloadType,
                            DownloadTask.RESULT_FAILED
                    )
            );
        }
    }

    @Override
    public void clearTask(Context context) {
        List<DownloadTask> list = downloaderService.clearTask(context);
        for (DownloadTask t : list) {
            if (t.result == DownloadTask.RESULT_DOWNLOADING) {
                MessageBus.getInstance().post(
                        new DownloadEvent(
                                t.taskId,
                                t.title,
                                t.downloadType,
                                DownloadTask.RESULT_FAILED
                        )
                );
            }
        }
    }

    public DownloadTask readTaskProcess(Context context, @NonNull DownloadTask task) {
        switch (task.result) {
            case DownloadTask.RESULT_DOWNLOADING:
                task.process = downloaderService.getTaskProcess(context, task);
                break;

            case DownloadTask.RESULT_FAILED:
                task.process = 0;
                break;

            case DownloadTask.RESULT_SUCCEED:
                task.process = 100;
                break;
        }
        return task;
    }

    public List<DownloadTask> readDownloadTaskList(Context context,
                                                   @DownloadTask.DownloadResultRule int result) {
        return downloaderService.readDownloadTaskList(context, result);
    }

    @Override
    public boolean isDownloading(Context context, String title) {
        return downloaderService.isDownloading(title);
    }

    public void updateTaskResult(Context context,
                                 @NonNull DownloadTask entity,
                                 @DownloadTask.DownloadResultRule int result) {
        downloaderService.updateTaskResult(context, entity, result);
    }

    @Nullable
    @Override
    public DownloadTask readDownloadTask(Context context, String title) {
        return downloaderService.readDownloadTask(context, title);
    }

    @Override
    public void startDownloadManageActivity(Activity activity) {
        RoutingHelper.startDownloadManageActivity(activity);
    }

    @Override
    public void startDownloadManageActivityFromNotification(Context context) {
        RoutingHelper.startDownloadManageActivityFromNotification(context);
    }

    @Override
    public Intent getDownloadManageActivityIntentForShortcut() {
        return RoutingHelper.getDownloadManageActivityIntentForShortcut();
    }

    @Override
    public Intent getDownloadManageActivityIntentForNotification() {
        return RoutingHelper.getDownloadManageActivityIntentForNotification();
    }
}

