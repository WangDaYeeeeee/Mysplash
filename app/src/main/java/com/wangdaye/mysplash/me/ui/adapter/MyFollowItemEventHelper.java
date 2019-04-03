package com.wangdaye.mysplash.me.ui.adapter;

import android.view.View;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.presenter.list.FollowOrCancelFollowPresenter;
import com.wangdaye.mysplash.user.ui.UserActivity;

public class MyFollowItemEventHelper implements MyFollowAdapter.ItemEventCallback {

    private MysplashActivity activity;
    private FollowOrCancelFollowPresenter followOrCancelFollowPresenter;

    public MyFollowItemEventHelper(MysplashActivity activity,
                                   FollowOrCancelFollowPresenter followOrCancelFollowPresenter) {
        this.activity = activity;
        this.followOrCancelFollowPresenter = followOrCancelFollowPresenter;
    }

    @Override
    public void onFollowItemClicked(View avatar, View background, User user) {
        IntentHelper.startUserActivity(activity, avatar, background, user, UserActivity.PAGE_PHOTO);
    }

    @Override
    public void onFollowUserOrCancel(User user, int adapterPosition, boolean follow) {
        user.settingFollow = true;
        MessageBus.getInstance().post(user);
        followOrCancelFollowPresenter.followOrCancelFollowUser(user, follow);
    }
}
