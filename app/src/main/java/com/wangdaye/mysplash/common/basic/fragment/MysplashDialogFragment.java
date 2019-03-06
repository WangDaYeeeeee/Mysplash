package com.wangdaye.mysplash.common.basic.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import dagger.android.support.AndroidSupportInjection;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

import org.jetbrains.annotations.NotNull;

/**
 * Mysplash dialog fragment.
 *
 * Basic DialogFragment class for Mysplash.
 *
 * */

public abstract class MysplashDialogFragment extends DialogFragment {

    @Override
    public void onAttach(@NotNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            activity.getDialogList().add(this);
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            activity.getDialogList().remove(this);
        }
    }

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();
}
