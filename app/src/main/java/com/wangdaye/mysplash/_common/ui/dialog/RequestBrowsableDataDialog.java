package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

/**
 * Request browsable data dialog.
 * */

public class RequestBrowsableDataDialog extends DialogFragment {

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_request_browsable_data, null, false);
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
