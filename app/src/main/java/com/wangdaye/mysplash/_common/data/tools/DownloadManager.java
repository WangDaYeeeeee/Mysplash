package com.wangdaye.mysplash._common.data.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Download manager.
 * */

public class DownloadManager {
    // widget
    private ThinDownloadManager manager;

    // data
    private List<Mission> missionList;
    public static final int CANCELED_CODE = 1008;
    public static final int DOWNLOAD_TYPE = 1;
    public static final int SHARE_TYPE = 2;
    public static final int WALLPAPER_TYPE = 3;

    /** <br> life cycle. */

    private DownloadManager() {
        this.manager = new ThinDownloadManager();
        this.missionList = new ArrayList<>();
    }

    /** <br> data. */

    public int add(Photo p, int type, OnDownloadListener l) {
        Mission m = new Mission(p, type);
        m.id = manager.add(m.request);
        m.addOnDownloadListener(l);
        missionList.add(m);
        return m.id;
    }

    public int cancel(int id) {
        for (int i = 0; i < missionList.size(); i ++) {
            if (missionList.get(i).id == id) {
                missionList.remove(i);
                break;
            }
        }
        return manager.cancel(id);
    }

    public void cancelAll() {
        missionList.clear();
        manager.cancelAll();
    }

    public void deleteMission(Mission m) {
        missionList.remove(m);
    }

    public void retry(int downloadId, OnDownloadListener l) {
        for (int i = 0; i < missionList.size(); i ++) {
            if (missionList.get(i).id == downloadId) {
                Photo p = missionList.get(i).photo;
                int type = missionList.get(i).downloadType;
                missionList.remove(i);
                add(p, type, l);
                return;
            }
        }
    }

    /** <br> interface. */

    public interface OnDownloadListener {
        void onDownloadComplete(int id);
        void onDownloadFailed(int id, int code);
        void onDownloadProgress(int id, int percent);
    }

    public void addOnDownloadListener(int downloadId, OnDownloadListener l) {
        for (int i = 0; i < missionList.size(); i ++) {
            if (missionList.get(i).id == downloadId) {
                missionList.get(i).addOnDownloadListener(l);
                return;
            }
        }
    }

    public void addOnDownloadListener(OnDownloadListener l) {
        for (int i = 0; i < missionList.size(); i ++) {
            missionList.get(i).addOnDownloadListener(l);
        }
    }

    public void removeDownloadListener(OnDownloadListener l) {
        for (int i = 0; i < missionList.size(); i ++) {
            missionList.get(i).removeOnDownloadListener(l);
        }
    }

    /** <br> singleton. */

    private static DownloadManager instance;

    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    /** <br> inner class. */

    public class Mission implements DownloadStatusListener, MaterialToast.OnActionClickListener {
        // data
        public int id = -1;
        public Photo photo;
        public int downloadType;
        public boolean failed = false;
        public DownloadRequest request;
        public List<OnDownloadListener> listenerList;

        // life cycle.

        public Mission(Photo p, int type) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Mysplash.getInstance());
            String scaleType = sharedPreferences.getString(
                    Mysplash.getInstance().getString(R.string.key_download_scale),
                    Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0]);

            Uri downloadUri;
            if (scaleType.equals(Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0])) {
                downloadUri = Uri.parse(p.urls.full);
            } else {
                downloadUri = Uri.parse(p.urls.raw);
            }
            Uri fileUri = Uri.parse(Mysplash.DOWNLOAD_PATH + p.id + Mysplash.DOWNLOAD_FORMAT);

            this.request = new DownloadRequest(downloadUri)
                    .setDestinationURI(fileUri)
                    .setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(this);
            this.photo = p;
            this.downloadType = type;
            this.listenerList = new ArrayList<>();
        }

        // interface.

        public void addOnDownloadListener(OnDownloadListener l) {
            listenerList.add(l);
        }

        public void removeOnDownloadListener(OnDownloadListener l) {
            listenerList.remove(l);
        }

        @Override
        public void onDownloadComplete(int id) {
            for (int i = 0; i < listenerList.size(); i ++) {
                listenerList.get(i).onDownloadComplete(id);
            }
            listenerList.clear();

            Uri file = Uri.parse(Mysplash.DOWNLOAD_PATH + photo.id + Mysplash.DOWNLOAD_FORMAT);
            Intent broadcast = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, file);
            Mysplash.getInstance().sendBroadcast(broadcast);
            switch (downloadType) {
                case DOWNLOAD_TYPE:
                    Context c = Mysplash.getInstance().getActivityList().get(0);
                    MaterialToast.makeText(
                            c,
                            c.getString(R.string.feedback_download_success),
                            c.getString(R.string.check),
                            MaterialToast.LENGTH_SHORT)
                            .setOnActionClickListener(this)
                            .show();
                    break;

                case SHARE_TYPE: {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, file);
                    intent.setType("image/*");
                    List<Activity> list = Mysplash.getInstance().getActivityList();
                    list.get(list.size() - 1).startActivity(
                            Intent.createChooser(
                                    intent,
                                    Mysplash.getInstance().getString(R.string.feedback_choose_share_app)));
                    break;
                }

                case WALLPAPER_TYPE: {
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.setDataAndType(file, "image/jpg");
                    intent.putExtra("mimeType", "image/jpg");
                    List<Activity> list = Mysplash.getInstance().getActivityList();
                    list.get(list.size() - 1).startActivity(
                            Intent.createChooser(
                                    intent,
                                    Mysplash.getInstance().getString(R.string.feedback_choose_wallpaper_app)));
                    break;
                }
            }
            deleteMission(this);
        }

        @Override
        public void onDownloadFailed(int id, int i1, String s) {
            for (int i = 0; i < listenerList.size(); i ++) {
                listenerList.get(i).onDownloadFailed(id, i1);
            }
            if (i1 == CANCELED_CODE) {
                listenerList.clear();
                deleteMission(this);
            } else {
                failed = true;
                Context c = Mysplash.getInstance().getActivityList().get(0);
                MaterialToast.makeText(
                        c,
                        c.getString(R.string.feedback_download_failed),
                        c.getString(R.string.check),
                        MaterialToast.LENGTH_SHORT)
                        .setOnActionClickListener(this)
                        .show();
            }
        }

        @Override
        public void onProgress(int id, long l, long l1, int i1) {
            for (int i = 0; i < listenerList.size(); i ++) {
                listenerList.get(i).onDownloadProgress(id, i1);
            }
        }

        @Override
        public void onActionClick() {
            if (failed) {
                Context c = Mysplash.getInstance().getActivityList().get(0);
                Intent intent = new Intent(c, DownloadManageActivity.class);
                c.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Uri file = Uri.parse(Mysplash.DOWNLOAD_PATH + photo.id + Mysplash.DOWNLOAD_FORMAT);
                intent.setDataAndType(file, "image/*");

                Context c = Mysplash.getInstance().getActivityList().get(0);
                c.startActivity(intent);
            }
        }
    }
}
