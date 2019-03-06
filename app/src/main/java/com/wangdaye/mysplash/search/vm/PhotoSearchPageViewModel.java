package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.search.repository.PhotoSearchPageViewRepository;

import javax.inject.Inject;

public class PhotoSearchPageViewModel extends AbstractSearchPageViewModel<Photo> {

    private PhotoSearchPageViewRepository repository;

    @Inject
    public PhotoSearchPageViewModel(PhotoSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getPhotos(getListResource(), getQuery(), true);
    }

    @Override
    public void load() {
        repository.getPhotos(getListResource(), getQuery(), false);
    }
}
