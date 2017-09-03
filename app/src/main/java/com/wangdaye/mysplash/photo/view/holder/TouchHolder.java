package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomTouchView;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Touch holder.
 * */

public class TouchHolder extends PhotoInfoAdapter.ViewHolder {

    @BindView(R.id.item_photo_touch)
    FreedomTouchView touchView;

    private PhotoActivity activity;

    public static final int TYPE_TOUCH = 1;

    public TouchHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        touchView.setSize(photo.width, photo.height);
        touchView.setShowShadow(false);
        this.activity = a;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    @OnClick(R.id.item_photo_touch)
    void clickTouchView() {
        IntentHelper.startPreviewActivity(
                Mysplash.getInstance().getTopActivity(), activity.getPhoto(), true);
    }
}
