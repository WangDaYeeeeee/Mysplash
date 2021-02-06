package com.wangdaye.me.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.vm.pager.PhotosPagerViewModel;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.repository.MePhotosViewRepository;

import javax.inject.Inject;

public class MePhotosViewModel extends PhotosPagerViewModel
        implements MePagerViewModel<Photo> {

    private final MePhotosViewRepository repository;
    private String username;

    @Inject
    public MePhotosViewModel(MePhotosViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> defaultResource) {
        if (super.init(defaultResource)) {
            setUsername(AuthManager.getInstance().getUsername());
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
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(this, true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserPhotos(this, false);
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

    MePhotosViewRepository getRepository() {
        return repository;
    }
}
