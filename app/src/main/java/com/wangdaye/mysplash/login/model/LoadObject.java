package com.wangdaye.mysplash.login.model;

import com.wangdaye.mysplash._common.i.model.LoadModel;

/**
 * Load object.
 * */

public class LoadObject
        implements LoadModel {
    // data
    private int state;
    public static final int NORMAL_STATE = 0;
    public static final int LOADING_STATE = 1;

    /** <br> life cycle. */

    public LoadObject(int state) {
        this.state = state;
    }

    /** <br> model. */

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }
}
