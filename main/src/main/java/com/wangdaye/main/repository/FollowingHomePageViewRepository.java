package com.wangdaye.main.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.FeedService;
import com.wangdaye.main.vm.FollowingHomePageViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class FollowingHomePageViewRepository {

    private FeedService service;

    @Inject
    public FollowingHomePageViewRepository(FeedService service) {
        this.service = service;
    }

    public void getFollowingFeeds(@NonNull FollowingHomePageViewModel viewModel,
                                  boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFollowingFeed(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh));
    }

    public void cancel() {
        service.cancel();
    }
}
