package com.wangdaye.muzei.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.muzei.R;
import com.wangdaye.muzei.R2;
import com.wangdaye.muzei.activity.MuzeiCollectionSourceConfigActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmExitWithoutSaveDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_confirm_exit_without_save_container) CoordinatorLayout container;

    @OnClick(R2.id.dialog_confirm_exit_without_save_saveBtn) void save() {
        ((MuzeiCollectionSourceConfigActivity) Objects.requireNonNull(getActivity()))
                .saveConfiguration();
        dismiss();
    }

    @OnClick(R2.id.dialog_confirm_exit_without_save_exitBtn) void exit() {
        ((MuzeiCollectionSourceConfigActivity) Objects.requireNonNull(getActivity()))
                .finishSelf(true);
        dismiss();
    }

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
}
