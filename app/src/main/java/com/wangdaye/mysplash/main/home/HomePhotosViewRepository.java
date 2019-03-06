package com.wangdaye.mysplash.main.home;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.api.PhotoApi;
import com.wangdaye.mysplash.common.network.callback.ListResourceCallback;
import com.wangdaye.mysplash.common.network.callback.RandomListResourceCallback;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.service.PhotoService;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class HomePhotosViewRepository {

    private PhotoService service;

    @Inject
    public HomePhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                          List<Integer> pageList, String order,
                          boolean featured, boolean random, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        if (random) {
            if (featured) {
                service.requestCuratePhotos(
                        pageList.get(current.getValue().dataPage + 1),
                        current.getValue().perPage,
                        PhotoApi.ORDER_BY_LATEST,
                        new RandomListResourceCallback<>(current, pageList, refresh));
            } else {
                service.requestPhotos(
                        pageList.get(current.getValue().dataPage + 1),
                        current.getValue().perPage,
                        PhotoApi.ORDER_BY_LATEST,
                        new RandomListResourceCallback<>(current, pageList, refresh));
            }
        } else {
            if (featured) {
                service.requestCuratePhotos(
                        current.getValue().dataPage + 1,
                        current.getValue().perPage,
                        order,
                        new ListResourceCallback<>(current, refresh));
            } else {
                service.requestPhotos(
                        current.getValue().dataPage + 1,
                        current.getValue().perPage,
                        order,
                        new ListResourceCallback<>(current, refresh));
            }
        }
    }

    public void cancel() {
        service.cancel();
    }
}
