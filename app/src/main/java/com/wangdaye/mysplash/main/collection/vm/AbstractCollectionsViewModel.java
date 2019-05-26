package com.wangdaye.mysplash.main.collection.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.bus.event.CollectionEvent;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Abstract collections view model.
 * */
public abstract class AbstractCollectionsViewModel extends PagerViewModel<Collection>
        implements Consumer<CollectionEvent> {

    private CollectionsViewRepository repository;
    private CollectionEventResponsePresenter presenter;
    private Disposable disposable;

    public AbstractCollectionsViewModel(CollectionsViewRepository repository,
                                        CollectionEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
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

    CollectionsViewRepository getRepository() {
        return repository;
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
