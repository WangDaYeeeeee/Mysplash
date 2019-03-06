package com.wangdaye.mysplash.collection.vm;

import com.wangdaye.mysplash.collection.repository.CollectionActivityRepository;
import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.basic.vm.BrowsableViewModel;

import androidx.annotation.NonNull;

/**
 * Collection activity model.
 * */
public class CollectionActivityModel extends BrowsableViewModel<Collection> {

    private CollectionActivityRepository repository;

    private Integer collectionId;
    private Boolean curated;

    public CollectionActivityModel(CollectionActivityRepository repository) {
        super();
        this.repository = repository;
        this.collectionId = null;
        this.curated = null;
    }

    public boolean init(@NonNull Resource<Collection> resource, int collectionId, boolean curated) {
        boolean init = super.init(resource);
        if (this.collectionId == null) {
            this.collectionId = collectionId;
        }
        if (this.curated == null) {
            this.curated = curated;
        }

        if (init && resource.data == null) {
            requestACollection();
        }

        return init;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    public void requestACollection() {
        if (curated) {
            repository.getACuratedCollection(getResource(), String.valueOf(collectionId));
        } else {
            repository.getACollection(getResource(), String.valueOf(collectionId));
        }
    }
}
