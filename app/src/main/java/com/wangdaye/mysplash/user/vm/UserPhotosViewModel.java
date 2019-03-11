package com.wangdaye.mysplash.user.vm;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class UserPhotosViewModel extends PagerViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private UserPhotosViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    private MutableLiveData<String> photosOrder;

    private String username;

    @Inject
    public UserPhotosViewModel(UserPhotosViewRepository repository,
                               PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);

        this.photosOrder = null;
    }

    public void init(@NonNull ListResource<Photo> defaultResource,
                     String defaultOrder, String defaultUsername) {
        boolean init = super.init(defaultResource);

        if (photosOrder == null) {
            photosOrder = new MutableLiveData<>();
            photosOrder.setValue(defaultOrder);
        }

        if (username == null) {
            username = defaultUsername;
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
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserPhotos(
                getListResource(), getUsername(), getPhotosOrder().getValue(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserPhotos(
                getListResource(), getUsername(), getPhotosOrder().getValue(), false);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
