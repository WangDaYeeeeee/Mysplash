package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter3;

/**
 * Photo info presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PhotoInfoView}.
 *
 * */

public interface PhotoInfoPresenter3 {

    void requestPhoto(Context context);
    void setLikeForAPhoto(Context context);
    void cancelRequest();

    void touchMenuItem(int itemId);

    @Nullable
    Photo getPhoto();
    void setPhoto(Photo photo, boolean init);

    PhotoInfoAdapter3 getAdapter();

    boolean isFailed();
}
