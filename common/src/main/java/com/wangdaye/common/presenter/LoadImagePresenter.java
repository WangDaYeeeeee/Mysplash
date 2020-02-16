package com.wangdaye.common.presenter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.R;
import com.wangdaye.common.image.transformation.CircleTransformation;
import com.wangdaye.common.image.ImageHelper;

public class LoadImagePresenter {

    public static void loadPhotoImage(Context context, ImageView view, @Nullable Photo photo,
                                      @Nullable ImageHelper.OnLoadImageListener l) {
        if (photo != null) {
            ImageHelper.loadImage(context, view, photo.getRegularUrl(), photo.urls.thumb,
                    photo.getRegularSize(), null, l);
        }
    }

    public static void loadFullSizePhotoImage(Context context, ImageView view, @Nullable Photo photo,
                                              @Nullable ImageHelper.OnLoadImageListener l) {
        if (photo != null) {
            ImageHelper.loadImage(context, view, photo.getFullUrl(), photo.getRegularUrl(),
                    photo.getFullSize(), null, l);
        }
    }

    public static void loadCollectionCover(Context context, ImageView view, @Nullable Collection collection,
                                           @Nullable ImageHelper.OnLoadImageListener l) {
        if (collection != null) {
            loadPhotoImage(context, view, collection.cover_photo, l);
        }
    }

    public static void loadUserAvatar(Context context, ImageView view, @Nullable User user,
                                      @Nullable ImageHelper.OnLoadImageListener l) {
        if (user == null || user.profile_image == null) {
            ImageHelper.loadImage(context, view, R.drawable.default_avatar,
                    new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE},
                    new BitmapTransformation[] {new CircleTransformation(context)}, l);
        } else {
            ImageHelper.loadImage(context, view, user.profile_image.large, R.drawable.default_avatar_round,
                    new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE},
                    new BitmapTransformation[] {new CircleTransformation(context)}, l);
        }
    }
}
