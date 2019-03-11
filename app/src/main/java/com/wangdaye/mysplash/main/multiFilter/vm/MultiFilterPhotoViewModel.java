package com.wangdaye.mysplash.main.multiFilter.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.main.multiFilter.MultiFilterPhotoViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MultiFilterPhotoViewModel extends PagerViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private MultiFilterPhotoViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    private String query;
    private String username;
    private String orientation;
    private boolean featured;

    @Inject
    public MultiFilterPhotoViewModel(MultiFilterPhotoViewRepository repository,
                                     PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
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
        presenter.clearResponse();
        disposable.dispose();
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

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        presenter.updatePhoto(getListResource(), photoEvent.photo, true);
    }
}
