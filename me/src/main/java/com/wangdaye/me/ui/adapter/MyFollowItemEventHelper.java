package com.wangdaye.me.ui.adapter;

import android.view.View;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.presenter.list.FollowOrCancelFollowPresenter;
import com.wangdaye.component.ComponentFactory;

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
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onFollowUserOrCancel(User user, int adapterPosition, boolean follow) {
        user.settingFollow = true;
        MessageBus.getInstance().post(user);
        followOrCancelFollowPresenter.followOrCancelFollowUser(user, follow);
    }
}
