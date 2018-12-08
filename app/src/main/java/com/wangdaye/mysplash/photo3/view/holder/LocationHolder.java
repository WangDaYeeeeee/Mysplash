package com.wangdaye.mysplash.photo3.view.holder;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo3.view.activity.PhotoActivity3;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Location holder.
 * */

public class LocationHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R.id.item_photo_3_location_container)
    FrameLayout container;

    @BindView(R.id.item_photo_3_location_title)
    TextView title;

    public static final int TYPE_LOCATION = 3;

    public LocationHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity3 a, Photo photo) {
        if (!TextUtils.isEmpty(photo.location.title)) {
            title.setText(photo.location.title);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    // interface.

    @OnClick(R.id.item_photo_3_location_container) void click() {
        if (!TextUtils.isEmpty(title.getText().toString())) {
            IntentHelper.startSearchActivity(
                    Mysplash.getInstance().getTopActivity(),
                    title.getText().toString());
        }
    }
}
