package com.wangdaye.main.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.main.vm.CollectionsHomePageViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class CollectionsHomePageViewRepository {

    private CollectionService service;

    @Inject
    public CollectionsHomePageViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getFeaturedCollections(@NonNull CollectionsHomePageViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFeaturedCollections(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
