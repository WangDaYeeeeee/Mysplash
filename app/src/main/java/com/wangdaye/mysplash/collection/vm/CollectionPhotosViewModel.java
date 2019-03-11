package com.wangdaye.mysplash.collection.vm;

import com.wangdaye.mysplash.collection.repository.CollectionPhotosViewRepository;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Collection photos view model.
 * */
public class CollectionPhotosViewModel extends PagerViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private CollectionPhotosViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    private Integer collectionId;
    private Boolean curated;

    public static final int INVALID_COLLECTION_ID = -1;

    public CollectionPhotosViewModel(CollectionPhotosViewRepository repository,
                                     PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
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
        presenter.clearResponse();
        disposable.dispose();
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

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        assert getListResource().getValue() != null;

        if (photoEvent.event == PhotoEvent.Event.ADD_TO_COLLECTION
                && photoEvent.collection != null
                && photoEvent.collection.id == collectionId) {
            // this photo was been added to this collection.
            getListResource().setValue(
                    ListResource.insertItem(getListResource().getValue(), photoEvent.photo, 0));
            return;
        }

        if (photoEvent.event == PhotoEvent.Event.REMOVE_FROM_COLLECTION
                && photoEvent.collection != null
                && photoEvent.collection.id == collectionId) {
            // remove this photo from this collection.
            presenter.removePhoto(getListResource(), photoEvent.photo, false);
            return;
        }

        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
