package com.wangdaye.mysplash.common.basic.fragment;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import dagger.android.support.AndroidSupportInjection;

/**
 * Mysplash fragment.
 *
 * Basic Fragment class for Mysplash.
 *
 * */

public abstract class MysplashFragment extends Fragment {

    @Override
    public void onAttach(@NotNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    // style.

    public abstract void initStatusBarStyle();

    public abstract void initNavigationBarStyle();

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

    // update data.

    public void updatePhoto(@NonNull Photo photo, Mysplash.MessageType type) {
        // do nothing.
    }

    public void updateUser(@NonNull User user, Mysplash.MessageType type) {
        // do nothing.
    }

    public void updateCollection(@NonNull Collection collection, Mysplash.MessageType type) {
        // do nothing.
    }
}
