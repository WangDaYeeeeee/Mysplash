package com.wangdaye.mysplash.photo.presenter.activity;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;

/**
 * Download implementor.
 * */

public class DownloadImplementor
        implements DownloadPresenter {
    // model & view.
    private DownloadModel model;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    /** <br> presenter. */

    @Override
    public void download() {
        doDownload(DownloadHelper.DOWNLOAD_TYPE);
    }

    @Override
    public void share() {
        doDownload(DownloadHelper.SHARE_TYPE);
    }

    @Override
    public void setWallpaper() {
        doDownload(DownloadHelper.WALLPAPER_TYPE);
    }

    /** <br> utils. */

    private void doDownload(int type) {
        Activity a = Mysplash.getInstance().getTopActivity();
        Photo p = (Photo) model.getDownloadKey();
        if (DatabaseHelper.getInstance(a).readDownloadEntityCount(p.id) == 0) {
            DownloadHelper.getInstance(a)
                    .addMission(a, p, type);
        } else {
            NotificationUtils.showSnackbar(
                    a.getString(R.string.feedback_download_repeat),
                    Snackbar.LENGTH_SHORT);
        }
    }
}