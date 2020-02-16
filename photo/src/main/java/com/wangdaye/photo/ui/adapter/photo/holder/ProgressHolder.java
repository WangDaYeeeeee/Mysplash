package com.wangdaye.photo.ui.adapter.photo.holder;

import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.photo.ui.adapter.photo.model.ProgressModel;

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

    @BindView(R2.id.item_photo_3_progress_progressView) CircularProgressView progress;
    @BindView(R2.id.item_photo_3_progress_button) Button button;

    @OnClick(R2.id.item_photo_3_progress_button) void retryRefresh() {
        MysplashActivity activity =  MysplashApplication.getInstance().getTopActivity();
        if (activity instanceof PhotoActivity) {
            ((PhotoActivity) activity).initRefresh();
        }
    }

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new ProgressHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof ProgressModel;
        }
    }

    public ProgressHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_progress);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        ProgressModel model = (ProgressModel) viewModel;
        if (model.failed) {
            AnimUtils.animShow(button, 150, button.getAlpha(), 1f);
            AnimUtils.animHide(progress, 150, progress.getAlpha(), 0f, false);
        } else {
            AnimUtils.animShow(progress, 150, progress.getAlpha(), 1f);
            AnimUtils.animHide(button, 150, button.getAlpha(), 0f, true);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
