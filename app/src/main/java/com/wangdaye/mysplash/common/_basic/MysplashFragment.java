package com.wangdaye.mysplash.common._basic;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Mysplash fragment.
 *
 * Basic Fragment class for Mysplash.
 *
 * */

public abstract class MysplashFragment extends Fragment {

    /** <br> view. */

    public void setStatusBarStyle(boolean onlyWhite) {
        DisplayUtils.setStatusBarStyle(getActivity(), onlyWhite);
    }

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();

    /** <br> data. */

    /**
     * This method can tell you if we need set status bar style only white.
     *
     * @return If we need to set status bar style only white.
     * */
    public abstract boolean needSetOnlyWhiteStatusBarText();

    /**
     * This method can tell you if the list view need back to top when user press the back button.
     *
     * @return if list view need back to top.
     * */
    public abstract boolean needBackToTop();
    public abstract void backToTop();

    /**
     * Write large data to the BaseSavedStateFragment when application saving instance state.
     *
     * @param outState The BaseSavedStateFragment which is used to save large data.
     * */
    public abstract void writeLargeData(MysplashActivity.BaseSavedStateFragment outState);

    /**
     * read large data from the BaseSavedStateFragment when application restarting.
     *
     * @param savedInstanceState The BaseSavedStateFragment which is used to save large data.
     * */
    public abstract void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState);
}
