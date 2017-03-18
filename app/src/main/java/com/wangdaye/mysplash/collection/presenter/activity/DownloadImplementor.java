package com.wangdaye.mysplash.collection.presenter.activity;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
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
    public void download(Context context) {
        Object key = getDownloadKey();
        if (key instanceof Collection) {
            DownloadHelper.getInstance(context).addMission(context, ((Collection) key));
        } else {
            DownloadHelper.getInstance(context).addMission(context, (Photo) key, DownloadHelper.DOWNLOAD_TYPE);
        }
    }

    @Override
    public void share(Context context) {
        // do nothing.
    }

    @Override
    public void setWallpaper(Context context) {
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
