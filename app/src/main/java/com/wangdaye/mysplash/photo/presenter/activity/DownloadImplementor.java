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
        implements DownloadPresenter, DownloadManager.OnDownloadListener {
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
        doDownload(DownloadManager.DOWNLOAD_TYPE);
    }

    @Override
    public void share() {
        model.setDownloadType(DownloadObject.SHARE_TYPE);
        doDownload(DownloadManager.SHARE_TYPE);
    }

    @Override
    public void setWallpaper() {
        model.setDownloadType(DownloadObject.WALLPAPER_TYPE);
        doDownload(DownloadManager.WALLPAPER_TYPE);
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

    @Override
    public int getDownloadId() {
        return model.getDownloadId();
    }

    @Override
    public void setDownloadId(int id) {
        model.setDownloadId(id);
    }

    /** <br> utils. */

    private void doDownload(int type) {
        if (FileUtils.createFile(Mysplash.getInstance())) {
            model.setDownloading(true);
            model.setDialogShowing(true);
            view.showDownloadDialog();

            int id = DownloadManager.getInstance().add(
                    (Photo) model.getDownloadKey(),
                    type,
                    this);
            model.setDownloadId(id);
        }
    }

    /** <br> interface. */

    @Override
    public void onDownloadComplete(int id) {
        if (model.getDownloadId() == id && model.isDialogShowing()) {
            view.dismissDownloadDialog();
            model.setDownloading(false);
        }
    }

    @Override
    public void onDownloadFailed(int id, int code) {
        if (model.getDownloadId() == id && model.isDialogShowing()) {
            view.dismissDownloadDialog();
            model.setDownloading(false);
        }
    }

    @Override
    public void onDownloadProgress(int id, int percent) {
        if (model.getDownloadId() == id && model.isDialogShowing()) {
            view.onDownloadProcess(percent);
        }
    }
}
