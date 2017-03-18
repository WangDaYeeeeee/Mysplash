package com.wangdaye.mysplash._common._basic;

import android.support.v4.app.Fragment;
import android.view.View;

import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Save instance fragment.
 * */

public abstract class MysplashFragment extends Fragment {

    /** <br> view. */

    public void setStatusBarStyle(boolean onlyWhite) {
        DisplayUtils.setStatusBarStyle(getActivity(), onlyWhite);
    }

    public abstract View getSnackbarContainer();

    /** <br> data. */

    public abstract boolean needSetOnlyWhiteStatusBarText();

    public abstract boolean needPagerBackToTop();
    public abstract void backToTop();

    public abstract void writeLargeData(MysplashActivity.BaseSavedStateFragment outState);
    public abstract void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState);
}
