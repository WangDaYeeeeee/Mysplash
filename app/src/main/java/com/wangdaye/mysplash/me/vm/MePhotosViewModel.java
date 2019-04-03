package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.me.repository.MePhotosViewRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MePhotosViewModel extends AbstractMePagerViewModel<Photo, PhotoEvent> {

    private MePhotosViewRepository repository;
    private PhotoEventResponsePresenter presenter;

    private MutableLiveData<String> photosOrder;

    @Inject
    public MePhotosViewModel(MePhotosViewRepository repository,
                             PhotoEventResponsePresenter presenter) {
        super(PhotoEvent.class);
        this.repository = repository;
        this.presenter = presenter;
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
        presenter.clearResponse();
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
        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
