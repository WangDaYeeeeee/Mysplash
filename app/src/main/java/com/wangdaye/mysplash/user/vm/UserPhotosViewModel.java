package com.wangdaye.mysplash.user.vm;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserPhotosViewModel extends AbstractUserViewModel<Photo, PhotoEvent> {

    private UserPhotosViewRepository repository;
    private PhotoEventResponsePresenter presenter;

    private MutableLiveData<String> photosOrder;

    @Inject
    public UserPhotosViewModel(UserPhotosViewRepository repository,
                               PhotoEventResponsePresenter presenter) {
        super(PhotoEvent.class);
        this.repository = repository;
        this.presenter = presenter;

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
        presenter.clearResponse();
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
        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
