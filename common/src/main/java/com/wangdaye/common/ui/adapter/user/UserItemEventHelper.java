package com.wangdaye.common.ui.adapter.user;

import android.text.TextUtils;
import android.view.View;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.component.ComponentFactory;

public class UserItemEventHelper implements UserAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public UserItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user, int index) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onPortfolioButtonClicked(User user) {
        if (!TextUtils.isEmpty(user.portfolio_url)) {
            RoutingHelper.startWebActivity(activity, user.portfolio_url);
        }
    }
}
