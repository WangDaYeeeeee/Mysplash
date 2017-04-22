package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
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

    @BindView(R.id.item_photo_touch) FreedomTouchView touchView;

    private Photo photo;

    public static final int TYPE_TOUCH = 1;

    public TouchHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null && activity instanceof PhotoActivity) {
            Photo photo = ((PhotoActivity) activity).getPhoto();
            touchView.setSize(photo.width, photo.height);
        }
    }

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        this.photo = photo;
        if (touchView.getSize()[0] != photo.width || touchView.getSize()[1] != photo.height) {
            touchView.setSize(photo.width, photo.height);
        }
    }

    @OnClick(R.id.item_photo_touch) void clickItem() {
        IntentHelper.startPreviewActivity(
                Mysplash.getInstance().getTopActivity(), photo, true);
    }
}
