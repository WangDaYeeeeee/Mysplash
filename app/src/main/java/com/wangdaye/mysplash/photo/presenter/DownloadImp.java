package com.wangdaye.mysplash.photo.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.tools.DownloadTools;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.photo.model.DownloadObject;
import com.wangdaye.mysplash.photo.model.i.DownloadModel;
import com.wangdaye.mysplash.photo.model.i.PhotoModel;
import com.wangdaye.mysplash.photo.presenter.i.DownloadPresenter;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.photo.view.activity.i.DownloadView;

import java.io.File;
import java.util.List;

/**
 * Download implementor.
 * */

public class DownloadImp
        implements DownloadPresenter {
    // model.
    private PhotoModel photoModel;
    private DownloadModel downloadModel;

    // view.
    private DownloadView downloadView;

    /** <br> life cycle. */

    public DownloadImp(PhotoModel photoModel, DownloadModel downloadModel,
                       DownloadView downloadView) {
        this.photoModel = photoModel;
        this.downloadModel = downloadModel;
        this.downloadView = downloadView;
    }

    /** <br> presenter. */

    // download.

    @Override
    public void download(Context c) {
        downloadModel.setDownloadType(DownloadObject.SIMPLE_DOWNLOAD_TYPE);
        doDownload(c);
    }

    @Override
    public void share(Context c) {
        downloadModel.setDownloadType(DownloadObject.SHARE_DOWNLOAD_TYPE);
        doDownload(c);
    }

    @Override
    public void setWallpaper(Context c) {
        downloadModel.setDownloadType(DownloadObject.WALL_DOWNLOAD_TYPE);
        doDownload(c);
    }

    private void doDownload(Context c) {
        if (FileUtils.createFile(c)) {
            downloadModel.setDownloading(true);
            downloadModel.setDialogShowing(true);
            downloadView.showDialog();

            Uri downloadUri = Uri.parse(photoModel.selectDownloadUrl());
            Uri fileUri = Uri.parse(Mysplash.DOWNLOAD_PATH + photoModel.getPhotoId() + Mysplash.DOWNLOAD_FORMAT);
            DownloadRequest request = new DownloadRequest(downloadUri)
                    .setDestinationURI(fileUri)
                    .setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadListener(photoModel.getPhotoId()));

            int id = DownloadTools.getInstance().add(request);
            downloadModel.setDownloadId(id);
        }
    }

    // dialog.

    @Override
    public void dismissDialog() {
        downloadModel.setDialogShowing(false);
    }

    @Override
    public void cancelDownload(Context c) {
        downloadModel.setDialogShowing(false);
        if (downloadModel.isDownloading()) {
            downloadModel.setDownloading(false);
            if (DownloadTools.getInstance()
                    .cancel(downloadModel.getDownloadId()) == 1) {
                Toast.makeText(
                        c,
                        c.getString(R.string.feedback_download_cancel) + "\n" + photoModel.getPhotoId(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // id.

    @Override
    public int getDownloadId() {
        return downloadModel.getDownloadId();
    }

    /** <br> interface. */

    private class DownloadListener implements DownloadStatusListener {
        // data
        private String id;

        public DownloadListener(String id) {
            this.id = id;
        }

        @Override
        public void onDownloadComplete(int i) {
            List<Activity> activityList = Mysplash.getInstance().getActivityList();
            if (activityList.size() > 0
                    && activityList.get(activityList.size() - 1) instanceof PhotoActivity) {
                if (downloadModel.isDialogShowing()
                        && ((PhotoActivity) activityList.get(activityList.size() - 1)).getDownloadId() == i) {
                    downloadView.dismissDialog();
                }
            }

            Uri file = Uri.fromFile(new File(Mysplash.DOWNLOAD_PATH + id + Mysplash.DOWNLOAD_FORMAT));
            Intent broadcast = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, file);
            Mysplash.getInstance().sendBroadcast(broadcast);
            switch (downloadModel.getDownloadType()) {
                case DownloadObject.SIMPLE_DOWNLOAD_TYPE:
                    break;

                case DownloadObject.SHARE_DOWNLOAD_TYPE: {
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

                case DownloadObject.WALL_DOWNLOAD_TYPE: {
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
            Toast.makeText(
                    Mysplash.getInstance(),
                    Mysplash.getInstance()
                            .getString(R.string.feedback_download_success) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();
            downloadModel.setDownloading(false);
        }

        @Override
        public void onDownloadFailed(int i, int i1, String s) {
            List<Activity> activityList = Mysplash.getInstance().getActivityList();
            if (activityList.size() > 0
                    && activityList.get(activityList.size() - 1) instanceof PhotoActivity) {
                if (downloadModel.isDialogShowing()
                        && ((PhotoActivity) activityList.get(activityList.size() - 1)).getDownloadId() == i) {
                    downloadView.dismissDialog();
                }
            }
            Toast.makeText(
                    Mysplash.getInstance(),
                    Mysplash.getInstance().
                            getString(R.string.feedback_download_failed) + "\n" + "ID = " + id,
                    Toast.LENGTH_SHORT).show();
            downloadModel.setDownloading(false);
        }

        @Override
        public void onProgress(int i, long l, long l1, int i1) {
            List<Activity> activityList = Mysplash.getInstance().getActivityList();
            if (activityList.size() > 0
                    && activityList.get(activityList.size() - 1) instanceof PhotoActivity) {
                if (downloadModel.isDialogShowing()
                        && ((PhotoActivity) activityList.get(activityList.size() - 1)).getDownloadId() == i) {
                    downloadView.setDialogProgress(i1);
                }
            }
        }
    }
}
