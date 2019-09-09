package com.wangdaye.main.collection.vm;

import com.wangdaye.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.main.collection.CollectionsViewRepository;

import javax.inject.Inject;

public class FeaturedCollectionsViewModel extends AbstractCollectionsViewModel {

    @Inject
    public FeaturedCollectionsViewModel(CollectionsViewRepository repository,
                                        CollectionEventResponsePresenter presenter) {
        super(repository, presenter);
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
