package com.wangdaye.mysplash.category.presenter.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.category.view.activity.CategoryActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        a.finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        ((CategoryActivity) a).touchToolbar();
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_filter:
                ((CategoryActivity) a).showPopup();
                break;
        }
        return true;
    }
}
