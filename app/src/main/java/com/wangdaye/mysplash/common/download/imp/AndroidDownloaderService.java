package com.wangdaye.mysplash.common.download.imp;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FlagRunnable;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;

import java.util.List;

public class AndroidDownloaderService extends AbstractDownloaderService {

    private static AndroidDownloaderService instance;

    public static AndroidDownloaderService getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (AndroidDownloaderService.class) {
                if (instance == null) {
                    instance = new AndroidDownloaderService(context);
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
                synchronizedExecuteRunnable(listenerList -> {
                    for (int i = listenerList.size() - 1; i >= 0; i --) {
                        if (listenerList.get(i).result != DownloadMissionEntity.RESULT_DOWNLOADING) {
                            listenerList.remove(i);
                            if (listenerList.size() == 0) {
                                setRunning(false);
                                runnable = null;
                                return;
                            }
                        } else {
                            final PollingResult targetResult = getDownloadInformation(listenerList.get(i).missionId);
                            final OnDownloadListener targetListener = listenerList.get(i);
                            handler.post(() -> {
                                targetListener.result = targetResult.result;
                                if (targetResult.result == DownloadMissionEntity.RESULT_DOWNLOADING) {
                                    targetListener.onProcess(targetResult.process);
                                } else {
                                    targetListener.onComplete(targetResult.result);
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

        @DownloadMissionEntity.DownloadResultRule int result;
        float process;

        PollingResult(@DownloadMissionEntity.DownloadResultRule int result, float process) {
            this.result = result;
            this.process = process;
        }
    }

    private AndroidDownloaderService(Context context) {
        super();

        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public long addMission(Context c, @NonNull DownloadMissionEntity entity, boolean showSnackbar) {
        if (downloadManager == null) {
            showErrorNotification();
            return -1;
        }

        synchronizedExecuteRunnable(listenerList -> {
            FileUtils.deleteFile(entity);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                    .setTitle(entity.getNotificationTitle())
                    .setDescription(c.getString(R.string.feedback_downloading))
                    .setDestinationInExternalPublicDir(
                            Mysplash.DOWNLOAD_PATH,
                            entity.title + entity.getFormat()
                    );
            request.allowScanningByMediaScanner();

            entity.missionId = downloadManager.enqueue(request);
            entity.result = DownloadMissionEntity.RESULT_DOWNLOADING;
            DatabaseHelper.getInstance(c).writeDownloadEntity(entity);
        });

        if (showSnackbar) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_download_start));
        }

        return entity.missionId;
    }

    @Override
    public long restartMission(Context c, @NonNull DownloadMissionEntity entity) {
        if (downloadManager == null) {
            showErrorNotification();
            return -1;
        }

        synchronizedExecuteRunnable(listenerList -> {
            downloadManager.remove(entity.missionId);
            DatabaseHelper.getInstance(c).deleteDownloadEntity(entity.missionId);
        });

        return addMission(c, entity, true);
    }

    @Override
    public void completeMission(Context c, @NonNull DownloadMissionEntity entity) {
        if (isMissionSuccess(entity.missionId)) {
            if (entity.downloadType != DownloadMissionEntity.COLLECTION_TYPE) {
                downloadPhotoSuccess(c, entity);
            } else {
                downloadCollectionSuccess(c, entity);
            }
            updateMissionResult(c, entity, DownloadMissionEntity.RESULT_SUCCEED);
        } else {
            if (entity.downloadType != DownloadMissionEntity.COLLECTION_TYPE) {
                downloadPhotoFailed(c, entity);
            } else {
                downloadCollectionFailed(c, entity);
            }
            updateMissionResult(c, entity, DownloadMissionEntity.RESULT_FAILED);
        }
    }

    @Override
    public void removeMission(Context c, @NonNull DownloadMissionEntity entity, boolean deleteEntity) {
        if (downloadManager == null) {
            showErrorNotification();
            return;
        }

        synchronizedExecuteRunnable(listenerList -> {
            if (entity.result != DownloadMissionEntity.RESULT_SUCCEED) {
                downloadManager.remove(entity.missionId);
            }
            if (deleteEntity) {
                DatabaseHelper.getInstance(c).deleteDownloadEntity(entity.missionId);
            }
        });
    }

    @Override
    public void clearMission(Context c, @NonNull List<DownloadMissionEntity> entityList) {
        if (downloadManager == null) {
            showErrorNotification();
            return;
        }

        synchronizedExecuteRunnable(listenerList -> {
            for (int i = 0; i < entityList.size(); i ++) {
                if (entityList.get(i).result != DownloadMissionEntity.RESULT_SUCCEED) {
                    downloadManager.remove(entityList.get(i).missionId);
                }
            }

            DatabaseHelper.getInstance(c).clearDownloadEntity();
        });
    }

    @Override
    public void updateMissionResult(Context c, @NonNull DownloadMissionEntity entity, int result) {
        entity.result = result;
        synchronizedExecuteRunnable(listenerList ->
                DatabaseHelper.getInstance(c).updateDownloadEntity(entity)
        );
    }

    @Override
    public float getMissionProcess(Context c, @NonNull DownloadMissionEntity entity) {
        Cursor cursor = getMissionCursor(entity.missionId);
        if (cursor != null) {
            float process = getMissionProcess(cursor);
            cursor.close();
            return process;
        }
        return -1;
    }

    @Override
    public void addOnDownloadListener(@NonNull OnDownloadListener l) {
        super.addOnDownloadListener(l, listenerList -> {
            if (runnable == null || !runnable.isRunning()) {
                runnable = new PollingRunnable();
                ThreadManager.getInstance().execute(runnable);
            }
        });
    }

    @Override
    public void addOnDownloadListener(@NonNull List<OnDownloadListener> list) {
        super.addOnDownloadListener(list, listenerList -> {
            if (runnable == null || !runnable.isRunning()) {
                runnable = new PollingRunnable();
                ThreadManager.getInstance().execute(runnable);
            }
        });
    }

    @Override
    public void removeOnDownloadListener(@NonNull OnDownloadListener l) {
        super.removeOnDownloadListener(l, listenerList -> {
            if (listenerList.size() == 0 && runnable != null && runnable.isRunning()) {
                runnable.setRunning(false);
                runnable = null;
            }
        });
    }

    @Override
    public void removeOnDownloadListener(@NonNull List<OnDownloadListener> list) {
        super.removeOnDownloadListener(list, listenerList -> {
            if (listenerList.size() == 0 && runnable != null && runnable.isRunning()) {
                runnable.setRunning(false);
                runnable = null;
            }
        });
    }

    private void showErrorNotification() {
        NotificationHelper.showSnackbar("Cannot get DownloadManager.");
    }

    private boolean isMissionSuccess(long id) {
        Cursor cursor = getMissionCursor(id);
        if (cursor != null) {
            int result = getDownloadResult(cursor);
            cursor.close();
            return result == DownloadMissionEntity.RESULT_SUCCEED;
        } else {
            return true;
        }
    }

    @DownloadMissionEntity.DownloadResultRule
    private int getDownloadResult(@NonNull Cursor cursor) {
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_SUCCESSFUL:
                return DownloadMissionEntity.RESULT_SUCCEED;

            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
                return DownloadMissionEntity.RESULT_FAILED;

            default:
                return DownloadMissionEntity.RESULT_DOWNLOADING;
        }
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

    private PollingResult getDownloadInformation(long missionId) {
        Cursor cursor = getMissionCursor(missionId);
        if (cursor != null) {
            PollingResult result = new PollingResult(
                    getDownloadResult(cursor),
                    getMissionProcess(cursor)
            );
            cursor.close();
            return result;
        }
        return new PollingResult(DownloadMissionEntity.RESULT_SUCCEED, 100);
    }

    private float getMissionProcess(@NonNull Cursor cursor) {
        long soFar = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        long total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int result = (int) (100.0 * soFar / total);
        result = Math.max(0, result);
        result = Math.min(100, result);
        return result;
    }
}
