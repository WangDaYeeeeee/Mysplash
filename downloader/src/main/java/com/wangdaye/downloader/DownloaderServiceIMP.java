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
import com.wangdaye.downloader.base.OnDownloadListener;
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
    public boolean switchDownloader(Context context, @DownloaderRule String downloader) {
        if (ComponentFactory.getDatabaseService()
                .readDownloadTaskCount(DownloadTask.RESULT_DOWNLOADING) > 0) {
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
    public void addTask(Context c, Photo p, @DownloadTask.DownloadTypeRule int type, String downloadScale) {
        if (FileUtils.createDownloadPath(c)) {
            long taskId = downloaderService.addTask(
                    c, new DownloadTask(c, p, type, downloadScale), true
            );
            photoService.downloadPhoto(p.id);

            MessageBus.getInstance().post(
                    new DownloadEvent(
                            taskId,
                            p.id,
                            type,
                            DownloadTask.RESULT_DOWNLOADING
                    )
            );
        }
    }

    @Override
    public void addTask(Context c, Collection collection) {
        if (FileUtils.createDownloadPath(c)) {
            long taskId = downloaderService.addTask(
                    c, new DownloadTask(collection), true
            );

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
    public DownloadTask restartTask(Context c, long taskId) {
        DownloadTask task = ComponentFactory.getDatabaseService().readDownloadTask(taskId);
        if (task != null) {
            task.taskId = downloaderService.restartTask(c, task);
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

    public void completeTask(Context c, long taskId) {
        DownloadTask entity = ComponentFactory.getDatabaseService().readDownloadTask(taskId);
        if (entity != null) {
            downloaderService.completeTask(c, entity);

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
    public void removeTask(Context c, @NonNull DownloadTask entity) {
        downloaderService.removeTask(c, entity, true);

        if (entity.result == DownloadTask.RESULT_DOWNLOADING) {
            MessageBus.getInstance().post(
                    new DownloadEvent(
                            entity.taskId,
                            entity.title,
                            entity.downloadType,
                            DownloadTask.RESULT_FAILED
                    )
            );
        }
    }

    @Override
    public void clearTask(Context c, @Nullable List<DownloadTask> entityList) {
        if (entityList != null) {
            downloaderService.clearTask(c, entityList);

            for (DownloadTask e : entityList) {
                if (e.result == DownloadTask.RESULT_DOWNLOADING) {
                    MessageBus.getInstance().post(
                            new DownloadEvent(
                                    e.taskId,
                                    e.title,
                                    e.downloadType,
                                    DownloadTask.RESULT_FAILED
                            )
                    );
                }
            }
        }
    }

    public DownloadTask readTaskProcess(Context c, @NonNull DownloadTask task) {
        switch (task.result) {
            case DownloadTask.RESULT_DOWNLOADING:
                task.process = downloaderService.getTaskProcess(c, task);
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

    public List<DownloadTask> readDownloadTaskList(Context c,
                                                   @DownloadTask.DownloadResultRule int result) {
        return downloaderService.readDownloadTaskList(c, result);
    }

    @Override
    public boolean isDownloading(Context c, String title) {
        return downloaderService.isDownloading(title);
    }

    public void updateTaskResult(Context c,
                                    @NonNull DownloadTask entity,
                                    @DownloadTask.DownloadResultRule int result) {
        downloaderService.updateTaskResult(c, entity, result);
    }

    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        downloaderService.addOnDownloadListener(l);
    }

    public void addOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        downloaderService.addOnDownloadListener(list);
    }

    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        downloaderService.removeOnDownloadListener(l);
    }

    public void removeOnDownloadListener(@NonNull List<? extends OnDownloadListener> list) {
        downloaderService.removeOnDownloadListener(list);
    }

    @Nullable
    @Override
    public DownloadTask readDownloadTask(Context context, String title) {
        return downloaderService.readDownloadTask(context, title);
    }

    @Override
    public void startDownloadManageActivity(Activity a) {
        RoutingHelper.startDownloadManageActivity(a);
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

