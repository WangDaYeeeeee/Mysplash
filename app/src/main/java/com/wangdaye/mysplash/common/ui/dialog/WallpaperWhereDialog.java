package com.wangdaye.mysplash.common.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.ui.activity.SetWallpaperActivity;
import com.wangdaye.mysplash.common.utils.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Wallpaper where dialog.
 *
 * This dialog is used to select where to set picture as wallpaper.
 *
 * */

public class WallpaperWhereDialog extends MysplashDialogFragment {

    @BindView(R.id.dialog_wallpaper_where_container)
    CoordinatorLayout container;

    @BindView(R.id.dialog_wallpaper_where_progress)
    CircularProgressView progressView;

    @BindView(R.id.dialog_wallpaper_where_selector)
    LinearLayout selector;

    private OnWhereSelectedListener listener;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_wallpaper_where, null, false);
        ButterKnife.bind(this, view);
        initWidget();
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressView.setVisibility(View.GONE);
            selector.setVisibility(View.VISIBLE);
        } else {
            progressView.setVisibility(View.VISIBLE);
            selector.setVisibility(View.GONE);
            if (listener != null) {
                listener.onWhereSelected(SetWallpaperActivity.WHERE_WALLPAPER);
            }
        }
    }

    // interface.

    // on where selected listener.

    public interface OnWhereSelectedListener {
        void onWhereSelected(@SetWallpaperActivity.WallpaperWhereRule int where);
    }

    public void setOnWhereSelectedListener(OnWhereSelectedListener l) {
        this.listener = l;
    }

    // on click listener.

    @OnClick(R.id.dialog_wallpaper_where_wallpaper) void clickWallpaper() {
        if (listener != null) {
            listener.onWhereSelected(SetWallpaperActivity.WHERE_WALLPAPER);
        }
        setCancelable(false);
        AnimUtils.animShow(progressView);
        AnimUtils.animHide(selector);
    }

    @OnClick(R.id.dialog_wallpaper_where_lockscreen) void clickLockScreen() {
        if (listener != null) {
            listener.onWhereSelected(SetWallpaperActivity.WHERE_LOCKSCREEN);
        }
        setCancelable(false);
        AnimUtils.animShow(progressView);
        AnimUtils.animHide(selector);
    }

    @OnClick(R.id.dialog_wallpaper_where_all) void clickAll() {
        if (listener != null) {
            listener.onWhereSelected(SetWallpaperActivity.WHERE_WALL_LOCK);
        }
        setCancelable(false);
        AnimUtils.animShow(progressView);
        AnimUtils.animHide(selector);
    }
}