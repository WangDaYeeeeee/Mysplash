package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.common.utils.bus.PhotoEvent;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.search.repository.PhotoSearchPageViewRepository;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PhotoSearchPageViewModel extends AbstractSearchPageViewModel<Photo>
        implements Consumer<PhotoEvent> {

    private PhotoSearchPageViewRepository repository;
    private PhotoEventResponsePresenter presenter;
    private Disposable disposable;

    @Inject
    public PhotoSearchPageViewModel(PhotoSearchPageViewRepository repository,
                                    PhotoEventResponsePresenter presenter) {
        super();
        this.repository = repository;
        this.presenter = presenter;
        this.disposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(this);
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
