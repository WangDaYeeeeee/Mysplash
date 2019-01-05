package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.ui.activity.muzei.MuzeiCollectionSourceConfigActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Download repeat dialog.
 *
 * This dialog is used to remain user that the download mission is repeat.
 *
 * */

public class ConfirmExitWithoutSaveDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_confirm_exit_without_save_container)
    CoordinatorLayout container;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_confirm_exit_without_save, null, false);
        ButterKnife.bind(this, view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // interface.

    // on click listener.

    @OnClick(R.id.dialog_confirm_exit_without_save_saveBtn) void save() {
        ((MuzeiCollectionSourceConfigActivity) getActivity()).saveConfiguration();
        dismiss();
    }

    @OnClick(R.id.dialog_confirm_exit_without_save_exitBtn) void exit() {
        ((MuzeiCollectionSourceConfigActivity) getActivity())
                .finishSelf(true);
        dismiss();
    }
}
