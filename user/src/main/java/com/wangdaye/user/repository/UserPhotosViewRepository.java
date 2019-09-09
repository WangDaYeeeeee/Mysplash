package com.wangdaye.user.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UserPhotosViewRepository {

    private PhotoService service;

    @Inject
    public UserPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getUserPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                              String username, String order, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestUserPhotos(
                username,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                order,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void getUserLikes(@NonNull MutableLiveData<ListResource<Photo>> current,
                             String username, String order, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestUserLikes(
                username,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                order,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
