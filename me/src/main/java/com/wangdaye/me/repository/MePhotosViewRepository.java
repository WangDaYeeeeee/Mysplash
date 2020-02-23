package com.wangdaye.me.repository;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.service.SettingsService;
import com.wangdaye.me.vm.MePhotosViewModel;

import javax.inject.Inject;

public class MePhotosViewRepository {

    private PhotoService service;

    @Inject
    public MePhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getUserPhotos(MePhotosViewModel viewModel, boolean refresh) {
        if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            if (refresh) {
                viewModel.writeListResource(ListResource::refreshing);
            } else {
                viewModel.writeListResource(ListResource::loading);
            }

            service.cancel();
            service.requestUserPhotos(
                    AuthManager.getInstance().getUsername(),
                    viewModel.getListRequestPage(),
                    viewModel.getListPerPage(),
                    new ListResourceObserver<>(viewModel, refresh)
            );
        }
    }

    public void getUserLikes(MePhotosViewModel viewModel, boolean refresh) {
        if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            if (refresh) {
                viewModel.writeListResource(ListResource::refreshing);
            } else {
                viewModel.writeListResource(ListResource::loading);
            }

            service.cancel();
            service.requestUserLikes(
                    AuthManager.getInstance().getUsername(),
                    viewModel.getListRequestPage(),
                    viewModel.getListPerPage(),
                    new ListResourceObserver<>(viewModel, refresh)
            );
        }
    }

    public void cancel() {
        service.cancel();
    }
}
