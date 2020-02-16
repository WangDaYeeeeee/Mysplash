package com.wangdaye.main.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.component.service.SettingsService;
import com.wangdaye.main.vm.PhotosHomePageViewModel;

import javax.inject.Inject;

public class PhotosHomePageViewRepository {

    private PhotoService service;

    @Inject
    public PhotosHomePageViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getPhotos(PhotosHomePageViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestPhotos(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                SettingsService.PHOTOS_ORDER_LATEST,
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
