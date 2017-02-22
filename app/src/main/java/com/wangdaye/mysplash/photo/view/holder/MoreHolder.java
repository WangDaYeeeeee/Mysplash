package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;

/**
 * More holder.
 * */

public class MoreHolder extends PhotoInfoAdapter.ViewHolder 
        implements View.OnClickListener {
    // widget
    private ImageView imageView;
    
    // data
    private Photo photo;

    public static final int TYPE_MORE = 7;

    /** <br> life cycle. */

    public MoreHolder(View itemView) {
        super(itemView);
        
        itemView.findViewById(R.id.item_photo_more).setOnClickListener(this);
        
        this.imageView = (ImageView) itemView.findViewById(R.id.item_photo_more_image);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        imageView.setTranslationY((float) (new DisplayUtils(a).dpToPx(72) * (-0.5)));
        if (photo.related_photos != null && photo.related_photos.results.size() != 0) {
            Glide.with(a)
                    .load(photo.related_photos.results.get(0).urls.regular)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } else if (photo.related_collections != null && photo.related_collections.results.size() != 0) {
            if (photo.related_collections.results.get(0).cover_photo != null) {
                Glide.with(a)
                        .load(photo.related_collections.results.get(0).cover_photo.urls.regular)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            } else {
                Glide.with(a)
                        .load(R.color.colorPrimary_dark)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            }
        } else {
            Glide.with(a)
                    .load(R.color.colorPrimary_dark)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        }
        this.photo = photo;
    }

    public ImageView getImageView() {
        return imageView;
    }

    /** <br> interface. */
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_photo_more:
                IntentHelper.startRelativeActivity(
                        Mysplash.getInstance().getTopActivity(),
                        photo);
                break;
        }
    }
}
