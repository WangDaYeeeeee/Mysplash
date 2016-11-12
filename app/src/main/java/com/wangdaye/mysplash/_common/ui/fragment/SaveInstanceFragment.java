package com.wangdaye.mysplash._common.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Save instance fragment.
 * */

public abstract class SaveInstanceFragment extends Fragment {
    // widget
    private Bundle bundle;

    /** <br> data. */

    public abstract SaveInstanceFragment readBundle(@Nullable Bundle savedInstanceState);
    public abstract void writeBundle(Bundle outState);

    @Nullable
    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle b) {
        bundle = b;
    }
}
