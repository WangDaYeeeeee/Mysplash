package com.wangdaye.mysplash.photo2.view.holder;

import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.ui.widget.NumberAnimTextView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** <br> Location holder. */

public class InfoHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_info_views)
    NumberAnimTextView views;

    @BindView(R.id.item_photo_2_info_downloads)
    NumberAnimTextView downloads;

    @BindView(R.id.item_photo_2_info_likes)
    NumberAnimTextView likes;

    private PhotoActivity2 a;
    private boolean init;

    public static final int TYPE_INFO = 4;

    public InfoHolder(View itemView, PhotoActivity2 a) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(a, views);
        DisplayUtils.setTypeface(a, downloads);
        DisplayUtils.setTypeface(a, likes);

        views.getPaint().setFakeBoldText(true);
        downloads.getPaint().setFakeBoldText(true);
        likes.getPaint().setFakeBoldText(true);

        this.a = a;
        this.init = false;
    }

    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        views.setEnableAnim(!init);
        downloads.setEnableAnim(!init);
        likes.setEnableAnim(!init);
        views.setNumberString(String.valueOf(photo.views));
        downloads.setNumberString(String.valueOf(photo.downloads));
        likes.setNumberString(String.valueOf(photo.likes));
        if (!init) {
            init = true;
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    // interface.

    @OnClick(R.id.item_photo_2_info_viewsContainer)
    void clickViews() {
        NotificationHelper.showSnackbar(
                a.getString(R.string.feedback_views) + " : " + views.getText());
    }

    @OnClick(R.id.item_photo_2_info_downloadsContainer)
    void clickDownloads() {
        NotificationHelper.showSnackbar(
                a.getString(R.string.feedback_downloads) + " : " + downloads.getText());
    }

    @OnClick(R.id.item_photo_2_info_likesContainer)
    void clickLikes() {
        NotificationHelper.showSnackbar(
                a.getString(R.string.feedback_likes) + " : " + likes.getText());
    }
}
