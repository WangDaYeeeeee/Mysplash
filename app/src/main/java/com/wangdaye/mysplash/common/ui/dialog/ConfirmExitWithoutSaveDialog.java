package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashDialogFragment;
import com.wangdaye.mysplash.common.ui.activity.MuzeiConfigurationActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

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

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_confirm_exit_without_save, null, false);
        ButterKnife.bind(this, view);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget(View v) {
        TextView content = ButterKnife.findById(v, R.id.dialog_confirm_exit_without_save_text);
        DisplayUtils.setTypeface(getActivity(), content);
    }

    // interface.

    // on click listener.

    @OnClick(R.id.dialog_confirm_exit_without_save_saveBtn) void save() {
        ((MuzeiConfigurationActivity) getActivity()).saveConfiguration();
        dismiss();
    }

    @OnClick(R.id.dialog_confirm_exit_without_save_exitBtn) void exit() {
        ((MuzeiConfigurationActivity) getActivity())
                .finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        dismiss();
    }
}
