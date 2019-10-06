package com.wangdaye.main.collection.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.PagerViewModel;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.main.collection.CollectionsViewRepository;

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
        if (collectionEvent.event == CollectionEvent.Event.UPDATE) {
            presenter.updateCollection(getListResource(), collectionEvent.collection);
        } else if (collectionEvent.event == CollectionEvent.Event.DELETE) {
            presenter.deleteCollection(getListResource(), collectionEvent.collection);
        }
    }
}
