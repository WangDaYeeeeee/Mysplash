package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Toolbar presenter.
 * */

public interface ToolbarPresenter {

    void touchNavigatorIcon(Activity a);
    void touchToolbar(Activity a);
    boolean touchMenuItem(Activity a, int itemId);
}
