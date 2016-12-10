package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Rate limit dialog.
 * */

public class RateLimitDialog extends MysplashDialogFragment
        implements View.OnClickListener {
    // widget
    private CoordinatorLayout container;

    /** <br> utils. */

    public static void checkAndNotify(Activity a, String remaining) {
        if (!TextUtils.isEmpty(remaining)) {
            if (Integer.parseInt(remaining) < 0) {
                RateLimitDialog dialog = new RateLimitDialog();
                dialog.show(a.getFragmentManager(), remaining);
            }
        }
    }

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rate_limit, null, false);
        initWidget(view);
        setCancelable(false);
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
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_rate_limit_container);

        TextView content = (TextView) v.findViewById(R.id.dialog_rate_limit_content);
        DisplayUtils.setTypeface(getActivity(), content);

        v.findViewById(R.id.dialog_rate_limit_button).setOnClickListener(this);
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_rate_limit_button:
                dismiss();
                break;
        }
    }
}
