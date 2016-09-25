package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

/**
 * Confirm change theme dialog.
 * */

public class ConfirmRebootDialog extends DialogFragment
        implements View.OnClickListener {
    // widget
    private OnConfirmRebootListener listener;

    // data
    private int type;
    public static final int CHANGE_THEME_TYPE = 1;
    public static final int RESTART_APP_TYPE = 2;

    /** <br> life cycle. */

    public static ConfirmRebootDialog buildDialog(int type) {
        return new ConfirmRebootDialog().setType(type);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm_reboot, null, false);
        initWidget(view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    /** <br> UI. */

    @SuppressLint("SetTextI18n")
    private void initWidget(View v) {
        setCancelable(false);

        TextView title = (TextView) v.findViewById(R.id.dialog_confirm_reboot_title);
        TextView content = (TextView) v.findViewById(R.id.dialog_confirm_reboot_content);
        TypefaceUtils.setTypeface(getActivity(), content);

        switch (type) {
            case CHANGE_THEME_TYPE:
                title.setText(R.string.feedback_confirm_change_theme);
                content.setText(R.string.feedback_change_theme_warning);
                break;

            case RESTART_APP_TYPE:
                title.setText(R.string.feedback_confirm_restart);
                content.setText(R.string.feedback_restart_warning);
                break;
        }

        v.findViewById(R.id.dialog_confirm_reboot_enterBtn).setOnClickListener(this);
        v.findViewById(R.id.dialog_confirm_reboot_cancelBtn).setOnClickListener(this);
    }

    /** <br> data. */

    public ConfirmRebootDialog setType(int type) {
        this.type = type;
        return this;
    }

    /** <br> interface. */

    // on confirm change theme listener.

    public interface OnConfirmRebootListener {
        void onConfirm();
    }

    public void setOnConfirmRebootListener(OnConfirmRebootListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_confirm_reboot_enterBtn:
                if (listener != null) {
                    listener.onConfirm();
                }
                dismiss();
                break;

            case R.id.dialog_confirm_reboot_cancelBtn:
                dismiss();
                break;
        }
    }
}
