package com.wangdaye.mysplash.common._basic.fragment;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;

/**
 * Mysplash fragment.
 *
 * Basic Fragment class for Mysplash.
 *
 * */

public abstract class MysplashFragment extends Fragment {

    // style.

    public abstract void initStatusBarStyle();

    public abstract void initNavigationBarStyle();

    /**
     * This method can tell you if we need set dark status bar style.
     *
     * @return If we need to set status bar style only white.
     * */
    public abstract boolean needSetDarkStatusBar();

    // save instance.

    /**
     * Write large data to the BaseSavedStateFragment when application saving instance state.
     *
     * @param outState The BaseSavedStateFragment which is used to save large data.
     * */
    public abstract void writeLargeData(MysplashActivity.BaseSavedStateFragment outState);

    /**
     * Read large data from the BaseSavedStateFragment when application restarting.
     *
     * @param savedInstanceState The BaseSavedStateFragment which is used to save large data.
     * */
    public abstract void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState);

    // snack bar.

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();

    // control.

    /**
     * Handle the result data from last activity.
     *
     * @param requestCode {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * @param resultCode  {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * @param data        {@link android.app.Activity#onActivityResult(int, int, Intent)}.
     * */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        // do nothing.
    }

    /**
     * This method can tell you if the list view need back to top when user press the back button.
     *
     * @return if list view need back to top.
     * */
    public abstract boolean needBackToTop();
    public abstract void backToTop();
}
