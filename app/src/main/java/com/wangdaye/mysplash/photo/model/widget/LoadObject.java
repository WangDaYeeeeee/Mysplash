package com.wangdaye.mysplash.photo.model.widget;

import com.wangdaye.mysplash._common.i.model.LoadModel;

/**
 * Load object.
 * */

public class LoadObject implements LoadModel {
    // data
    private int state;
    public static final int LOADING_STATE = 0;
    public static final int FAILED_STATE = -1;
    public static final int NORMAL_STATE = 1;

    /** <br> life cycle. */

    public LoadObject(int state) {
        this.state = state;
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
