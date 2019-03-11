package com.wangdaye.mysplash.user.vm;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.utils.bus.CollectionEvent;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.user.repository.UserCollectionsViewRepository;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class UserCollectionsViewModel extends PagerViewModel<Collection>
        implements Consumer<CollectionEvent> {

    private UserCollectionsViewRepository repository;
    private CollectionEventResponsePresenter presenter;
    private Disposable disposable;

    private String username;

    @Inject
    public UserCollectionsViewModel(UserCollectionsViewRepository repository,
                                    CollectionEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
    }

    public void init(@NonNull ListResource<Collection> defaultResource, String defaultUsername) {
        boolean init = super.init(defaultResource);

        if (username == null) {
            username = defaultUsername;
        }

        if (init) {
            refresh();
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // interface.

    @Override
    public void accept(CollectionEvent collectionEvent) {
        if (!collectionEvent.collection.user.username.equals(username)
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
