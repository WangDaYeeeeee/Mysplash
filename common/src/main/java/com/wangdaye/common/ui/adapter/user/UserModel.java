package com.wangdaye.common.ui.adapter.user;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;

class UserModel implements BaseAdapter.ViewModel {

    @Nullable String avatarUrl;
    @Size int[] avatarSize;

    String title;
    String subtitle;
    @Nullable String portfolioUrl;

    User user;
    boolean hasFadeIn;

    UserModel(Context context, User user) {
        if (TextUtils.isEmpty(user.profile_image.large)) {
            avatarUrl = null;
        } else {
            avatarUrl = user.profile_image.large;
        }
        avatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        title = user.name;
        if (TextUtils.isEmpty(user.bio)) {
            subtitle = user.total_photos
                    + " " + context.getResources().getStringArray(R.array.user_tabs)[0] + ", "
                    + user.total_collections
                    + " " + context.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                    + user.total_likes
                    + " " + context.getResources().getStringArray(R.array.user_tabs)[2];
        } else {
            subtitle = user.bio;
        }

        this.user = user;
        this.hasFadeIn = false;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof UserModel && ((UserModel) newModel).user.id.equals(user.id);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        ((UserModel) newModel).hasFadeIn = hasFadeIn;

        return ImageHelper.isSameUrl(((UserModel) newModel).avatarUrl, avatarUrl)
                && ImageHelper.isSameUrl(((UserModel) newModel).portfolioUrl, portfolioUrl)
                && ((UserModel) newModel).title.equals(title)
                && ((UserModel) newModel).subtitle.equals(subtitle);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
