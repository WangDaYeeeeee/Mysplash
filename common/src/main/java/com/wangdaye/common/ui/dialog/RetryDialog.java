package com.wangdaye.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Retry dialog.
 *
 * */

public class RetryDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_retry_container) CoordinatorLayout container;
    @OnClick(R2.id.dialog_retry_button) void retry() {
        if (retryListener != null) {
            retryListener.onRetryButtonClicked();
        }
    }

    private OnRetryListener retryListener;
    private OnBackPressedListener backPressedListener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_retry, null, false);
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

    public interface OnRetryListener {
        void onRetryButtonClicked();
    }

    public void setOnRetryListener(OnRetryListener l) {
        retryListener = l;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener l) {
        backPressedListener = l;
    }
}
