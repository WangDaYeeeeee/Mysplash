package com.wangdaye.mysplash.common.download.imp;

import android.app.NotificationManager;
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
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FlagRunnable;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;

import java.util.ArrayList;
import java.util.List;

public class FileDownloaderService extends DownloaderService {

    private Context context;
    private List<TaskDownloadListener> listenerList;

    private class TaskDownloadListener extends FileDownloadListener {

        long id;
        String title;
        long soFar;
        long total;

        @Nullable
        private OnDownloadListener outsideListener;

        TaskDownloadListener(String title, @Nullable OnDownloadListener l) {
            this.title = title;
            this.outsideListener = l;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            // do nothing.
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            this.soFar = soFarBytes;
            this.total = totalBytes;
            if (outsideListener != null) {
                outsideListener.onProcess((float) Math.max(0, Math.min(100, 100.0 * soFarBytes / totalBytes)));
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            downloadFinish(context, id, true);
            unregisterDownloadListener(id);
            if (outsideListener != null) {
                outsideListener.onComplete(RESULT_SUCCEED);
                outsideListener = null;
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            outsideListener = null;
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            downloadFinish(context, id, false);
            unregisterDownloadListener(id);
            if (outsideListener != null) {
                outsideListener.onComplete(RESULT_FAILED);
                outsideListener = null;
            }
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
            while (isRunning() && listenerList.size() > 0) {
                List<String> titleList = new ArrayList<>(listenerList.size());
                long total = 0;
                long soFar = 0;
                for (TaskDownloadListener l : listenerList) {
                    titleList.add(l.title);
                    total += l.total;
                    soFar += l.soFar;
                }
                int process = (int) Math.max(0, Math.min(100, 100.0 * soFar / total));

                NotificationCompat.InboxStyle inboxStyle = buildInboxStyle(titleList, process);

                builder.setSubText(process + "%");
                builder.setProgress(100, process, false);
                builder.setStyle(inboxStyle);
                NotificationHelper.sendDownloadingNotification(context, builder.build());

                SystemClock.sleep(1000);
            }
        }
    }

    public FileDownloaderService(Context context) {
        this.context = context.getApplicationContext();
        this.listenerList = new ArrayList<>();

        FileDownloader.setup(context);
        FileDownloader.setGlobalPost2UIInterval(50);

        List<DownloadMissionEntity> downloadingList = DatabaseHelper.getInstance(context)
                .readDownloadEntityList(RESULT_DOWNLOADING);
        for (DownloadMissionEntity e : downloadingList) {
            downloadFinish(context, e, false);
        }
    }

    @Override
    public long addMission(Context c, @NonNull DownloadMissionEntity entity, boolean showSnackbar) {
        TaskDownloadListener listener = new TaskDownloadListener(entity.getNotificationTitle(), null);

        entity.missionId = FileDownloader.getImpl()
                .create(entity.downloadUrl)
                .setPath(entity.getFilePath())
                .setCallbackProgressMinInterval(50)
                .setForceReDownload(true)
                .setListener(listener)
                .start();
        entity.result = RESULT_DOWNLOADING;
        registerDownloadListener(entity.missionId, listener);

        DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

        if (showSnackbar) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_download_start));
        }

        return entity.missionId;
    }

    @Override
    public long restartMission(Context c, @NonNull DownloadMissionEntity entity) {
        unregisterDownloadListener(entity.missionId);

        FileDownloader.getImpl().clear((int) entity.missionId, "");
        FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(entity.getFilePath()));

        DatabaseHelper.getInstance(c).deleteDownloadEntity(entity.missionId);

        return addMission(c, entity, true);
    }

    @Override
    public void removeMission(Context c, @NonNull DownloadMissionEntity entity, boolean deleteEntity) {
        if (entity.result != RESULT_SUCCEED) {
            unregisterDownloadListener(entity.missionId);
            FileDownloader.getImpl().clear((int) entity.missionId, "");
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(entity.getFilePath()));
        }
        if (deleteEntity) {
            DatabaseHelper.getInstance(c).deleteDownloadEntity(entity.missionId);
        }
    }

    @Override
    public void clearMission(Context c, @NonNull List<DownloadMissionEntity> entityList) {
        FileDownloader.getImpl().clearAllTaskData();
        DatabaseHelper.getInstance(c).clearDownloadEntity();
    }

    @Override
    public void updateMissionResult(Context c, @NonNull DownloadMissionEntity entity, int result) {
        entity.result = result;
        DatabaseHelper.getInstance(c).updateDownloadEntity(entity);
    }

    @Override
    public float getMissionProcess(Context c, @NonNull DownloadMissionEntity entity) {
        long soFar = FileDownloader.getImpl().getSoFar((int) entity.missionId);
        long total = FileDownloader.getImpl().getTotal((int) entity.missionId);
        float result = (float) (100.0 * soFar / total);
        result = Math.max(0, result);
        result = Math.min(100, result);
        return result;
    }

    @Override
    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        updateDownloadListener(l.missionId, l);
    }

    @Override
    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        updateDownloadListener(l.missionId, null);
    }

    private void downloadFinish(Context context, long missionId, boolean complete) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(context).readDownloadEntity(missionId);
        if (entity != null) {
            downloadFinish(context, entity, complete);
        }
    }

    private void downloadFinish(Context context, @NonNull DownloadMissionEntity entity, boolean complete) {
        if (complete) {
            updateMissionResult(context, entity, DownloaderService.RESULT_SUCCEED);
            context.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse(entity.getFilePath())));
            if (entity.downloadType != DownloaderService.COLLECTION_TYPE) {
                downloadPhotoSuccess(context, entity);
            } else {
                downloadCollectionSuccess(context, entity);
            }
        } else {
            FileDownloadUtils.deleteTempFile(FileDownloadUtils.getTempPath(entity.getFilePath()));
            updateMissionResult(context, entity, DownloaderService.RESULT_FAILED);
            if (entity.downloadType != DownloaderService.COLLECTION_TYPE) {
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
                    builder.build());

            runnable = new NotificationRunnable(builder);
            ThreadManager.getInstance().execute(runnable);
        }
    }

    private void updateDownloadListener(long missionId, @Nullable OnDownloadListener l) {
        for (int i = 0; i < listenerList.size(); i ++) {
            if (listenerList.get(i).id == missionId) {
                listenerList.get(i).outsideListener = l;
                break;
            }
        }
    }

    private void unregisterDownloadListener(long missionId) {
        for (int i = 0; i < listenerList.size(); i ++) {
            if (listenerList.get(i).id == missionId) {
                listenerList.remove(i);
                break;
            }
        }
        if (listenerList.size() == 0
                && runnable != null && runnable.isRunning()) {
            runnable.setRunning(false);
            runnable = null;

            FileDownloader.getImpl().stopForeground(true);
            NotificationHelper.removeDownloadingNotification(context);
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
                .setContentIntent(NotificationHelper.buildIntent(context));
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