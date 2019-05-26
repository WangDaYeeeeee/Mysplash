package com.wangdaye.mysplash.main.collection.vm;

import com.wangdaye.mysplash.common.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;

import javax.inject.Inject;

public class CuratedCollectionsViewModel extends AbstractCollectionsViewModel {

    @Inject
    public CuratedCollectionsViewModel(CollectionsViewRepository repository,
                                       CollectionEventResponsePresenter presenter) {
        super(repository, presenter);
    }

    @Override
    public void refresh() {
        getRepository().getCuratedCollections(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getCuratedCollections(getListResource(), false);
    }
}
