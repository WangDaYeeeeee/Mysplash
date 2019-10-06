package com.wangdaye.downloader.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.downloader.R;
import com.wangdaye.downloader.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Path dialog.
 *
 * This dialog is used to show the download path.
 *
 * */

public class PathDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_path_container) CoordinatorLayout container;

    @OnClick(R2.id.dialog_path_copyBtn) void copy() {
        if (getActivity() == null) {
            return;
        }
        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(
                Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.setPrimaryClip(
                ClipData.newPlainText(
                        "storage/emulated/0/Pictures/Mysplash",
                        "storage/emulated/0/Pictures/Mysplash"
                )
        );
    }

    @OnClick(R2.id.dialog_path_enterBtn) void enter() {
        dismiss();
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_path, null, false);
        ButterKnife.bind(this, view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }
}
