package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.utils.bus.CollectionEvent;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.search.repository.CollectionSearchPageViewRepository;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class CollectionSearchPageViewModel extends AbstractSearchPageViewModel<Collection>
        implements Consumer<CollectionEvent> {
    
    private CollectionSearchPageViewRepository repository;
    private CollectionEventResponsePresenter presenter;
    private Disposable disposable;

    @Inject
    public CollectionSearchPageViewModel(CollectionSearchPageViewRepository repository,
                                         CollectionEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
        presenter.clearResponse();
        disposable.dispose();
    }

    @Override
    public void refresh() {
        repository.getCollections(getListResource(), getQuery(), true);
    }

    @Override
    public void load() {
        repository.getCollections(getListResource(), getQuery(), false);
    }

    // interface.

    @Override
    public void accept(CollectionEvent collectionEvent) {
        switch (collectionEvent.event) {
            case UPDATE:
                presenter.updateCollection(getListResource(), collectionEvent.collection);
                break;

            case DELETE:
                presenter.deleteCollection(getListResource(), collectionEvent.collection);
                break;
        }
    }
}
