package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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

public class DownloadRepeatDialog extends MysplashDialogFragment
        implements View.OnClickListener {
    // widget
    private CoordinatorLayout container;
    private OnCheckOrDownloadListener listener;

    // data
    private Object downloadKey;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download_repeat, null, false);
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
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_download_repeat_container);

        TextView content = (TextView) v.findViewById(R.id.dialog_download_repeat_text);
        DisplayUtils.setTypeface(getActivity(), content);

        v.findViewById(R.id.dialog_download_repeat_checkBtn).setOnClickListener(this);
        v.findViewById(R.id.dialog_download_repeat_downloadBtn).setOnClickListener(this);
    }

    /** <br> data. */

    public void setDownloadKey(Object obj) {
        this.downloadKey = obj;
    }

    /** <br> interface. */

    // on check or download swipeListener.

    public interface OnCheckOrDownloadListener {
        void onCheck(Object obj);
        void onDownload(Object obj);
    }

    public void setOnCheckOrDownloadListener(OnCheckOrDownloadListener l) {
        this.listener = l;
    }

    // on click swipeListener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_download_repeat_checkBtn:
                if (listener != null) {
                    listener.onCheck(downloadKey);
                }
                dismiss();
                break;

            case R.id.dialog_download_repeat_downloadBtn:
                if (listener != null) {
                    listener.onDownload(downloadKey);
                }
                dismiss();
                break;
        }
    }
}
