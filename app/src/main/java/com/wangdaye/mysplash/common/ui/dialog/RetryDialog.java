package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Retry dialog.
 *
 * */

public class RetryDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_retry_container) CoordinatorLayout container;
    @OnClick(R.id.dialog_retry_button) void retry() {
        if (listener != null) {
            listener.onRetryButtonClicked();
        }
    }

    private OnRetryListener listener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_retry, null, false);
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

    public interface OnRetryListener {
        void onRetryButtonClicked();
    }

    public void setOnRetryListener(OnRetryListener l) {
        listener = l;
    }
}
