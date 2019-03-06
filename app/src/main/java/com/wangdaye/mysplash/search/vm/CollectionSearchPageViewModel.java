package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.search.repository.CollectionSearchPageViewRepository;

import javax.inject.Inject;

public class CollectionSearchPageViewModel extends AbstractSearchPageViewModel<Collection> {
    
    private CollectionSearchPageViewRepository repository;

    @Inject
    public CollectionSearchPageViewModel(CollectionSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getCollections(getListResource(), getQuery(), true);
    }

    @Override
    public void load() {
        repository.getCollections(getListResource(), getQuery(), false);
    }
}
