package com.wangdaye.mysplash.user.presenter.activity;

import android.app.Activity;
import android.os.Build;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Swipe back manage implementor.
 * */

public class SwipeBackManageImplementor
        implements SwipeBackManagePresenter {
    // model & view.
    private SwipeBackManageView view;

    /** <br> life cycle. */

    public SwipeBackManageImplementor(SwipeBackManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public boolean checkCanSwipeBack(int dir) {
        return view.checkCanSwipeBack(dir);
    }

    @Override
    public void swipeBackFinish(Activity a, int dir) {
        UserActivity activity = (UserActivity) a;
        SwipeBackCoordinatorLayout.hideBackgroundShadow(activity.getSnackbarContainer());
        if (!activity.isBrowsable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
            switch (dir) {
                case SwipeBackCoordinatorLayout.UP_DIR:
                    activity.overridePendingTransition(0, R.anim.activity_slide_out_top);
                    break;

                case SwipeBackCoordinatorLayout.DOWN_DIR:
                    activity.overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                    break;
            }
        }
    }
}

