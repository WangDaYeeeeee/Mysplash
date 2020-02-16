package com.wangdaye.common.ui.adapter.collection;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;

class CollectionModel implements BaseAdapter.ViewModel {

    @Nullable String coverUrl;
    @Nullable String thumbUrl;
    @Size int[] coverSize;
    @ColorInt int coverColor;

    String title;
    String subtitle;

    @Nullable String authorAvatarUrl;
    @Size int[] authorAvatarSize;

    String authorName;

    Collection collection;
    boolean hasFadeIn;

    CollectionModel(Context context, Collection collection) {
        if (collection.cover_photo != null) {
            coverUrl = collection.cover_photo.getRegularUrl();
            thumbUrl = collection.cover_photo.urls.thumb;
            coverSize = collection.cover_photo.getRegularSize();
            coverColor = ImageHelper.computeCardBackgroundColor(context, collection.cover_photo.color);
        } else {
            coverUrl = null;
            thumbUrl = null;
            coverSize = new int[] {10, 6};
            coverColor = Color.TRANSPARENT;
        }

        title = collection.title.toUpperCase();
        subtitle = collection.total_photos + " " + context.getResources().getStringArray(R.array.user_tabs)[0];

        if (TextUtils.isEmpty(collection.user.profile_image.large)) {
            authorAvatarUrl = null;
        } else {
            authorAvatarUrl = collection.user.profile_image.large;
        }
        authorAvatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        authorName = collection.user.name;

        this.collection = collection;
        this.hasFadeIn = false;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof CollectionModel && ((CollectionModel) newModel).collection.id == collection.id;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        ((CollectionModel) newModel).hasFadeIn = hasFadeIn;

        return ImageHelper.isSameUrl(((CollectionModel) newModel).coverUrl, coverUrl)
                && ((CollectionModel) newModel).title.equals(title)
                && ((CollectionModel) newModel).subtitle.equals(subtitle)
                && ImageHelper.isSameUrl(((CollectionModel) newModel).authorAvatarUrl, authorAvatarUrl)
                && ((CollectionModel) newModel).authorName.equals(authorName);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (ImageHelper.isSameUrl(((CollectionModel) newModel).coverUrl, coverUrl)) {
            return 1;
        }
        return null;
    }
}
