package com.wangdaye.common.ui.adapter.photo;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.presenter.LikePhotoPresenter;
import com.wangdaye.component.ComponentFactory;

class PhotoModel implements BaseAdapter.ViewModel {

    @NonNull String photoUrl;
    @Nullable String thumbUrl;
    @Size int[] photoSize;
    @ColorInt int photoColor;

    @Nullable String authorAvatarUrl;
    @Size int[] authorAvatarSize;

    String authorName;

    Photo photo;
    boolean downloading;
    boolean collected;
    boolean likeProgressing;
    boolean liked;
    boolean hasFadeIn;

    PhotoModel(Context context, Photo photo) {
        photoUrl = photo.getRegularUrl(context);
        thumbUrl = photo.urls.thumb;
        photoSize = photo.getRegularSize();
        photoColor = ImageHelper.computeCardBackgroundColor(context, photo.color);

        if (TextUtils.isEmpty(photo.user.profile_image.large)) {
            authorAvatarUrl = null;
        } else {
            authorAvatarUrl = photo.user.profile_image.large;
        }
        authorAvatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        authorName = photo.user.name;

        this.photo = photo;
        this.downloading = ComponentFactory.getDownloaderService().isDownloading(context, photo.id);
        this.collected = photo.current_user_collections != null && photo.current_user_collections.size() != 0;
        this.likeProgressing = LikePhotoPresenter.getInstance().isInProgress(photo);
        this.liked = photo.liked_by_user;
        this.hasFadeIn = false;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof PhotoModel && ((PhotoModel) newModel).photo.id.equals(photo.id);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        ((PhotoModel) newModel).hasFadeIn = hasFadeIn;

        return ImageHelper.isSameUrl(((PhotoModel) newModel).photoUrl, photoUrl)
                && ImageHelper.isSameUrl(((PhotoModel) newModel).authorAvatarUrl, authorAvatarUrl)
                && ((PhotoModel) newModel).authorName.equals(authorName)
                && ((PhotoModel) newModel).downloading == downloading
                && ((PhotoModel) newModel).collected == collected
                && ((PhotoModel) newModel).likeProgressing == likeProgressing
                && ((PhotoModel) newModel).liked == liked;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (ImageHelper.isSameUrl(((PhotoModel) newModel).photoUrl, photoUrl)
                && ImageHelper.isSameUrl(((PhotoModel) newModel).authorAvatarUrl, authorAvatarUrl)) {
            return 1;
        }
        return null;
    }
}
