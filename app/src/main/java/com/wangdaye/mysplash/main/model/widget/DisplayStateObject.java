package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.main.model.widget.i.DisplayStateModel;

/**
 * Display state object.
 * */

public class DisplayStateObject
        implements DisplayStateModel {
    // data
    private int state = INIT_LOADING_STATE;
    public static final int INIT_LOADING_STATE = 0;
    public static final int INIT_LOAD_FAILED_STATE = -1;
    public static final int NORMAL_DISPLAY_STATE = 1;

    /** <br> life cycle. */

    public DisplayStateObject(int state) {
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
