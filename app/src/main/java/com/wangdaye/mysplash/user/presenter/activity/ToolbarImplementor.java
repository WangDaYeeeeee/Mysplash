package com.wangdaye.mysplash.user.presenter.activity;

import android.text.TextUtils;
import android.widget.Toast;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

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
        // do nothing.
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        UserActivity activity = (UserActivity) a;

        switch (itemId) {
            case R.id.action_open_portfolio:
                String url = activity.getUserPortfolio();
                if (!TextUtils.isEmpty(url)) {
                    IntentHelper.startWebActivity(a, url, true);
                } else {
                    Toast.makeText(
                            activity,
                            a.getString(R.string.feedback_portfolio_is_null),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_share:
                ShareUtils.shareUser(activity.getUser());
                break;

            case R.id.action_filter:
                activity.showPopup();
                break;
        }
        return true;
    }
}
