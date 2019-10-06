package com.wangdaye.search.vm;

import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.search.repository.PhotoSearchPageViewRepository;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class PhotoSearchPageViewModel extends AbstractSearchPageViewModel<Photo, PhotoEvent> {

    private PhotoSearchPageViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable downloadEventDisposable;

    @Inject
    public PhotoSearchPageViewModel(PhotoSearchPageViewRepository repository,
                                    PhotoEventResponsePresenter photoEventResponsePresenter,
                                    DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super(PhotoEvent.class);

        this.repository = repository;
        this.photoEventResponsePresenter = photoEventResponsePresenter;
        this.downloadEventResponsePresenter = downloadEventResponsePresenter;

        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(event -> this.downloadEventResponsePresenter.updatePhoto(
                        getListResource(),
                        event,
                        false
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        repository.cancel();
        photoEventResponsePresenter.clearResponse();
        downloadEventResponsePresenter.clearResponse();

        downloadEventDisposable.dispose();
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
        photoEventResponsePresenter.updatePhoto(getListResource(), photoEvent.photo, false);
    }
}
