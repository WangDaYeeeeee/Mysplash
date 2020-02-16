package com.wangdaye.user.vm;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.pager.PhotosPagerViewModel;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserPhotosViewModel extends PhotosPagerViewModel
        implements UserPagerViewModel<Photo> {

    private UserPhotosViewRepository repository;
    private String username;

    @Inject
    public UserPhotosViewModel(UserPhotosViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> defaultResource, String defaultUsername) {
        if (super.init(defaultResource)) {
            setUsername(defaultUsername);
            refresh();
            return true;
        }
        return false;
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
        getRepository().getUserPhotos(this, getUsername(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserPhotos(this, getUsername(), false);
    }

    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    UserPhotosViewRepository getRepository() {
        return repository;
    }
}
