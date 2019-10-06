package com.wangdaye.common.base.fragment;

import android.app.Activity;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

/**
 * Mysplash fragment.
 *
 * Basic Fragment class for Mysplash.
 *
 * */

public abstract class MysplashFragment extends Fragment {

    // style.

    public abstract void initStatusBarStyle(Activity activity, boolean newInstance);

    public abstract void initNavigationBarStyle(Activity activity, boolean newInstance);

    /**
     * This method can tell you if we need set dark status bar style.
     *
     * @return If we need to set status bar style only white.
     * */
    public abstract boolean needSetDarkStatusBar();

    // snack bar.

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();

    // control.

    /**
     * This method can tell you if the list view need back to top when user press the back button.
     *
     * @return if list view need back to top.
     * */
    public abstract boolean needBackToTop();
    public abstract void backToTop();
}
