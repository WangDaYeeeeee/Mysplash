package com.wangdaye.mysplash.photo3;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.vm.BrowsableViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Photo activity model.
 * */
public class PhotoActivityModel extends BrowsableViewModel<Photo> {

    private PhotoActivityRepository repository;
    private String photoId;

    @Inject
    public PhotoActivityModel(PhotoActivityRepository repository) {
        super();
        this.repository = repository;
        this.photoId = null;
    }

    public void init(@NonNull Resource<Photo> resource, String photoId) {
        boolean init = super.init(resource);

        if (this.photoId == null) {
            this.photoId = photoId;
        }

        if (init) {
            checkToRequestPhoto();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    public void checkToRequestPhoto() {
        if (getResource().getValue() != null
                && (getResource().getValue().data == null || !getResource().getValue().data.complete)) {
            repository.getAPhoto(getResource(), photoId);
        }
    }

    public void likeOrDislikePhoto(boolean like) {
        repository.likeOrDislikePhoto(getResource(), photoId, like);
    }

    public void setPhoto(@NonNull Photo photo) {
        setResource(Resource.success(photo));
        photoId = photo.id;
        checkToRequestPhoto();
    }

    public void setCollectedListForPhoto(List<Collection> list) {
        Photo photo = Objects.requireNonNull(getResource().getValue()).data;
        if (photo != null) {
            photo.current_user_collections.clear();
            photo.current_user_collections.addAll(list);
            setResource(Resource.success(photo));
        }
    }

    public String getPhotoId() {
        return photoId;
    }
}
