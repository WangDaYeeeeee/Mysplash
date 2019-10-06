package com.wangdaye.me.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.utils.manager.AuthManager;

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

    public void getFollowers(@NonNull MutableLiveData<ListResource<User>> current,
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
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new MyFollowUserObserver(current, refresh)
        );
    }

    public void getFollowing(@NonNull MutableLiveData<ListResource<User>> current,
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
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new MyFollowUserObserver(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }

    private class MyFollowUserObserver extends BaseObserver<List<User>> {

        private MutableLiveData<ListResource<User>> current;
        private boolean refresh;

        MyFollowUserObserver(MutableLiveData<ListResource<User>> current,
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
                current.setValue(ListResource.refreshSuccess(current.getValue(), list));
            } else if (list.size() == current.getValue().perPage) {
                current.setValue(ListResource.loadSuccess(current.getValue(), list));
            } else {
                current.setValue(ListResource.allLoaded(current.getValue(), list));
            }
        }

        @Override
        public void onFailed() {
            if (current.getValue() == null) {
                return;
            }
            current.setValue(ListResource.error(current.getValue()));
        }
    }
}
