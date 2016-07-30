package com.wangdaye.mysplash.photo.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;

/**
 * Download dialog.
 * */

public class DownloadDialog extends DialogFragment implements View.OnClickListener {
    // widget
    private TextView text;
    private OnDismissListener listener;

    // data
    private boolean cancel = false;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download, null, false);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cancel && listener != null) {
            listener.onCancel();
        } else if (listener != null) {
            listener.onDismiss();
        }
    }

    /** <br> UI. */

    @SuppressLint("SetTextI18n")
    private void initWidget(View v) {
        this.text = (TextView) v.findViewById(R.id.dialog_download_text);
        text.setText(getString(R.string.feedback_downloading) + " : " + getString(R.string.feedback_connecting));

        v.findViewById(R.id.dialog_download_cancelButton).setOnClickListener(this);
        v.findViewById(R.id.dialog_download_backgroundButton).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void setDownloadProgress(int percent) {
        if (percent < 0) {
            text.setText(getString(R.string.feedback_downloading) + " : " + getString(R.string.feedback_connecting));
        } else {
            text.setText(getString(R.string.feedback_downloading) + " : " + percent + "%");
        }
    }

    /** <br> interface. */

    public interface OnDismissListener {
        void onDismiss();
        void onCancel();
    }

    public void setOnDismissListener(OnDismissListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_download_cancelButton:
                cancel = true;
                dismiss();
                break;

            case R.id.dialog_download_backgroundButton:
                cancel = false;
                dismiss();
                break;
        }
    }
}
