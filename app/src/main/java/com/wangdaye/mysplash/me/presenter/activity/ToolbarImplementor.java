package com.wangdaye.mysplash.me.presenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui.activity.UpdateMeActivity;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(Activity a) {
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
                    Intent u = new Intent(a, UpdateMeActivity.class);
                    a.startActivity(u);
                    a.overridePendingTransition(R.anim.activity_in, 0);
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
                        Toast.makeText(
                                a,
                                a.getString(R.string.feedback_portfolio_is_null),
                                Toast.LENGTH_SHORT).show();
                    }
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
