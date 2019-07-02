package com.wangdaye.mysplash.common.basic.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import dagger.android.support.AndroidSupportInjection;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

/**
 * Mysplash dialog fragment.
 *
 * Basic DialogFragment class for Mysplash.
 *
 * */

public abstract class MysplashDialogFragment extends DialogFragment {

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof MysplashActivity) {
            ((MysplashActivity) activity).getDialogList().add(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity activity = getActivity();
        if (activity instanceof MysplashActivity) {
            ((MysplashActivity) activity).getDialogList().remove(this);
        }
    }

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();
}
