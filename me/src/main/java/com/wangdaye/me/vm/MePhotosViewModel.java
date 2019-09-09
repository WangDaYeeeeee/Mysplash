package com.wangdaye.me.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.me.repository.MePhotosViewRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.disposables.Disposable;

public class MePhotosViewModel extends AbstractMePagerViewModel<Photo, PhotoEvent> {

    private MePhotosViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable downloadEventDisposable;

    private MutableLiveData<String> photosOrder;

    @Inject
    public MePhotosViewModel(MePhotosViewRepository repository,
                             PhotoEventResponsePresenter photoEventResponsePresenter,
                             DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super(PhotoEvent.class);

        this.repository = repository;
        this.photoEventResponsePresenter = photoEventResponsePresenter;
        this.downloadEventResponsePresenter = downloadEventResponsePresenter;

        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(event -> this.downloadEventResponsePresenter.updatePhoto(
                        getListResource(),
                        event,
                        false
                ));

        this.photosOrder = null;
    }

    public void init(@NotNull ListResource<Photo> defaultResource, String defaultOrder) {
        boolean init = super.init(defaultResource);

        if (photosOrder == null) {
            photosOrder = new MutableLiveData<>();
            photosOrder.setValue(defaultOrder);
        }

        if (init) {
            refresh();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        getRepository().cancel();
        photoEventResponsePresenter.clearResponse();
        downloadEventResponsePresenter.clearResponse();

        downloadEventDisposable.dispose();
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(getListResource(), getPhotosOrder().getValue(), true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(getListResource(), getPhotosOrder().getValue(), false);
    }

    MePhotosViewRepository getRepository() {
        return repository;
    }

    public LiveData<String> getPhotosOrder() {
        return photosOrder;
    }

    public void setPhotosOrder(String order) {
        photosOrder.setValue(order);
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        photoEventResponsePresenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
