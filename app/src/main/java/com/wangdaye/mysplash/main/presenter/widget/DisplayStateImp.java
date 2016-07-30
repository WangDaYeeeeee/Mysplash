package com.wangdaye.mysplash.main.presenter.widget;

import com.wangdaye.mysplash.main.model.widget.DisplayStateObject;
import com.wangdaye.mysplash.main.model.widget.i.DisplayStateModel;
import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;
import com.wangdaye.mysplash.main.presenter.widget.i.DisplayStatePresenter;
import com.wangdaye.mysplash.main.view.widget.i.ContentView;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;

/**
 * Display state implementor.
 * */

public class DisplayStateImp
        implements DisplayStatePresenter {
    // model.
    private PhotoStateModel photoStateModel;
    private DisplayStateModel displayStateModel;

    // view.
    private ContentView contentView;
    private LoadingView loadingView;
    private PhotosView photosView;

    /** <br> life cycle. */

    public DisplayStateImp(PhotoStateModel photoStateModel, DisplayStateModel displayStateModel,
                           ContentView contentView, LoadingView loadingView, PhotosView photosView) {
        this.photoStateModel = photoStateModel;
        this.displayStateModel = displayStateModel;
        this.contentView = contentView;
        this.loadingView = loadingView;
        this.photosView = photosView;
    }

    /** <br> presenter. */

    @Override
    public void setLoadingState() {
        switch (displayStateModel.getState()) {
            case DisplayStateObject.INIT_LOAD_FAILED_STATE:
                displayStateModel.setState(DisplayStateObject.INIT_LOADING_STATE);
                contentView.animHide(loadingView.getFeedbackContainer());
                contentView.animShow(loadingView.getProgressView());
                break;

            case DisplayStateObject.NORMAL_DISPLAY_STATE:
                displayStateModel.setState(DisplayStateObject.INIT_LOADING_STATE);
                photoStateModel.getAdapter().clearItem();
                contentView.animHide(photosView.getPhotosView());
                contentView.animShow(loadingView.getLoadingView());
                break;
        }
    }

    @Override
    public void setFailedState() {
        if (displayStateModel.getState() == DisplayStateObject.INIT_LOADING_STATE) {
            displayStateModel.setState(DisplayStateObject.INIT_LOAD_FAILED_STATE);
            contentView.animHide(loadingView.getProgressView());
            contentView.animShow(loadingView.getFeedbackContainer());
        }
    }

    @Override
    public void setNormalState() {
        if (displayStateModel.getState() == DisplayStateObject.INIT_LOADING_STATE) {
            displayStateModel.setState(DisplayStateObject.NORMAL_DISPLAY_STATE);
            contentView.animHide(loadingView.getLoadingView());
            contentView.animShow(photosView.getPhotosView());
        }
    }
}
