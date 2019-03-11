package com.wangdaye.mysplash.user.repository;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.observer.NoBodyObserver;
import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.UserService;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import okhttp3.ResponseBody;

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

        NoBodyObserver<ResponseBody> callback = new NoBodyObserver<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody responseBody) {
                if (current.getValue() != null && current.getValue().data != null) {
                    User user = current.getValue().data;
                    user.settingFollow = false;
                    user.followed_by_user = setToFollow;
                    user.followers_count += setToFollow ? 1 : -1;
                    MessageBus.getInstance().post(user);
                }
            }

            @Override
            public void onFailed() {
                if (current.getValue() != null && current.getValue().data != null) {
                    User user = current.getValue().data;
                    user.settingFollow = false;
                    MessageBus.getInstance().post(user);
                }
            }
        };

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
