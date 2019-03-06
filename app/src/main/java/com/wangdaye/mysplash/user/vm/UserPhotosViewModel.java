package com.wangdaye.mysplash.user.vm;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserPhotosViewModel extends PagerViewModel<Photo> {

    private UserPhotosViewRepository repository;
    private MutableLiveData<String> photosOrder;
    private String username;

    @Inject
    public UserPhotosViewModel(UserPhotosViewRepository repository) {
        super();
        this.repository = repository;
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
}
