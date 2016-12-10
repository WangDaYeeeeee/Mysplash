package com.wangdaye.mysplash._common.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Save instance fragment.
 * */

public abstract class MysplashFragment extends Fragment {
    // widget
    private Bundle bundle;

    /** <br> view. */

    public abstract View getSnackbarContainer();

    /** <br> data. */

    public abstract MysplashFragment readBundle(@Nullable Bundle savedInstanceState);
    public abstract void writeBundle(Bundle outState);

    @Nullable
    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle b) {
        bundle = b;
    }
}
