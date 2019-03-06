package com.wangdaye.mysplash.main.collection.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;

import androidx.annotation.NonNull;

/**
 * Abstract collections view model.
 * */
public abstract class AbstractCollectionsViewModel extends PagerViewModel<Collection> {

    private CollectionsViewRepository repository;

    public AbstractCollectionsViewModel(CollectionsViewRepository repository) {
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

    CollectionsViewRepository getRepository() {
        return repository;
    }
}
