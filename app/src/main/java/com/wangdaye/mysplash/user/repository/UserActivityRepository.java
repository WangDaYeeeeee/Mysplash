package com.wangdaye.mysplash.user.repository;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.callback.NoBodyCallback;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.UserService;

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
        userService.requestUserProfile(username, new Callback<User>() {

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
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        NoBodyCallback<ResponseBody> callback = new NoBodyCallback<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody responseBody) {
                if (current.getValue() != null && current.getValue().data != null) {
                    User user = current.getValue().data;
                    user.followed_by_user = setToFollow;
                    user.followers_count += setToFollow ? 1 : -1;
                    current.setValue(Resource.success(user));
                }
            }

            @Override
            public void onFailed() {
                if (current.getValue() != null && current.getValue().data != null) {
                    current.setValue(Resource.error(current.getValue().data));
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
