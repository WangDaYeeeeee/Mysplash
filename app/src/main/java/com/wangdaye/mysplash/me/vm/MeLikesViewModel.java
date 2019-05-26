package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.mysplash.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.me.repository.MePhotosViewRepository;

import javax.inject.Inject;

public class MeLikesViewModel extends MePhotosViewModel {

    @Inject
    public MeLikesViewModel(MePhotosViewRepository repository,
                            PhotoEventResponsePresenter photoEventResponsePresenter,
                            DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super(repository, photoEventResponsePresenter, downloadEventResponsePresenter);
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserLikes(getListResource(), getPhotosOrder().getValue(), true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserLikes(getListResource(), getPhotosOrder().getValue(), false);
    }
}
