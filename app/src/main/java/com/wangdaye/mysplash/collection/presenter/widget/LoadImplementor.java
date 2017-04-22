package com.wangdaye.mysplash.collection.presenter.widget;

import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.collection.model.widget.LoadObject;

/**
 * Load implementor.
 *
 * A {@link LoadPresenter} for
 * {@link com.wangdaye.mysplash.collection.view.widget.CollectionPhotosView}.
 *
 * */

public class LoadImplementor
        implements LoadPresenter {

    private LoadModel model;
    private LoadView view;

    public LoadImplementor(LoadModel model, LoadView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public int getLoadState() {
        return model.getState();
    }

    @Override
    public void setLoadingState() {
        switch (model.getState()) {
            case LoadObject.FAILED_STATE:
                model.setState(LoadObject.LOADING_STATE);
                view.setLoadingState();
                break;

            case LoadObject.NORMAL_STATE:
                model.setState(LoadObject.LOADING_STATE);
                view.resetLoadingState();
                break;
        }
    }

    @Override
    public void setFailedState() {
        if (model.getState() == LoadObject.LOADING_STATE) {
            model.setState(LoadObject.FAILED_STATE);
            view.setFailedState();
        }
    }

    @Override
    public void setNormalState() {
        if (model.getState() == LoadObject.LOADING_STATE) {
            model.setState(LoadObject.NORMAL_STATE);
            view.setNormalState();
        }
    }
}
