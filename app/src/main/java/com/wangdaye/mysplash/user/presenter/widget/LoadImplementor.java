package com.wangdaye.mysplash.user.presenter.widget;

import androidx.annotation.NonNull;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;

/**
 * Load implementor.
 * */

public class LoadImplementor implements LoadPresenter {

    private LoadModel model;
    private LoadView view;

    public LoadImplementor(LoadModel model, LoadView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void bindActivity(@NonNull MysplashActivity activity) {
        model.setActivity(activity);
    }

    @Override
    public int getLoadState() {
        return model.getState();
    }

    @Override
    public void setLoadingState() {
        int old = model.getState();
        if (model.getState() != LoadModel.LOADING_STATE) {
            model.setState(LoadModel.LOADING_STATE);
            view.setLoadingState(model.getActivity(), old);
        }
    }

    @Override
    public void setFailedState() {
        int old = model.getState();
        if (model.getState() != LoadModel.FAILED_STATE) {
            model.setState(LoadModel.FAILED_STATE);
            view.setFailedState(model.getActivity(), old);
        }
    }

    @Override
    public void setNormalState() {
        int old = model.getState();
        if (model.getState() != LoadModel.NORMAL_STATE) {
            model.setState(LoadModel.NORMAL_STATE);
            view.setNormalState(model.getActivity(), old);
        }
    }
}
