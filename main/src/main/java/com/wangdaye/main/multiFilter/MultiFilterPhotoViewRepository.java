package com.wangdaye.main.multiFilter;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class MultiFilterPhotoViewRepository {

    private PhotoService service;

    @Inject
    public MultiFilterPhotoViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getSearchResult(@NonNull MutableLiveData<ListResource<Photo>> current, boolean refresh,
                                Boolean featured, String username, String query, String orientation) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestRandomPhotos(
                null, featured, username, query, orientation,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
