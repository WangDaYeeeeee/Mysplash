package com.wangdaye.main.ui.following.adapter.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;

public class TitleFeedModel extends FollowingModel {

    public @Nullable String avatarUrl;
    public @Size int[] avatarSize;

    public String title;
    public @Nullable String subtitle;

    public User user;

    public TitleFeedModel(Photo photo, int adapterPosition, int photoPosition) {
        super(photo, adapterPosition, photoPosition);

        if (TextUtils.isEmpty(photo.user.profile_image.large)) {
            avatarUrl = null;
        } else {
            avatarUrl = photo.user.profile_image.large;
        }
        avatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        title = photo.user.name;
        subtitle = photo.user.location;

        this.user = photo.user;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof TitleFeedModel && ((TitleFeedModel) newModel).user.id.equals(user.id);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof TitleFeedModel
                && ImageHelper.isSameUrl(((TitleFeedModel) newModel).avatarUrl, avatarUrl)
                && ((TitleFeedModel) newModel).title.equals(title)
                && ImageHelper.isSameUrl(((TitleFeedModel) newModel).subtitle, subtitle);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
