package com.wangdaye.collection.repository;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;

public class CollectionPhotosViewRepository {

    private PhotoService service;

    @Inject
    public CollectionPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getCollectionPhotos(@NonNull CollectionPhotosViewModel viewModel,
                                    int collectionId, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestCollectionPhotos(
                collectionId,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void getCuratedCollectionPhotos(@NonNull CollectionPhotosViewModel viewModel,
                                           int collectionId, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestCuratedCollectionPhotos(
                collectionId,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
