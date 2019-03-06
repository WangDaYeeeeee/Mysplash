package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.Calendar;

/**
 * Time picker dialog.
 * */

public class TimePickerDialog extends MysplashDialogFragment {

    private CoordinatorLayout container;
    private OnTimeChangedListener listener;

    private int hour;
    private int minute;

    private boolean startTime = true;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time_picker, null, false);
        this.initData();
        this.initWidget(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    public void setModel(boolean start) {
        this.startTime = start;
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    private void initWidget(View view) {
        this.container = view.findViewById(R.id.dialog_time_picker_container);

        Button done = view.findViewById(R.id.dialog_time_picker_done);
        done.setOnClickListener(v -> {
            String hourText;
            String minuteText;

            if (hour < 10) {
                hourText = "0" + Integer.toString(hour);
            } else {
                hourText = Integer.toString(hour);
            }

            if (minute < 10) {
                minuteText = "0" + Integer.toString(minute);
            } else {
                minuteText = Integer.toString(minute);
            }

            if (startTime) {
                ThemeManager.getInstance(getActivity())
                        .setNightStartTime(getActivity(), hourText + ":" + minuteText);
            } else {
                ThemeManager.getInstance(getActivity())
                        .setNightEndTime(getActivity(), hourText + ":" + minuteText);
            }

            if (listener != null) {
                listener.timeChanged();
            }

            dismiss();
        });

        Button cancel = view.findViewById(R.id.dialog_time_picker_cancel);
        cancel.setOnClickListener(v -> dismiss());

        TimePicker timePicker = view.findViewById(R.id.dialog_time_picker_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            this.hour = hourOfDay;
            this.minute = minute;
        });
    }

    // interface.

    // on time changed listener.

    public interface OnTimeChangedListener {
        void timeChanged();
    }

    public void setOnTimeChangedListener(OnTimeChangedListener l) {
        this.listener = l;
    }
}