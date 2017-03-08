package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Path dialog.
 * */

public class PathDialog extends MysplashDialogFragment
        implements View.OnClickListener {
    // widget
    private CoordinatorLayout container;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_path, null, false);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_path_container);

        TextView content = (TextView) v.findViewById(R.id.dialog_path_text);
        DisplayUtils.setTypeface(getActivity(), content);

        v.findViewById(R.id.dialog_path_copyBtn).setOnClickListener(this);
        v.findViewById(R.id.dialog_path_enterBtn).setOnClickListener(this);
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_path_copyBtn:
                ((ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE))
                        .setPrimaryClip(
                                ClipData.newPlainText(
                                        "storage/emulated/0/Pictures/Mysplash",
                                        "storage/emulated/0/Pictures/Mysplash"));
                break;

            case R.id.dialog_path_enterBtn:
                dismiss();
                break;
        }
    }
}
