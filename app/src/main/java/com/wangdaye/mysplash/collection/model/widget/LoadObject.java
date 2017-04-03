package com.wangdaye.mysplash.collection.model.widget;

import android.support.annotation.IntDef;

import com.wangdaye.mysplash.common.i.model.LoadModel;

/**
 * Load object.
 * */

public class LoadObject implements LoadModel {
    // data
    @StateRule
    private int state;

    public static final int LOADING_STATE = 0;
    public static final int FAILED_STATE = -1;
    public static final int NORMAL_STATE = 1;
    @IntDef({LOADING_STATE, FAILED_STATE, NORMAL_STATE})
    private @interface StateRule {}

    /** <br> life cycle. */

    public LoadObject(@StateRule int state) {
        this.state = state;
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
