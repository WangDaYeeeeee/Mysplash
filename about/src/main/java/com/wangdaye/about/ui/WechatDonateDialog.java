package com.wangdaye.about.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.wangdaye.about.R;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.common.ui.widget.CoverImageView;

public class WechatDonateDialog extends MysplashDialogFragment {

    private CoordinatorLayout container;

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_donate_wechat, null, false);

        container = view.findViewById(R.id.dialog_donate_wechat);

        CoverImageView image = view.findViewById(R.id.dialog_donate_wechat_img);
        image.setSize(3, 4);
        image.setShowShadow(false);
        Glide.with(getActivity())
                .load(R.drawable.donate_wechat)
                .into(image);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }
}