package com.wangdaye.me.repository;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.CollectionService;
import com.wangdaye.common.utils.manager.AuthManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class MeCollectionsViewRepository {

    private CollectionService service;

    @Inject
    public MeCollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getUserCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                                   boolean refresh) {
        if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            assert current.getValue() != null;
            if (refresh) {
                current.setValue(ListResource.refreshing(current.getValue()));
            } else {
                current.setValue(ListResource.loading(current.getValue()));
            }

            service.cancel();
            service.requestUserCollections(
                    AuthManager.getInstance().getUsername(),
                    current.getValue().getRequestPage(),
                    current.getValue().perPage,
                    new ListResourceObserver<>(current, refresh)
            );
        }
    }

    public void cancel() {
        service.cancel();
    }
}
