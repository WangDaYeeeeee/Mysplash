package com.wangdaye.mysplash.me.presenter.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.popup.MeMenuPopupWindow;
import com.wangdaye.mysplash._common.ui.popup.PhotoOrderPopupWindow;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

/**
 * Popup manage implementor.
 * */

public class PopupManageImplementor
        implements PopupManagePresenter,
        MeMenuPopupWindow.OnSelectItemListener {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public PopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showPopup(Context c, View anchor, String value, final int position) {
        if (position < 0) {
            MeMenuPopupWindow window = new MeMenuPopupWindow(c, anchor);
            window.setOnSelectItemListener(this);
        } else if (position % 2 == 0) {
            PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                    c,
                    anchor,
                    value,
                    PhotoOrderPopupWindow.NO_RANDOM_TYPE);
            window.setOnPhotoOrderChangedListener(new PhotoOrderPopupWindow.OnPhotoOrderChangedListener() {
                @Override
                public void onPhotoOrderChange(String orderValue) {
                    view.responsePopup(orderValue, position);
                }
            });
        } else {
            NotificationUtils.showSnackbar(
                    c.getString(R.string.feedback_no_filter),
                    Snackbar.LENGTH_SHORT);
        }
    }

    /** <br> interface. */

    // on select item listener.

    @Override
    public void onSelectItem(int id) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        switch (id) {
            case MeMenuPopupWindow.ITEM_SUBMIT:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/submit"));
                a.startActivity(intent);
                break;

            case MeMenuPopupWindow.ITEM_PORTFOLIO:
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

            case MeMenuPopupWindow.ITEM_SHARE:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    ShareUtils.shareUser(AuthManager.getInstance().getUser());
                }
                break;
        }
    }
}
