package com.wangdaye.me.vm;

import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.me.repository.MePhotosViewRepository;

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
