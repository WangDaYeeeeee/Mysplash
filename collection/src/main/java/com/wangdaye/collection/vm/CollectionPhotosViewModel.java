package com.wangdaye.collection.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.vm.pager.PhotosPagerViewModel;
import com.wangdaye.collection.repository.CollectionPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Collection photos view model.
 * */
public class CollectionPhotosViewModel extends PhotosPagerViewModel {

    private CollectionPhotosViewRepository repository;

    private Integer collectionId;
    private Boolean curated;

    private boolean usersCollection;

    public static final int INVALID_COLLECTION_ID = -1;

    @Inject
    public CollectionPhotosViewModel(CollectionPhotosViewRepository repository) {
        super();
        this.repository = repository;
        this.collectionId = null;
        this.curated = null;
        this.usersCollection = false;
    }

    public void init(@NonNull ListResource<Photo> resource, int collectionId, boolean curated,
                     boolean usersCollection) {
        boolean init = super.init(resource);

        if (this.collectionId == null) {
            this.collectionId = collectionId;
        }
        if (this.curated == null) {
            this.curated = curated;
        }

        this.usersCollection = usersCollection;

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

    @Override
    protected void onAddPhotoToCollection(Photo photo, @Nullable Collection collection) {
        if (usersCollection && collection != null && collection.id == collectionId) {
            writeListResource(resource -> ListResource.insertItem(resource, photo, 0));
        } else {
            super.onAddPhotoToCollection(photo, collection);
        }
    }

    @Override
    protected void onRemovePhotoFromCollection(Photo photo, @Nullable Collection collection) {
        if (usersCollection && collection != null && collection.id == collectionId) {
            asynchronousWriteDataList((writer, resource) -> {
                for (int i = 0; i < resource.dataList.size(); i ++) {
                    if (resource.dataList.get(i).id.equals(photo.id)) {
                        writer.postListResource(ListResource.removeItem(resource, i));
                        break;
                    }
                }
            });
        } else {
            super.onRemovePhotoFromCollection(photo, collection);
        }
    }

    private void getCollectionPhotos(boolean refresh) {
        if (collectionId != INVALID_COLLECTION_ID) {
            if (curated) {
                repository.getCuratedCollectionPhotos(this, collectionId, refresh);
            } else {
                repository.getCollectionPhotos(this, collectionId, refresh);
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
