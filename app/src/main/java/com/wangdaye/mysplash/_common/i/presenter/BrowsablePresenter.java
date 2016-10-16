package com.wangdaye.mysplash._common.i.presenter;

import android.net.Uri;

/**
 * Borwsable presenter.
 * */

public interface BrowsablePresenter {

    Uri getIntentUri();
    boolean isBrowsable();

    void requestBrowsableData();
    void drawBrowsableView();
    void visitParentView();

    void cancelRequest();
}
