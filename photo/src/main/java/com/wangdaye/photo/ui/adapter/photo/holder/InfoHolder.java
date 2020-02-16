package com.wangdaye.photo.ui.adapter.photo.holder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.ui.widget.NumberAnimTextView;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.model.InfoModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Info holder.
 * */

public class InfoHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_info_views) NumberAnimTextView views;
    @BindView(R2.id.item_photo_3_info_downloads) NumberAnimTextView downloads;
    @BindView(R2.id.item_photo_3_info_likes) NumberAnimTextView likes;

    @OnClick(R2.id.item_photo_3_info_viewsContainer)
    void clickViews() {
        NotificationHelper.showSnackbar(
                a,
                a.getString(R.string.feedback_views) + " : " + views.getText()
        );
    }

    @OnClick(R2.id.item_photo_3_info_downloadsContainer)
    void clickDownloads() {
        NotificationHelper.showSnackbar(
                a,
                a.getString(R.string.feedback_downloads) + " : " + downloads.getText()
        );
    }

    @OnClick(R2.id.item_photo_3_info_likesContainer)
    void clickLikes() {
        NotificationHelper.showSnackbar(
                a,
                a.getString(R.string.feedback_likes) + " : " + likes.getText()
        );
    }

    private PhotoActivity a;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new InfoHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof InfoModel;
        }
    }

    public InfoHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_info);
        ButterKnife.bind(this, itemView);

        views.getPaint().setFakeBoldText(true);
        downloads.getPaint().setFakeBoldText(true);
        likes.getPaint().setFakeBoldText(true);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        InfoModel model = (InfoModel) viewModel;

        this.a = a;

        views.setEnableAnim(model.enableAnim);
        views.setDuration(model.viewsAnimDuration);
        views.setNumberString(model.views);

        downloads.setEnableAnim(model.enableAnim);
        downloads.setDuration(model.downloadsAnimDuration);
        downloads.setNumberString(model.downloads);

        likes.setEnableAnim(model.enableAnim);
        likes.setDuration(model.likesAnimDuration);
        likes.setNumberString(model.likes);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
