package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.bus.CollectionEvent;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.me.repository.MeCollectionsViewRepository;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MeCollectionsViewModel extends PagerViewModel<Collection>
        implements Consumer<CollectionEvent> {

    private MeCollectionsViewRepository repository;
    private CollectionEventResponsePresenter presenter;
    private Disposable disposable;

    @Nullable private String username;

    @Inject
    public MeCollectionsViewModel(MeCollectionsViewRepository repository,
                                  CollectionEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
        this.username = null;
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
        disposable.dispose();
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

    @Nullable
    public String getUsername() {
        return username;
    }

    protected void setUsername(@Nullable String username) {
        this.username = username;
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
