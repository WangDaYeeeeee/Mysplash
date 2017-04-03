package com.wangdaye.mysplash.common.i.presenter;

import android.net.Uri;

/**
 * Borwsable presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.BrowsableView}.
 *
 * */

public interface BrowsablePresenter {

    Uri getIntentUri();
    boolean isBrowsable();

    /**
     * When an activity were opened from a web url, there might be only 1 activity in the task.
     * That's means the activity is the root of task. So if user doesn't want to quit the application,
     * this method can open the previous activity.
     * */
    void visitPreviousPage();

    void requestBrowsableData();
    void cancelRequest();
}
