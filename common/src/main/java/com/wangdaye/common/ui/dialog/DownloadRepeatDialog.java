package com.wangdaye.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
 * Download repeat dialog.
 *
 * This dialog is used to remain user that the download mission is repeat.
 *
 * */

public class DownloadRepeatDialog extends MysplashDialogFragment {

    @BindView(R2.id.dialog_download_repeat_container) CoordinatorLayout container;

    @OnClick(R2.id.dialog_download_repeat_checkBtn) void check() {
        if (listener != null) {
            listener.onCheck(downloadKey);
        }
        dismiss();
    }

    @OnClick(R2.id.dialog_download_repeat_downloadBtn) void download() {
        if (listener != null) {
            listener.onDownload(downloadKey);
        }
        dismiss();
    }

    private OnCheckOrDownloadListener listener;

    private Object downloadKey; // the thing that need to be downloaded.

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_download_repeat, null, false);
        ButterKnife.bind(this, view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    public void setDownloadKey(Object obj) {
        this.downloadKey = obj;
    }

    // interface.

    // on check or download listener.

    public interface OnCheckOrDownloadListener {
        void onCheck(Object obj);
        void onDownload(Object obj);
    }

    public void setOnCheckOrDownloadListener(OnCheckOrDownloadListener l) {
        this.listener = l;
    }
}
