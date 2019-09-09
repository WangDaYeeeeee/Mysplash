package com.wangdaye.user.repository;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.observer.NoBodyObserver;
import com.wangdaye.common.network.service.FollowService;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.bus.MessageBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UserActivityRepository {

    private UserService userService;
    private FollowService followService;

    @Inject
    public UserActivityRepository(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    public void getUser(@NonNull MutableLiveData<Resource<User>> current, String username) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        userService.cancel();
        userService.requestUserProfile(username, new BaseObserver<User>() {

            @Override
            public void onSucceed(User user) {
                user.complete = true;
                current.setValue(Resource.success(user));
            }

            @Override
            public void onFailed() {
                current.setValue(Resource.error(current.getValue().data));
            }
        });
    }

    public void followOrCancelFollowUser(@NonNull MutableLiveData<Resource<User>> current,
                                         String username, boolean setToFollow) {
        if (current.getValue() == null || current.getValue().data == null) {
            return;
        }

        User user = current.getValue().data;
        user.settingFollow = true;
        current.setValue(Resource.loading(user));

        NoBodyObserver callback = new NoBodyObserver(succeed -> {
            if (current.getValue() != null && current.getValue().data != null) {
                user.settingFollow = false;
                if (succeed) {
                    user.followed_by_user = setToFollow;
                    user.followers_count += setToFollow ? 1 : -1;
                    MessageBus.getInstance().post(user);
                } else {
                    MessageBus.getInstance().post(user);
                }
            }
        });

        followService.cancel();
        if (setToFollow) {
            followService.followUser(username, callback);
        } else {
            followService.cancelFollowUser(username, callback);
        }
    }

    public void cancel() {
        userService.cancel();
        followService.cancel();
    }
}
