package com.wangdaye.mysplash._common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.ApiManager;

/**
 * Custom api dialog.
 * */

public class CustomApiDialog extends MysplashDialogFragment
        implements View.OnClickListener {
    // widget
    private CoordinatorLayout container;
    private TextView key;
    private TextView secret;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_custom_api, null, false);
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
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_custom_api_container);

        this.key = (TextView) v.findViewById(R.id.dialog_custom_api_key);
        DisplayUtils.setTypeface(getActivity(), key);
        if (!TextUtils.isEmpty(Mysplash.getInstance().getCustomApiKey())) {
            key.setText(Mysplash.getInstance().getCustomApiKey());
        }

        this.secret = (TextView) v.findViewById(R.id.dialog_custom_api_secret);
        DisplayUtils.setTypeface(getActivity(), secret);
        if (!TextUtils.isEmpty(Mysplash.getInstance().getCustomApiSecret())) {
            secret.setText(Mysplash.getInstance().getCustomApiSecret());
        }

        v.findViewById(R.id.dialog_custom_api_cancelBtn).setOnClickListener(this);
        v.findViewById(R.id.dialog_custom_api_enterBtn).setOnClickListener(this);
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_custom_api_cancelBtn:
                dismiss();
                break;

            case R.id.dialog_custom_api_enterBtn:
                ApiManager.getInstance(getActivity())
                        .writeCustomApi(
                                key.getText().toString(),
                                secret.getText().toString());
                ApiManager.getInstance(getActivity()).destroy();
                Mysplash.getInstance().setCustomApiKey(key.getText().toString());
                Mysplash.getInstance().setCustomApiSecret(secret.getText().toString());
                dismiss();
                break;
        }
    }
}
