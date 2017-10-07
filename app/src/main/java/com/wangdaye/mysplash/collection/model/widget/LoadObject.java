package com.wangdaye.mysplash.collection.model.widget;

import android.support.annotation.NonNull;

import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.LoadModel;

/**
 * Load object.
 * */

public class LoadObject
        implements LoadModel {

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
    @StateRule
    public int getState() {
        return state;
    }

    @Override
    public void setState(@StateRule int state) {
        this.state = state;
    }
}
