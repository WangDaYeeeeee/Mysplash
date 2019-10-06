package com.wangdaye.main.home.vm;

import com.wangdaye.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.main.home.HomePhotosViewRepository;

import javax.inject.Inject;

/**
 * Home new pager model.
 * */
public class NewHomePhotosViewModel extends AbstractHomePhotosViewModel {

    @Inject
    public NewHomePhotosViewModel(HomePhotosViewRepository repository,
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
                false,
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
                false,
                true,
                refresh
        );
    }
}
