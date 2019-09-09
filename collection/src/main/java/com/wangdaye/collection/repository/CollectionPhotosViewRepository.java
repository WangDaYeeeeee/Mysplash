package com.wangdaye.collection.repository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;

public class CollectionPhotosViewRepository {

    private PhotoService service;

    @Inject
    public CollectionPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getCollectionPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                                    int collectionId, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestCollectionPhotos(
                collectionId,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void getCuratedCollectionPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                                           int collectionId, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestCuratedCollectionPhotos(
                collectionId,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
