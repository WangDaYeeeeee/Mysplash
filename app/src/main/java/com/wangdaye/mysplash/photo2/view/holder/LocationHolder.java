package com.wangdaye.mysplash.photo2.view.holder;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Location holder.
 * */

public class LocationHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_location_container)
    FrameLayout container;

    @BindView(R.id.item_photo_2_location_title)
    TextView title;

    public static final int TYPE_LOCATION = 3;

    public LocationHolder(View itemView, int marginHorizontal, int columnCount) {
        super(itemView, marginHorizontal, columnCount);
        ButterKnife.bind(this, itemView);

        if (marginHorizontal > 0 && columnCount == PhotoInfoAdapter2.COLUMN_COUNT_HORIZONTAL) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
            params.setMarginStart(marginHorizontal);
            params.setMarginEnd(marginHorizontal);
            container.setLayoutParams(params);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        if (!TextUtils.isEmpty(photo.location.title)) {
            title.setText(photo.location.title);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    // interface.

    @OnClick(R.id.item_photo_2_location_container) void click() {
        if (!TextUtils.isEmpty(title.getText().toString())) {
            IntentHelper.startSearchActivity(
                    Mysplash.getInstance().getTopActivity(),
                    title.getText().toString());
        }
    }
}
