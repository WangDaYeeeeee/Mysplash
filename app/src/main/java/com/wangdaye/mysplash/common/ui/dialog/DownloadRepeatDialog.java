package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

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
    // widget
    @BindView(R.id.dialog_download_repeat_container) CoordinatorLayout container;
    private OnCheckOrDownloadListener listener;

    // data
    private Object downloadKey; // the thing that need to be downloaded.

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_download_repeat, null, false);
        ButterKnife.bind(this, view);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget(View v) {
        TextView content = ButterKnife.findById(v, R.id.dialog_download_repeat_text);
        DisplayUtils.setTypeface(getActivity(), content);
    }

    /** <br> data. */

    public void setDownloadKey(Object obj) {
        this.downloadKey = obj;
    }

    /** <br> interface. */

    // on check or download listener.

    public interface OnCheckOrDownloadListener {
        void onCheck(Object obj);
        void onDownload(Object obj);
    }

    public void setOnCheckOrDownloadListener(OnCheckOrDownloadListener l) {
        this.listener = l;
    }

    // on click listener.

    @OnClick(R.id.dialog_download_repeat_checkBtn) void check() {
        if (listener != null) {
            listener.onCheck(downloadKey);
        }
        dismiss();
    }

    @OnClick(R.id.dialog_download_repeat_downloadBtn) void download() {
        if (listener != null) {
            listener.onDownload(downloadKey);
        }
        dismiss();
    }
}
