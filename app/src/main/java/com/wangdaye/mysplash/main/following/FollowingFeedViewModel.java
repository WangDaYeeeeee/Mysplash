package com.wangdaye.mysplash.main.following;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Following feed view model.
 * */
public class FollowingFeedViewModel extends PagerViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private FollowingFeedViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    @Inject
    public FollowingFeedViewModel(FollowingFeedViewRepository repository,
                                  PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
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
        presenter.clearResponse();
        disposable.dispose();
    }

    @Override
    public void refresh() {
        repository.getFollowingFeeds(getListResource(), true);
    }

    @Override
    public void load() {
        repository.getFollowingFeeds(getListResource(), false);
    }

    // interface.

    @Override
    public void accept(PhotoEvent photoEvent) {
        presenter.updatePhoto(getListResource(), photoEvent.photo, true);
    }
}
