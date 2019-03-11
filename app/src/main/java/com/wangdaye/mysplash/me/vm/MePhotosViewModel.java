package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.me.repository.MePhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MePhotosViewModel extends PagerViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private MePhotosViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    private MutableLiveData<String> photosOrder;

    @Nullable private String username;

    @Inject
    public MePhotosViewModel(MePhotosViewRepository repository,
                             PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
        this.photosOrder = null;
        this.username = null;
    }

    public void init(ListResource<Photo> defaultResource, String defaultOrder) {
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
        disposable.dispose();
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

    @Nullable
    public String getUsername() {
        return username;
    }

    protected void setUsername(@Nullable String username) {
        this.username = username;
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
