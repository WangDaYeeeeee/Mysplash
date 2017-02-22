package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;

/**
 * Photo info presenter.
 * */

public interface PhotoInfoPresenter {

    void requestPhoto(Context context);
    void cancelRequest();

    void touchMenuItem(int itemId);

    Photo getPhoto();
    PhotoInfoAdapter getAdapter();

    boolean isFailed();
    void setFailed(boolean b);
}
