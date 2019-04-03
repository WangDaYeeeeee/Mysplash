package com.wangdaye.mysplash.common.ui.adapter.user;

import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.ui.UserActivity;

public class UserItemEventHelper implements UserAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public UserItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user, int index) {
        IntentHelper.startUserActivity(activity, avatar, background, user, UserActivity.PAGE_PHOTO);
    }

    @Override
    public void onPortfolioButtonClicked(User user) {
        if (!TextUtils.isEmpty(user.portfolio_url)) {
            IntentHelper.startWebActivity(activity, user.portfolio_url);
        }
    }
}
