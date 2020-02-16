package com.wangdaye.main.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.pager.CollectionsPagerViewModel;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.main.repository.CollectionsHomePageViewRepository;

import androidx.annotation.NonNull;

import javax.inject.Inject;

/**
 * Collections view model.
 * */
public class CollectionsHomePageViewModel extends CollectionsPagerViewModel {

    private CollectionsHomePageViewRepository repository;

    @Inject
    public CollectionsHomePageViewModel(CollectionsHomePageViewRepository repository) {
        super();
        this.repository = repository;
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
    }

    @Override
    public void refresh() {
        getRepository().getFeaturedCollections(this, true);
    }

    @Override
    public void load() {
        getRepository().getFeaturedCollections(this, false);
    }

    CollectionsHomePageViewRepository getRepository() {
        return repository;
    }
}
