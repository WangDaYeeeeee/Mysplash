package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.MysplashDialogFragment;

/**
 * Request browsable data dialog.
 * */

public class RequestBrowsableDataDialog extends MysplashDialogFragment {
    // widget
    private RelativeLayout container;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_request_browsable_data, null, false);
        container = (RelativeLayout) view.findViewById(R.id.dialog_request_browsable_data_container);
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }
}
