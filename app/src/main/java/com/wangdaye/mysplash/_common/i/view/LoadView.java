package com.wangdaye.mysplash._common.i.view;

import android.view.View;

/**
 * Load view.
 * */

public interface LoadView {

    void animShow(View v);
    void animHide(View v);

    void setLoadingState();
    void setFailedState();
    void setNormalState();
    void resetLoadingState();
}
