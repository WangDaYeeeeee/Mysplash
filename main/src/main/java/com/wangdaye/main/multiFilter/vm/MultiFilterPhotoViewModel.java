package com.wangdaye.main.multiFilter.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.PagerViewModel;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.main.multiFilter.MultiFilterPhotoViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MultiFilterPhotoViewModel extends PagerViewModel<Photo> {

    private MultiFilterPhotoViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    private Consumer<PhotoEvent> photoEventConsumer = photoEvent ->
            photoEventResponsePresenter.updatePhoto(getListResource(), photoEvent.photo, true);

    private Consumer<DownloadEvent> downloadEventConsumer = event ->
            downloadEventResponsePresenter.updatePhoto(getListResource(), event, true);

    private String query;
    private String username;
    private String orientation;
    private boolean featured;

    @Inject
    public MultiFilterPhotoViewModel(MultiFilterPhotoViewRepository repository,
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

        this.query = "";
        this.username = "";
        this.orientation = "";
        this.featured = false;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> resource) {
        return super.init(resource);
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
        repository.getSearchResult(
                getListResource(), true, featured, username, query, orientation);
    }

    @Override
    public void load() {
        repository.getSearchResult(
                getListResource(), false, featured, username, query, orientation);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}
