package com.wangdaye.mysplash.main.presenter.widget.i;

import android.content.Context;

/**
 * Request data presenter.
 * */

public interface RequestDataPresenter {

    void requestPhotos(Context c, boolean refresh);
    void searchPhotos(Context c, boolean refresh);
    void requestPhotosInCategory(Context c, boolean refresh);
    void cancelRequest();
}
