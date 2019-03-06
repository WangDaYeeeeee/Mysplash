package com.wangdaye.mysplash.main.collection.vm;

import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;

import javax.inject.Inject;

public class FeaturedCollectionsViewModel extends AbstractCollectionsViewModel {

    @Inject
    public FeaturedCollectionsViewModel(CollectionsViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        getRepository().getFeaturedCollections(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getFeaturedCollections(getListResource(), false);
    }
}
