package com.wangdaye.mysplash.me.repository;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.observer.ListResourceObserver;
import com.wangdaye.mysplash.common.network.service.CollectionService;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

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
                    new ListResourceObserver<>(current, refresh));
        }
    }

    public void cancel() {
        service.cancel();
    }
}
