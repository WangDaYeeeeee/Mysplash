package com.wangdaye.me.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.me.repository.MeCollectionsViewRepository;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Me collections view model.
 * */
public class MeCollectionsViewModel extends AbstractMePagerViewModel<Collection, CollectionEvent> {

    private MeCollectionsViewRepository repository;
    private CollectionEventResponsePresenter presenter;

    @Inject
    public MeCollectionsViewModel(MeCollectionsViewRepository repository,
                                  CollectionEventResponsePresenter presenter) {
        super(CollectionEvent.class);
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> resource) {
        if (super.init(resource)) {
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
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(getListResource(), true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(getListResource(), false);
    }

    MeCollectionsViewRepository getRepository() {
        return repository;
    }

    // interface.

    @Override
    public void accept(CollectionEvent collectionEvent) {
        User user = AuthManager.getInstance().getUser();
        if (user == null
                || !user.username.equals(collectionEvent.collection.user.username)
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
