package com.wangdaye.mysplash.photo2.view.holder;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import butterknife.BindView;
import butterknife.ButterKnife;

/** <br> Location holder. */

public class LocationHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_location_title)
    TextView title;

    public static final int TYPE_LOCATION = 3;

    public LocationHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), title);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        if (!TextUtils.isEmpty(photo.location.title)) {
            title.setText(a.getString(R.string.from) + " " + photo.location.title);
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}
