package com.wangdaye.mysplash.me.presenter.activity;

import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;

/**
 * Download implementor.
 * */

public class DownloadImplementor implements DownloadPresenter {
    // model & view.
    private DownloadModel model;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    /** <br> presenter. */

    @Override
    public void download() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        Photo p = (Photo) model.getDownloadKey();
        if (DatabaseHelper.getInstance(a).readDownloadEntityCount(p.id) == 0) {
            DownloadHelper.getInstance(a).addMission(a, p, DownloadHelper.DOWNLOAD_TYPE);
        } else {
            NotificationUtils.showSnackbar(
                    a.getString(R.string.feedback_download_repeat),
                    Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void share() {
        // do nothing.
    }

    @Override
    public void setWallpaper() {
        // do nothing.
    }

    @Override
    public Object getDownloadKey() {
        return model.getDownloadKey();
    }

    @Override
    public void setDownloadKey(Object key) {
        model.setDownloadKey(key);
    }
}
