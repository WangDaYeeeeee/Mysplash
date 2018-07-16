package com.wangdaye.mysplash.common.i.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

/**
 * Load model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.LoadView}.
 *
 * */

public interface LoadModel {

    int LOADING_STATE = 0;
    int FAILED_STATE = -1;
    int NORMAL_STATE = 1;
    @IntDef({LOADING_STATE, FAILED_STATE, NORMAL_STATE})
    @interface StateRule {}

    @Nullable
    MysplashActivity getActivity();
    void setActivity(@NonNull MysplashActivity activity);

    @StateRule
    int getState();
    void setState(@StateRule int state);
}
