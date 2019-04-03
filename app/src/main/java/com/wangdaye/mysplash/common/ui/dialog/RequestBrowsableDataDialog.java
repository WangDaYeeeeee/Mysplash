package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.KeyEvent;
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

    @BindView(R.id.dialog_request_browsable_data_container) CoordinatorLayout container;

    private OnBackPressedListener backPressedListener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_request_browsable_data, null, false);
        ButterKnife.bind(this, view);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setOnKeyListener((dialog1, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                        dismiss();
                        if (backPressedListener != null) {
                            backPressedListener.onBackPressed();
                        }
                    }
                    return false;
                }).create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener l) {
        backPressedListener = l;
    }
}
