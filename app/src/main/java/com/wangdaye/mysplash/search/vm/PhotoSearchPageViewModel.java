package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.search.repository.PhotoSearchPageViewRepository;

import javax.inject.Inject;

public class PhotoSearchPageViewModel extends AbstractSearchPageViewModel<Photo, PhotoEvent> {

    private PhotoSearchPageViewRepository repository;
    private PhotoEventResponsePresenter presenter;

    @Inject
    public PhotoSearchPageViewModel(PhotoSearchPageViewRepository repository,
                                    PhotoEventResponsePresenter presenter) {
        super(PhotoEvent.class);
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
        presenter.clearResponse();
    }

    @Override
    public void refresh() {
        repository.getPhotos(getListResource(), getQuery(), true);
    }

    @Override
    public void load() {
        repository.getPhotos(getListResource(), getQuery(), false);
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        presenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
