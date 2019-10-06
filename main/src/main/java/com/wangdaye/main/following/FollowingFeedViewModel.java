package com.wangdaye.main.following;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.PagerViewModel;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Following feed view model.
 * */
public class FollowingFeedViewModel extends PagerViewModel<Photo> {

    private FollowingFeedViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    private Consumer<PhotoEvent> photoEventConsumer = photoEvent ->
            photoEventResponsePresenter.updatePhoto(getListResource(), photoEvent.photo, true);

    private Consumer<DownloadEvent> downloadEventConsumer = event ->
            downloadEventResponsePresenter.updatePhoto(getListResource(), event, true);

    @Inject
    public FollowingFeedViewModel(FollowingFeedViewRepository repository,
                                  PhotoEventResponsePresenter photoEventResponsePresenter,
                                  DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super();

        this.repository = repository;
        this.photoEventResponsePresenter = photoEventResponsePresenter;
        this.downloadEventResponsePresenter = downloadEventResponsePresenter;

        this.photoEventDisposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(photoEventConsumer);
        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(downloadEventConsumer);
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> resource) {
        if (super.init(resource)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        repository.cancel();
        photoEventResponsePresenter.clearResponse();
        downloadEventResponsePresenter.clearResponse();

        photoEventDisposable.dispose();
        downloadEventDisposable.dispose();
    }

    @Override
    public void refresh() {
        repository.getFollowingFeeds(getListResource(), true);
    }

    @Override
    public void load() {
        repository.getFollowingFeeds(getListResource(), false);
    }
}
