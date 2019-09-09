package com.wangdaye.user.vm;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.user.repository.UserCollectionsViewRepository;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class UserCollectionsViewModel extends AbstractUserViewModel<Collection, CollectionEvent> {

    private UserCollectionsViewRepository repository;
    private CollectionEventResponsePresenter presenter;

    @Inject
    public UserCollectionsViewModel(UserCollectionsViewRepository repository,
                                    CollectionEventResponsePresenter presenter) {
        super(CollectionEvent.class);
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> defaultResource, String defaultUsername) {
        if (super.init(defaultResource, defaultUsername)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getRepository().cancel();
        presenter.clearResponse();
    }

    @Override
    public void refresh() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserCollections(getListResource(), getUsername(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserCollections(getListResource(), getUsername(), false);
    }

    UserCollectionsViewRepository getRepository() {
        return repository;
    }

    // interface.

    @Override
    public void accept(CollectionEvent collectionEvent) {
        if (!collectionEvent.collection.user.username.equals(getUsername())
                || Objects.requireNonNull(getListResource().getValue()).state != ListResource.State.SUCCESS) {
            return;
        }
        switch (collectionEvent.event) {
            case CREATE:
                presenter.createCollection(getListResource(), collectionEvent.collection);
                break;

            case UPDATE:
                presenter.updateCollection(getListResource(), collectionEvent.collection);
                break;

            case DELETE:
                presenter.deleteCollection(getListResource(), collectionEvent.collection);
                break;
        }
    }
}
