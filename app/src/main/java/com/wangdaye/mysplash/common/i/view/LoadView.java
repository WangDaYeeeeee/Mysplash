package com.wangdaye.mysplash.common.i.view;

import android.view.View;

/**
 * Load view.
 *
 * A view which can control multiple display states.
 *
 * */

public interface LoadView {

    void animShow(View v);
    void animHide(View v);

    void setLoadingState();
    void setFailedState();
    void setNormalState();
    void resetLoadingState();
}
