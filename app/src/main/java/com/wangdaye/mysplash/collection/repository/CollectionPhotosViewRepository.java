package com.wangdaye.mysplash.collection.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.callback.ListResourceCallback;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.service.PhotoService;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CollectionPhotosViewRepository {

    private PhotoService service;

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
                current.getValue().dataPage + 1,
                current.getValue().perPage,
                new ListResourceCallback<>(current, refresh));
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
                current.getValue().dataPage + 1,
                current.getValue().perPage,
                new ListResourceCallback<>(current, refresh));
    }

    public void cancel() {
        service.cancel();
    }
}
