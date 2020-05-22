package com.wangdaye.user.repository;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.presenter.FollowUserPresenter;
import com.wangdaye.user.vm.UserActivityModel;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;

public class UserActivityRepository {

    private UserService userService;

    @Inject
    public UserActivityRepository(UserService userService) {
        this.userService = userService;
    }

    public void getUser(UserActivityModel viewModel, String username) {
        MutableLiveData<Resource<User>> current = viewModel.getResource();
        assert current.getValue() != null;
        viewModel.setResource(Resource.loading(current.getValue().data));

        userService.cancel();
        userService.requestUserProfile(username, new BaseObserver<User>() {

            @Override
            public void onSucceed(User user) {
                MessageBus.getInstance().post(user);
            }

            @Override
            public void onFailed() {
                current.setValue(Resource.error(current.getValue().data));
            }
        });
    }

    public void followOrCancelFollowUser(UserActivityModel viewModel, String username, boolean setToFollow) {
        MutableLiveData<Resource<User>> current = viewModel.getResource();
        assert current.getValue() != null;
        User user = current.getValue().data;

        if (user != null && !FollowUserPresenter.getInstance().isInProgress(user)) {
            if (setToFollow) {
                FollowUserPresenter.getInstance().follow(user);
            } else {
                FollowUserPresenter.getInstance().unfollow(user);
            }

            // handel update result by message bus.
            // viewModel.setResource(Resource.loading(user), false);
        }
    }

    public void cancel() {
        userService.cancel();
    }
}
