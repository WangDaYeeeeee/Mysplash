package com.wangdaye.mysplash.main.home.vm;

import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.main.home.HomePhotosViewRepository;

import javax.inject.Inject;

/**
 * Home new pager model.
 * */
public class NewHomePhotosViewModel extends AbstractHomePhotosViewModel {

    @Inject
    public NewHomePhotosViewModel(HomePhotosViewRepository repository,
                                  PhotoEventResponsePresenter presenter) {
        super(repository, presenter);
    }

    @Override
    void getPhotosOrderly(boolean refresh) {
        getRepository().getPhotos(
                getListResource(), getPageList(), getPhotosOrder().getValue(),
                false, false, refresh);
    }

    @Override
    void getPhotosRandom(boolean refresh) {
        getRepository().getPhotos(
                getListResource(), getPageList(), getPhotosOrder().getValue(),
                false, true, refresh);
    }
}
