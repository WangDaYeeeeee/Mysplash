package com.wangdaye.user.vm;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.disposables.Disposable;

public class UserPhotosViewModel extends AbstractUserViewModel<Photo, PhotoEvent> {

    private UserPhotosViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable downloadEventDisposable;

    private MutableLiveData<String> photosOrder;

    @Inject
    public UserPhotosViewModel(UserPhotosViewRepository repository,
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

    public void init(@NonNull ListResource<Photo> defaultResource,
                     String defaultOrder, String defaultUsername) {
        boolean init = super.init(defaultResource, defaultUsername);

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
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserPhotos(
                getListResource(),
                getUsername(),
                getPhotosOrder().getValue(),
                true
        );
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserPhotos(
                getListResource(),
                getUsername(),
                getPhotosOrder().getValue(),
                false
        );
    }

    UserPhotosViewRepository getRepository() {
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
