package com.wangdaye.mysplash.collection.vm;

import com.wangdaye.mysplash.collection.repository.CollectionPhotosViewRepository;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;

import androidx.annotation.NonNull;

/**
 * Collection photos view model.
 * */
public class CollectionPhotosViewModel extends PagerViewModel<Photo> {

    private CollectionPhotosViewRepository repository;

    private Integer collectionId;
    private Boolean curated;

    public static final int INVALID_COLLECTION_ID = -1;

    public CollectionPhotosViewModel(CollectionPhotosViewRepository repository) {
        super();
        this.repository = repository;
        this.collectionId = null;
        this.curated = null;
    }

    public void init(@NonNull ListResource<Photo> resource, int collectionId, boolean curated) {
        boolean init = super.init(resource);

        if (this.collectionId == null) {
            this.collectionId = collectionId;
        }
        if (this.curated == null) {
            this.curated = curated;
        }

        if (init) {
            refresh();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        getCollectionPhotos(true);
    }

    @Override
    public void load() {
        getCollectionPhotos(false);
    }

    private void getCollectionPhotos(boolean refresh) {
        if (collectionId != INVALID_COLLECTION_ID) {
            if (curated) {
                repository.getCuratedCollectionPhotos(getListResource(), collectionId, refresh);
            } else {
                repository.getCollectionPhotos(getListResource(), collectionId, refresh);
            }
        }
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public void setCurated(boolean curated) {
        this.curated = curated;
    }
}
