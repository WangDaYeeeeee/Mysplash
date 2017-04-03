package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;

/**
 * Photo info presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PhotoInfoView}.
 *
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
