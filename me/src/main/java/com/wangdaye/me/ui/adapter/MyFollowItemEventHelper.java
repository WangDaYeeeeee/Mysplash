package com.wangdaye.me.ui.adapter;

import android.view.View;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.presenter.FollowUserPresenter;
import com.wangdaye.component.ComponentFactory;

public class MyFollowItemEventHelper implements MyFollowAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public MyFollowItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onFollowItemClicked(View avatar, View background, User user) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onFollowUserOrCancel(User user, int adapterPosition, boolean follow) {
        if (follow) {
            FollowUserPresenter.getInstance().follow(user);
        } else {
            FollowUserPresenter.getInstance().unfollow(user);
        }
    }
}
