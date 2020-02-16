package com.wangdaye.main.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.pager.PhotosPagerViewModel;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.main.repository.PhotosHomePageViewRepository;

import androidx.annotation.NonNull;

import javax.inject.Inject;

/**
 * Home photos view model.
 * */
public class PhotosHomePageViewModel extends PhotosPagerViewModel {

    private PhotosHomePageViewRepository repository;

    @Inject
    public PhotosHomePageViewModel(PhotosHomePageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> resource) {
        boolean init = super.init(resource);
        if (init) {
            refresh();
        }
        return init;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getPhotos(this, true);
    }

    @Override
    public void load() {
        repository.getPhotos(this, false);
    }
}
