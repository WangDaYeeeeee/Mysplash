package com.wangdaye.user.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.component.service.SettingsService;
import com.wangdaye.user.vm.UserPhotosViewModel;

import javax.inject.Inject;

public class UserPhotosViewRepository {

    private PhotoService service;

    @Inject
    public UserPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getUserPhotos(UserPhotosViewModel viewModel, String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserPhotos(
                username,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                SettingsService.PHOTOS_ORDER_LATEST,
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void getUserLikes(UserPhotosViewModel viewModel, String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserLikes(
                username,
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
