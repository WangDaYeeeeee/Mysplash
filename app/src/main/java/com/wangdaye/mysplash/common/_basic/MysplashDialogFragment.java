package com.wangdaye.mysplash.common._basic;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;

import com.wangdaye.mysplash.Mysplash;

/**
 * Mysplash dialog fragment.
 *
 * Basic DialogFragment class for Mysplash.
 *
 * */

public abstract class MysplashDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().getTopActivity().getDialogList().add(this);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().getTopActivity().getDialogList().remove(this);
    }

    /**
     * Get the container CoordinatorLayout of snack bar.
     *
     * @return The container layout of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();
}
