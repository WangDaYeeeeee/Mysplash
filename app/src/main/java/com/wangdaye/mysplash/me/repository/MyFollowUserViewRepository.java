package com.wangdaye.mysplash.me.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.service.UserService;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.me.ui.MyFollowAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class MyFollowUserViewRepository {

    private UserService service;

    @Inject
    public MyFollowUserViewRepository(UserService service) {
        this.service = service;
    }

    public void getFollowers(@NonNull MutableLiveData<ListResource<MyFollowAdapter.MyFollowUser>> current,
                             boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestFollowers(
                AuthManager.getInstance().getUsername(),
                current.getValue().dataPage + 1,
                current.getValue().perPage,
                new MyFollowUserCallback(current, refresh));
    }

    public void getFollowing(@NonNull MutableLiveData<ListResource<MyFollowAdapter.MyFollowUser>> current,
                             boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestFollowing(
                AuthManager.getInstance().getUsername(),
                current.getValue().dataPage + 1,
                current.getValue().perPage,
                new MyFollowUserCallback(current, refresh));
    }

    public void cancel() {
        service.cancel();
    }

    private class MyFollowUserCallback extends Callback<List<User>> {

        private MutableLiveData<ListResource<MyFollowAdapter.MyFollowUser>> current;
        private boolean refresh;

        MyFollowUserCallback(MutableLiveData<ListResource<MyFollowAdapter.MyFollowUser>> current,
                             boolean refresh) {
            this.current = current;
            this.refresh = refresh;
        }

        @Override
        public void onSucceed(List<User> list) {
            if (current.getValue() == null) {
                return;
            }
            if (refresh) {
                current.setValue(
                        ListResource.refreshSuccess(
                                current.getValue(),
                                MyFollowAdapter.MyFollowUser.getMyFollowUserList(list)));
            } else if (list.size() == current.getValue().perPage) {
                current.setValue(
                        ListResource.loadSuccess(
                                current.getValue(),
                                MyFollowAdapter.MyFollowUser.getMyFollowUserList(list)));
            } else {
                current.setValue(
                        ListResource.allLoaded(
                                current.getValue(),
                                MyFollowAdapter.MyFollowUser.getMyFollowUserList(list)));
            }
        }

        @Override
        public void onFailed() {
            if (current.getValue() == null) {
                return;
            }
            if (refresh) {
                current.setValue(ListResource.refreshError(current.getValue()));
            } else {
                current.setValue(ListResource.loadError(current.getValue()));
            }
        }
    }
}
