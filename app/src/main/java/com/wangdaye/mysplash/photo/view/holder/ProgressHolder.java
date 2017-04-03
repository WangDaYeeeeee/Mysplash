package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;
import android.widget.Button;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Progress holder.
 *
 * This view holder is used to show the progress.
 *
 * */

public class ProgressHolder extends PhotoInfoAdapter.ViewHolder {
    // widget
    @BindView(R.id.item_photo_progress_progressView) CircularProgressView progress;
    @BindView(R.id.item_photo_progress_button) Button button;

    // data
    private boolean failed;
    public static final int TYPE_PROGRESS = 3;

    /** <br> life cycle. */

    public ProgressHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        if (a instanceof PhotoActivity) {
            failed = ((PhotoActivity) a).isLoadFailed();
        }
        if (failed) {
            progress.setAlpha(0f);
            button.setAlpha(1f);
            button.setVisibility(View.VISIBLE);
        } else {
            progress.setAlpha(1f);
            button.setAlpha(0f);
            button.setVisibility(View.GONE);
        }
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

    /** <br> interface. */

    @OnClick(R.id.item_photo_progress_button) void retryRefresh() {
        MysplashActivity activity =  Mysplash.getInstance().getTopActivity();
        if (activity != null && activity instanceof PhotoActivity) {
            setProgressState();
            ((PhotoActivity) activity).initRefresh();
        }
    }
}
