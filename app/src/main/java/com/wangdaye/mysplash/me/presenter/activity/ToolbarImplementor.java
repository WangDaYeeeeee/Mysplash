package com.wangdaye.mysplash.me.presenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(Activity a) {
        SwipeBackCoordinatorLayout.hideBackgroundShadow(((MeActivity) a).getSnackbarContainer());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.finishAfterTransition();
        } else {
            a.finish();
            a.overridePendingTransition(0, R.anim.activity_slide_out_bottom);
        }
    }

    @Override
    public void touchToolbar(Activity a) {
        // do nothing.
    }

    @Override
    public boolean touchMenuItem(Activity a, int itemId) {
        switch (itemId) {
            case R.id.action_edit:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    IntentHelper.startUpdateMeActivity(a);
                }
                break;

            case R.id.action_open_portfolio:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    String url = AuthManager.getInstance().getMe().portfolio_url;
                    if (!TextUtils.isEmpty(url)) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        a.startActivity(i);
                    } else {
                        NotificationUtils.showSnackbar(
                                a.getString(R.string.feedback_portfolio_is_null),
                                Snackbar.LENGTH_SHORT);
                    }
                }
                break;

            case R.id.action_share:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    ShareUtils.shareUser(AuthManager.getInstance().getUser());
                }
                break;

            case R.id.action_filter:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    ((MeActivity) a).showPopup();
                }
                break;
        }
        return true;
    }
}
