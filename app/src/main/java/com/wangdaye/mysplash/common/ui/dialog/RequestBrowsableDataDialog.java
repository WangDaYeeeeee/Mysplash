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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Request browsable data dialog.
 *
 * This dialog is used to show a progress when a browsable view is requesting the browsable data
 * with a HTTP request.
 *
 * */

public class RequestBrowsableDataDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_request_browsable_data_container)
    CoordinatorLayout container;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_request_browsable_data, null, false);
        ButterKnife.bind(this, view);
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }
}
