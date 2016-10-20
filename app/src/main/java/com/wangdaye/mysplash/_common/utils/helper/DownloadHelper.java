package com.wangdaye.mysplash._common.utils.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;
import com.wangdaye.mysplash._common.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Download helper.
 * */

public class DownloadHelper {
    // data
    public List<DownloadMissionEntity> entityList;
    public List<Long> collectionIdList;

    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;

    private OnDownloadListener listener;

    /** <br> life cycle. */

    private DownloadHelper(Context c) {
        initEntityList(c);
        this.collectionIdList = new ArrayList<>();
    }

    private void initEntityList(Context c) {
        this.entityList = DatabaseHelper.getInstance(c).readDownloadEntity();

        DownloadManager manager = (DownloadManager) c.getSystemService(DOWNLOAD_SERVICE);
        for (int i = 0; i < entityList.size(); i ++) {
            Cursor cursor = manager.query(
                    new DownloadManager.Query()
                            .setFilterById(entityList.get(i).missionId));
            cursor.moveToFirst();
            int cursorCount = 0;
            try {
                cursorCount = cursor.getCount();
            } catch (CursorIndexOutOfBoundsException ignored) {

            }
            if (cursorCount == 0) {
                entityList.clear();
            } else if (DownloadManager.STATUS_SUCCESSFUL
                    == cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                entityList.remove(i --);
            }
        }
    }

    /** <br> data. */

    public void addMission(Context c, Photo p, int type) {
        for (int i = 0; i < entityList.size(); i ++) {
            if (entityList.get(i).photoId.equals(p.id)) {
                NotificationUtils.showSnackbar(
                        c.getString(R.string.feedback_download_repeat),
                        Snackbar.LENGTH_SHORT);
                return;
            }
        }

        DownloadMissionEntity entity = new DownloadMissionEntity(c, p, type);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                .setTitle(entity.photoId)
                .setDescription(c.getString(R.string.feedback_downloading))
                .setDestinationInExternalPublicDir(
                        Mysplash.DOWNLOAD_PATH,
                        entity.photoId + Mysplash.DOWNLOAD_FORMAT);
        request.allowScanningByMediaScanner();

        entity.missionId = ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
        entityList.add(entity);
        DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

        NotificationUtils.showSnackbar(
                c.getString(R.string.feedback_download_start),
                Snackbar.LENGTH_SHORT);
    }

    public void restartMission(Context c, int position) {
        DownloadMissionEntity entity = entityList.get(position);
        ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).remove(entity.missionId);
        DatabaseHelper.getInstance(c).deleteDownloadEntity(entity);
        entityList.remove(position);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.downloadUrl))
                .setTitle(entity.photoId)
                .setDescription(c.getString(R.string.feedback_downloading))
                .setDestinationInExternalPublicDir(
                        Mysplash.DOWNLOAD_PATH,
                        entity.photoId + Mysplash.DOWNLOAD_FORMAT);
        request.allowScanningByMediaScanner();

        entity.missionId = ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
        entityList.add(entity);
        DatabaseHelper.getInstance(c).writeDownloadEntity(entity);

        NotificationUtils.showSnackbar(
                c.getString(R.string.feedback_download_start),
                Snackbar.LENGTH_SHORT);
    }

    public void removeMission(Context c, long id) {
        for (int i = 0; i < entityList.size(); i ++) {
            if (entityList.get(i).missionId == id) {
                ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).remove(id);
                DatabaseHelper.getInstance(c).deleteDownloadEntity(entityList.get(i));
                entityList.remove(i);
                return;
            }
        }
    }

    public void clearMission(Context c) {
        for (int i = 0; i < entityList.size(); i ++) {
            ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).remove(entityList.get(i).missionId);
            DatabaseHelper.getInstance(c).deleteDownloadEntity(entityList.get(i));
            entityList.remove(i --);
        }
    }

    public void downloadCollection(Context c, Collection collection) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(collection.links.download))
                .setTitle("#" + collection.id)
                .setDescription(c.getString(R.string.feedback_downloading))
                .setDestinationInExternalPublicDir(
                        Mysplash.DOWNLOAD_PATH,
                        "#" + collection.id + Mysplash.DOWNLOAD_FORMAT);

        collectionIdList.add(((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).enqueue(request));

        NotificationUtils.showSnackbar(
                c.getString(R.string.feedback_download_start),
                Snackbar.LENGTH_SHORT);
    }

    public void refreshEntityList() {
        DownloadManager manager = (DownloadManager) Mysplash
                .getInstance()
                .getSystemService(DOWNLOAD_SERVICE);
        for (int i = 0; i < entityList.size(); i ++) {
            Cursor cursor = manager.query(
                    new DownloadManager.Query()
                            .setFilterById(entityList.get(i).missionId));
            cursor.moveToFirst();

            int statusNow = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (entityList.get(i).downloadStatus != statusNow) {
                switch (statusNow) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        // do nothing.
                        break;

                    case DownloadManager.STATUS_FAILED:
                        entityList.get(i).downloadStatus = statusNow;
                        DatabaseHelper
                                .getInstance(Mysplash.getInstance())
                                .updateDownloadEntity(entityList.get(i));
                        if (listener != null) {
                            listener.onFailed(entityList.get(i).missionId, i);
                        }
                        break;

                    default:
                        if (listener != null) {
                            listener.onProcess(entityList.get(i).missionId, i);
                        }
                        break;
                }
            } else if (statusNow == DownloadManager.STATUS_RUNNING) {
                if (listener != null) {
                    listener.onProcess(entityList.get(i).missionId, i);
                }
            }
        }
    }

    public void downloadPhotoSuccess(int position) {
        DatabaseHelper
                .getInstance(Mysplash.getInstance())
                .deleteDownloadEntity(entityList.get(position));
        long id = entityList.get(position).missionId;
        entityList.remove(position);
        if (listener != null) {
            listener.onSuccess(id, position);
        }
    }

    /** <br> singleton. */

    private static DownloadHelper instance;

    public static DownloadHelper getInstance(Context c) {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                if (instance == null) {
                    instance = new DownloadHelper(c);
                }
            }
        }
        return instance;
    }

    public static DownloadHelper refresh(Context c) {
        instance = new DownloadHelper(c);
        return instance;
    }

    /** <br> interface. */

    public interface OnDownloadListener {
        void onProcess(long id, int position);
        void onSuccess(long id, int position);
        void onFailed(long id, int position);
    }

    public void setOnDownloadListener(OnDownloadListener l) {
        listener = l;
    }
}
