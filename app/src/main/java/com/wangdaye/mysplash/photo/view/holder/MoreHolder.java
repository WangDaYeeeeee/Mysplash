package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;
import android.widget.ImageView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * More holder.
 *
 * This view holder is used to show related photos and collections information.
 *
 * */

public class MoreHolder extends PhotoInfoAdapter.ViewHolder {
    // widget
    @BindView(R.id.item_photo_more_image) ImageView imageView;
    private OnLoadImageCallback callback;
    
    // data
    private Photo photo;

    public static final int TYPE_MORE = 7;

    /** <br> life cycle. */

    public MoreHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        // do nothing.
    }

    public void loadMoreImage(final MysplashActivity a, final Photo photo, final boolean hasFadedIn,
                              OnLoadImageCallback c) {
        setOnLoadImageCallback(c);
        ImageHelper.OnLoadImageListener listener = new ImageHelper.OnLoadImageListener() {
            @Override
            public void onLoadSucceed() {
                if (!hasFadedIn) {
                    if (callback != null) {
                        callback.onLoadImageSucceed();
                    }
                    ImageHelper.startSaturationAnimation(a, imageView);
                }
            }

            @Override
            public void onLoadFailed() {
                // do nothing.
            }
        };

        imageView.setTranslationY((float) (new DisplayUtils(a).dpToPx(72) * (-0.5)));
        if (photo.related_photos != null && photo.related_photos.results.size() != 0) {
            ImageHelper.loadRegularPhoto(a, imageView, photo.related_photos.results.get(0), hasFadedIn ? null : listener);
        } else if (photo.related_collections != null && photo.related_collections.results.size() != 0) {
            ImageHelper.loadCollectionCover(a, imageView, photo.related_collections.results.get(0), hasFadedIn ? null : listener);
        } else {
            imageView.setImageResource(R.color.colorPrimary_dark);
        }
        this.photo = photo;
    }

    public ImageView getImageView() {
        return imageView;
    }

    /** <br> interface. */

    // on load image callback.

    public interface OnLoadImageCallback {
        void onLoadImageSucceed();
    }

    private void setOnLoadImageCallback(OnLoadImageCallback c) {
        this.callback = c;
    }

    // on click swipeListener.

    @OnClick(R.id.item_photo_more) void clickItem() {
        IntentHelper.startRelativeActivity(
                Mysplash.getInstance().getTopActivity(),
                photo);
    }
}
