package com.wangdaye.mysplash.main.home.vm;

import com.wangdaye.mysplash.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.mysplash.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.main.home.HomePhotosViewRepository;

import javax.inject.Inject;

/**
 * Home featured pager model.
 * */
public class FeaturedHomePhotosViewModel extends AbstractHomePhotosViewModel {

    @Inject
    public FeaturedHomePhotosViewModel(HomePhotosViewRepository repository,
                                       PhotoEventResponsePresenter photoEventResponsePresenter,
                                       DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super(repository, photoEventResponsePresenter, downloadEventResponsePresenter);
    }

    @Override
    void getPhotosOrderly(boolean refresh) {
        getRepository().getPhotos(
                getListResource(),
                getPageList(),
                getPhotosOrder().getValue(),
                true,
                false,
                refresh
        );
    }

    @Override
    void getPhotosRandom(boolean refresh) {
        getRepository().getPhotos(
                getListResource(),
                getPageList(),
                getPhotosOrder().getValue(),
                true,
                true,
                refresh
        );
    }
}
