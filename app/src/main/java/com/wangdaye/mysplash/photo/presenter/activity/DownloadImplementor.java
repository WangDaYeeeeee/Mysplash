package com.wangdaye.mysplash.photo.presenter.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.tools.DownloadManager;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.view.DownloadView;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash.photo.model.activity.DownloadObject;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.io.File;
import java.util.List;

/**
 * Download implementor.
 * */

public class DownloadImplementor
        implements DownloadPresenter {
    // model & view.
    private DownloadModel model;
    private DownloadView view;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model, DownloadView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void download() {
        model.setDownloadType(DownloadObject.DOWNLOAD_TYPE);
        doDownload();
    }

    @Override
    public void share() {
        model.setDownloadType(DownloadObject.SHARE_TYPE);
        doDownload();
    }

    @Override
    public void setWallpaper() {
        model.setDownloadType(DownloadObject.WALLPAPER_TYPE);
        doDownload();
    }

    @Override
    public void setDialogShowing(boolean showing) {
        model.setDialogShowing(showing);
    }

    @Override
    public void cancelDownloading() {
        DownloadManager.getInstance().cancel(model.getDownloadId());
        model.setDownloading(false);
    }

    /** <br> utils. */

    private void doDownload() {
        if (FileUtils.createFile(Mysplash.getInstance())) {
            model.setDownloading(true);
            model.setDialogShowing(true);
            view.showDownloadDialog();

            Photo p = (Photo) model.getDownloadKey();
            Uri downloadUri;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Mysplash.getInstance());
            String scaleType = sharedPreferences.getString(
                    Mysplash.getInstance().getString(R.string.key_download_scale),
                    Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0]);
            if (scaleType.equals(Mysplash.getInstance().getResources().getStringArray(R.array.download_type_values)[0])) {
                downloadUri = Uri.parse(p.urls.full);
            } else {
                downloadUri = Uri.parse(p.urls.raw);
            }

            Uri fileUri = Uri.parse(Mysplash.DOWNLOAD_PATH + p.id + Mysplash.DOWNLOAD_FORMAT);
            DownloadRequest request = new DownloadRequest(downloadUri)
                    .setDestinationURI(fileUri)
                    .setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadListener(p.id, model));

            int id = DownloadManager.getInstance().add(request);
            model.setDownloadId(id);
        }
    }

    /** <br> interface. */

    private class DownloadListener
            implements DownloadStatusListener, MaterialToast.OnActionClickListener {
        // data
        private String id;
        private DownloadModel downloadModel;

        public DownloadListener(String id, DownloadModel downloadModel) {
            this.id = id;
            this.downloadModel = downloadModel;
        }

        @Override
        public void onDownloadComplete(int i) {
            PhotoActivity a = getPhotoActivity();
            if (a != null && a.getDownloadId() == i
                    && downloadModel.isDialogShowing()) {
                a.dismissDownloadDialog();
            }

            Uri file = Uri.fromFile(new File(Mysplash.DOWNLOAD_PATH + id + Mysplash.DOWNLOAD_FORMAT));
            Intent broadcast = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, file);
            Mysplash.getInstance().sendBroadcast(broadcast);
            switch (downloadModel.getDownloadType()) {
                case DownloadObject.DOWNLOAD_TYPE:
                    break;

                case DownloadObject.SHARE_TYPE: {
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

                case DownloadObject.WALLPAPER_TYPE: {
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

            Context c = Mysplash.getInstance().getActivityList().get(0);
            MaterialToast.makeText(
                    c,
                    c.getString(R.string.feedback_download_success),
                    c.getString(R.string.feedback_download_check),
                    MaterialToast.LENGTH_SHORT)
                    .setOnActionClickListener(this)
                    .show();
            downloadModel.setDownloading(false);
        }

        @Override
        public void onDownloadFailed(int i, int i1, String s) {
            PhotoActivity a = getPhotoActivity();
            if (a != null && a.getDownloadId() == i
                    && downloadModel.isDialogShowing()) {
                a.dismissDownloadDialog();
            }

            Context c = Mysplash.getInstance().getActivityList().get(0);
            MaterialToast.makeText(
                    c,
                    c.getString(R.string.feedback_download_failed) + " (" + id + ")",
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            downloadModel.setDownloading(false);
        }

        @Override
        public void onProgress(int i, long l, long l1, int i1) {
            PhotoActivity a = getPhotoActivity();
            if (a != null && a.getDownloadId() == i
                    && downloadModel.isDialogShowing()) {
                a.onDownloadProcess(i1);
            }
        }

        private PhotoActivity getPhotoActivity() {
            List<Activity> list = Mysplash.getInstance().getActivityList();
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i) instanceof PhotoActivity) {
                    return (PhotoActivity) list.get(i);
                }
            }
            return null;
        }

        @Override
        public void onActionClick() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
            intent.setDataAndType(Uri.fromFile(file), "file/*");

            Context c = Mysplash.getInstance().getActivityList().get(0);
            c.startActivity(Intent.createChooser(intent, c.getString(R.string.feedback_download_check)));
        }
    }
}
