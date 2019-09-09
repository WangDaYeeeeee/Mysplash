package com.wangdaye.photo.ui.holder;

import android.view.View;
import android.widget.LinearLayout;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.common.ui.widget.NumberAnimTextView;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.photo.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Info holder.
 * */

public class InfoHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_info_container) LinearLayout container;
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
    private boolean enableAnim;

    public static final int TYPE_INFO = 4;

    public InfoHolder(PhotoActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        views.setDuration(1000);
        downloads.setDuration(1000);
        likes.setDuration(1000);

        views.getPaint().setFakeBoldText(true);
        downloads.getPaint().setFakeBoldText(true);
        likes.getPaint().setFakeBoldText(true);

        this.a = a;
        this.enableAnim = false;
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        views.setEnableAnim(enableAnim);
        downloads.setEnableAnim(enableAnim);
        likes.setEnableAnim(enableAnim);

        views.setNumberString(String.valueOf(photo.views));
        downloads.setNumberString(String.valueOf(photo.downloads));
        likes.setNumberString(String.valueOf(photo.likes));
    }

    public void setEnableAnim(boolean enable) {
        enableAnim = enable;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
