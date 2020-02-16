package com.wangdaye.me.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.vm.MyFollowerViewModel;
import com.wangdaye.me.vm.MyFollowingViewModel;

import javax.inject.Inject;

public class MyFollowUserViewRepository {

    private UserService service;

    @Inject
    public MyFollowUserViewRepository(UserService service) {
        this.service = service;
    }

    public void getFollowers(MyFollowerViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFollowers(
                AuthManager.getInstance().getUsername(),
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void getFollowing(MyFollowingViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFollowing(
                AuthManager.getInstance().getUsername(),
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
