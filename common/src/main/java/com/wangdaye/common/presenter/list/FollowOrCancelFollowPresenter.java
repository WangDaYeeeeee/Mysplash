package com.wangdaye.common.presenter.list;

import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.network.observer.NoBodyObserver;
import com.wangdaye.common.network.service.FollowService;

import javax.inject.Inject;

public class FollowOrCancelFollowPresenter {

    private FollowService service;

    @Inject
    public FollowOrCancelFollowPresenter(FollowService service) {
        this.service = service;
    }

    public void followOrCancelFollowUser(User user, boolean setToFollow) {
        NoBodyObserver callback = new NoBodyObserver(succeed -> {
            if (succeed) {
                user.settingFollow = false;
                user.followed_by_user = setToFollow;
                user.followers_count += setToFollow ? 1 : -1;
                MessageBus.getInstance().post(user);
            } else {
                user.settingFollow = false;
                MessageBus.getInstance().post(user);
            }
        });

        if (setToFollow) {
            service.followUser(user.username, callback);
        } else {
            service.cancelFollowUser(user.username, callback);
        }
    }
}
