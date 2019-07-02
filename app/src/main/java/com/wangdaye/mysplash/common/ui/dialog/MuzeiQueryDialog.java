package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.muzei.MuzeiOptionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Muzei query dialog.
 * */

public class MuzeiQueryDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_muzei_query_container) CoordinatorLayout container;
    @BindView(R.id.dialog_muzei_query_text) TextInputEditText query;

    @OnClick(R.id.dialog_muzei_query_enterBtn) void enter() {
        if (listener != null) {
            Editable e = query.getText();
            listener.onQueryChanged(e == null ? "" : e.toString());
        }
        dismiss();
    }

    @OnClick(R.id.dialog_muzei_query_cancelBtn) void cancel() {
        dismiss();
    }

    private OnQueryChangedListener listener;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_muzei_query, null, false);
        ButterKnife.bind(this, view);
        initWidget();
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        setCancelable(false);
        query.setText(MuzeiOptionManager.getInstance(getActivity()).getQuery());
    }

    public interface OnQueryChangedListener {
        void onQueryChanged(String query);
    }

    public void setOnQueryChangedListener(@Nullable OnQueryChangedListener l) {
        listener = l;
    }
}
