package com.wangdaye.photo.ui.holder;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.common.utils.AnimUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Progress holder.
 *
 * This view holder is used to show the progress.
 *
 * */

public class ProgressHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_progress_container) RelativeLayout container;
    @BindView(R2.id.item_photo_3_progress_progressView) CircularProgressView progress;
    @BindView(R2.id.item_photo_3_progress_button) Button button;
    @OnClick(R2.id.item_photo_3_progress_button) void retryRefresh() {
        MysplashActivity activity =  MysplashApplication.getInstance().getTopActivity();
        if (activity instanceof PhotoActivity) {
            setProgressState();
            ((PhotoActivity) activity).initRefresh();
        }
    }

    private boolean failed;
    public static final int TYPE_PROGRESS = 1;

    public ProgressHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        progress.setAlpha(1f);
        button.setAlpha(0f);
        button.setVisibility(View.GONE);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    public void setFailedState() {
        if (!failed) {
            failed = true;
            AnimUtils.animShow(button, 150, button.getAlpha(), 1f);
            AnimUtils.animHide(progress, 150, progress.getAlpha(), 0f, false);
        }
    }

    private void setProgressState() {
        if (failed) {
            failed = false;
            AnimUtils.animShow(progress, 150, progress.getAlpha(), 1f);
            AnimUtils.animHide(button, 150, button.getAlpha(), 0f, true);
        }
    }
}
