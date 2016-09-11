package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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
 * Rate limit dialog.
 * */

public class RateLimitDialog extends DialogFragment
        implements View.OnClickListener {

    /** <br> utils. */

    public static void checkAndNotify(Activity a, String remaining) {
        if (Integer.parseInt(remaining) < 0) {
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(a.getFragmentManager(), remaining);
        }
    }

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mysplash.getInstance().setActivityInBackstage(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rate_limit, null, false);
        initWidget(view);
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    /** <br> UI. */

    private void initWidget(View v) {
        TextView content = (TextView) v.findViewById(R.id.dialog_rate_limit_content);
        TypefaceUtils.setTypeface(getActivity(), content);

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
