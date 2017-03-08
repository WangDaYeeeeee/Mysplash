package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomTouchView;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

/**
 * Touch holder.
 * */

public class TouchHolder extends PhotoInfoAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private FreedomTouchView touchView;

    // data
    private Photo photo;

    public static final int TYPE_TOUCH = 1;

    /** <br> life cycle. */

    public TouchHolder(View itemView) {
        super(itemView);

        this.touchView = (FreedomTouchView) itemView.findViewById(R.id.item_photo_touch);
        touchView.setOnClickListener(this);
        if (Mysplash.getInstance().getTopActivity() instanceof PhotoActivity) {
            Photo photo = ((PhotoActivity) Mysplash.getInstance().getTopActivity()).getPhoto();
            touchView.setSize(photo.width, photo.height);
        }
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        this.photo = photo;
        if (touchView.getSize()[0] != photo.width || touchView.getSize()[1] != photo.height) {
            touchView.setSize(photo.width, photo.height);
        }
    }

    /** <br> interface. */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_photo_touch:
                IntentHelper.startPreviewActivity(
                        Mysplash.getInstance().getTopActivity(), photo, true);
                break;
        }
    }
}
