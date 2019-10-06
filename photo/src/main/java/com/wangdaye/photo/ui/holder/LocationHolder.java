package com.wangdaye.photo.ui.holder;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.photo.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Location holder.
 * */

public class LocationHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_location_container) FrameLayout container;
    @OnClick(R2.id.item_photo_3_location_container) void click() {
        if (!TextUtils.isEmpty(title.getText().toString())) {
            ComponentFactory.getSearchModule().startSearchActivity(
                    MysplashApplication.getInstance().getTopActivity(),
                    title.getText().toString());
        }
    }
    @BindView(R2.id.item_photo_3_location_title) TextView title;

    public static final int TYPE_LOCATION = 3;

    public LocationHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        if (!TextUtils.isEmpty(photo.location.title)) {
            title.setText(photo.location.title);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
