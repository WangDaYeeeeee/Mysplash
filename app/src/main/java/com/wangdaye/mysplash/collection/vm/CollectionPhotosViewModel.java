package com.wangdaye.mysplash.collection.vm;

import com.wangdaye.mysplash.collection.repository.CollectionPhotosViewRepository;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.bus.event.DownloadEvent;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.bus.event.PhotoEvent;
import com.wangdaye.mysplash.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.mysplash.common.presenter.event.PhotoEventResponsePresenter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Collection photos view model.
 * */
public class CollectionPhotosViewModel extends PagerViewModel<Photo> {

    private CollectionPhotosViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    private Integer collectionId;
    private Boolean curated;

    public static final int INVALID_COLLECTION_ID = -1;

    private Consumer<PhotoEvent> photoEventConsumer = photoEvent -> {
        assert getListResource().getValue() != null;

        if (photoEvent.event == PhotoEvent.Event.ADD_TO_COLLECTION
                && photoEvent.collection != null
                && photoEvent.collection.id == collectionId) {
            // this photo was been added to this collection.
            getListResource().setValue(
                    ListResource.insertItem(
                            getListResource().getValue(),
                            photoEvent.photo,
                            0
                    )
            );
            return;
        }

        if (photoEvent.event == PhotoEvent.Event.REMOVE_FROM_COLLECTION
                && photoEvent.collection != null
                && photoEvent.collection.id == collectionId) {
            // remove this photo from this collection.
            photoEventResponsePresenter.removePhoto(
                    getListResource(),
                    photoEvent.photo,
                    false
            );
            return;
        }

        photoEventResponsePresenter.updatePhoto(
                getListResource(),
                photoEvent.photo,
                false
        );
    };

    private Consumer<DownloadEvent> downloadEventConsumer = downloadEvent ->
            downloadEventResponsePresenter.updatePhoto(getListResource(), downloadEvent, false);

    @Inject
    public CollectionPhotosViewModel(CollectionPhotosViewRepository repository,
                                     PhotoEventResponsePresenter photoEventResponsePresenter,
                                     DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super();

        this.repository = repository;
        this.photoEventResponsePresenter = photoEventResponsePresenter;
        this.downloadEventResponsePresenter = downloadEventResponsePresenter;

        this.photoEventDisposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(photoEventConsumer);
        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(downloadEventConsumer);

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
        photoEventResponsePresenter.clearResponse();
        downloadEventResponsePresenter.clearResponse();

        photoEventDisposable.dispose();
        downloadEventDisposable.dispose();
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
