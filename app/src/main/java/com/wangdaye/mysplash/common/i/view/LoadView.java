package com.wangdaye.mysplash.common.i.view;

import androidx.annotation.Nullable;
import android.view.View;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.LoadModel;

/**
 * Load view.
 *
 * A view which can control multiple display states.
 *
 * */

public interface LoadView {

    void animShow(View v);
    void animHide(View v);

    void setLoadingState(@Nullable MysplashActivity activity, @LoadModel.StateRule int old);
    void setFailedState(@Nullable MysplashActivity activity, @LoadModel.StateRule int old);
    void setNormalState(@Nullable MysplashActivity activity, @LoadModel.StateRule int old);
}
