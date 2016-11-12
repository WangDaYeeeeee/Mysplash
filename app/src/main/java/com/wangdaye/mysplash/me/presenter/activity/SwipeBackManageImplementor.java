package com.wangdaye.mysplash.me.presenter.activity;

import android.app.Activity;
import android.os.Build;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

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
        SwipeBackCoordinatorLayout.hideBackgroundShadow(((MeActivity) a).getSnackbarContainer());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.finishAfterTransition();
        } else {
            a.finish();
            switch (dir) {
                case SwipeBackCoordinatorLayout.UP_DIR:
                    a.overridePendingTransition(0, R.anim.activity_slide_out_top);
                    break;

                case SwipeBackCoordinatorLayout.DOWN_DIR:
                    a.overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                    break;
            }
        }
    }
}

