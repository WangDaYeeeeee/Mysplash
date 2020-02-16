package com.wangdaye.user.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.user.vm.UserCollectionsViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class UserCollectionsViewRepository {

    private CollectionService service;

    @Inject
    public UserCollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getUserCollections(@NonNull UserCollectionsViewModel viewModel,
                                   String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserCollections(
                username,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
