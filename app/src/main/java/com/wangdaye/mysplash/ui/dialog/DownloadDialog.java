package com.wangdaye.mysplash.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;

/**
 * Download dialog.
 * */

public class DownloadDialog extends DialogFragment {

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download, null, false);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
