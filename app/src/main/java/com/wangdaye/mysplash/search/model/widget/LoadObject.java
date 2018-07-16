package com.wangdaye.mysplash.search.model.widget;

import android.support.annotation.NonNull;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.LoadModel;

/**
 * Load object.
 *
 * */

public class LoadObject implements LoadModel {

    private MysplashActivity activity;

    @StateRule
    private int state;

    public LoadObject(@StateRule int state) {
        this.state = state;
    }

    @Override
    public MysplashActivity getActivity() {
        return activity;
    }

    @Override
    public void setActivity(@NonNull MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }
}
