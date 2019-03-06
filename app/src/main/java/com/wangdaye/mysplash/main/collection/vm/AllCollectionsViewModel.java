package com.wangdaye.mysplash.main.collection.vm;

import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;

import javax.inject.Inject;

public class AllCollectionsViewModel extends AbstractCollectionsViewModel {

    @Inject
    public AllCollectionsViewModel(CollectionsViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        getRepository().getAllCollections(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getAllCollections(getListResource(), false);
    }
}
