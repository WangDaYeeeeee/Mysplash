package com.wangdaye.mysplash._common.ui._basic;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;

/**
 * Mysplash dialog fragment.
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

    public abstract View getSnackbarContainer();
}
