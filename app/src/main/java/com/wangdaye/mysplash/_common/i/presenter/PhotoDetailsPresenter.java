package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

/**
 * Photo details presenter.
 * */

public interface PhotoDetailsPresenter {

    void requestPhotoDetails(Context c);
    void cancelRequest();

    void showExifDescription(Context c, String title, String content);
}
