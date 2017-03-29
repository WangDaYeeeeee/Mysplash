package com.wangdaye.mysplash._common.ui.dialog;

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
import com.wangdaye.mysplash._common._basic.MysplashDialogFragment;
import com.wangdaye.mysplash._common.ui.activity.SetWallpaperActivity;
import com.wangdaye.mysplash._common.utils.AnimUtils;

/**
 * Wallpaper where dialog.
 * */

public class WallpaperWhereDialog extends MysplashDialogFragment
        implements View.OnClickListener {
    // widget
    private CoordinatorLayout container;
    private CircularProgressView progressView;
    private LinearLayout selector;

    private OnWhereSelectedListener listener;

    /** <br> life cycle. */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_wallpaper_where, null, false);
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
        this.container = (CoordinatorLayout) v.findViewById(R.id.dialog_wallpaper_where_container);
        this.progressView = (CircularProgressView) v.findViewById(R.id.dialog_wallpaper_where_progress);
        this.selector = (LinearLayout) v.findViewById(R.id.dialog_wallpaper_where_selector);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressView.setVisibility(View.GONE);
            selector.setVisibility(View.VISIBLE);
            v.findViewById(R.id.dialog_wallpaper_where_wallpaper).setOnClickListener(this);
            v.findViewById(R.id.dialog_wallpaper_where_lockscreen).setOnClickListener(this);
            v.findViewById(R.id.dialog_wallpaper_where_all).setOnClickListener(this);
        } else {
            progressView.setVisibility(View.VISIBLE);
            selector.setVisibility(View.GONE);
            if (listener != null) {
                listener.onWhereSelected(SetWallpaperActivity.WHERE_WALLPAPER);
            }
        }
    }

    /** <br> interface. */

    // on where selected swipeListener.

    public interface OnWhereSelectedListener {
        void onWhereSelected(int where);
    }

    public void setOnWhereSelectedListener(OnWhereSelectedListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_wallpaper_where_wallpaper:
                if (listener != null) {
                    listener.onWhereSelected(SetWallpaperActivity.WHERE_WALLPAPER);
                }
                setCancelable(false);
                AnimUtils.animShow(progressView);
                AnimUtils.animHide(selector);
                break;

            case R.id.dialog_wallpaper_where_lockscreen:
                if (listener != null) {
                    listener.onWhereSelected(SetWallpaperActivity.WHERE_LOCKSCREEN);
                }
                setCancelable(false);
                AnimUtils.animShow(progressView);
                AnimUtils.animHide(selector);
                break;

            case R.id.dialog_wallpaper_where_all:
                if (listener != null) {
                    listener.onWhereSelected(SetWallpaperActivity.WHERE_WALL_LOCK);
                }
                setCancelable(false);
                AnimUtils.animShow(progressView);
                AnimUtils.animHide(selector);
                break;
        }
    }
}