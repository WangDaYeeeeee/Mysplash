package com.wangdaye.mysplash.photo3;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.vm.BrowsableViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Photo activity model.
 * */
public class PhotoActivityModel extends BrowsableViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private PhotoActivityRepository repository;
    private Disposable disposable;

    private String photoId;

    @Inject
    public PhotoActivityModel(PhotoActivityRepository repository) {
        super();
        this.repository = repository;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
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
        disposable.dispose();
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

    public String getPhotoId() {
        return photoId;
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        if (photoEvent.photo.id.equals(photoId)) {
            setPhoto(photoEvent.photo);
        }
    }
}
