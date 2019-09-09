package com.wangdaye.main.following.ui.adapter.holder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.main.following.ui.adapter.FollowingAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Title holder.
 *
 * CollectionHolder class for {@link FollowingAdapter} to show the title part of following feed data.
 *
 * */
public class TitleFeedHolder extends FollowingHolder {

    @BindView(R2.id.item_following_title_background) RelativeLayout background;

    @BindView(R2.id.item_following_title_avatar) CircularImageView avatar;
    @BindView(R2.id.item_following_title_actor) TextView actor;
    @OnClick({
            R2.id.item_following_title_avatar,
            R2.id.item_following_title_actor
    }) void checkActor() {
        if (callback != null) {
            callback.onStartUserActivity(avatar, background, user, ProfilePager.PAGE_PHOTO);
        }
    }

    @BindView(R2.id.item_following_title_verb) TextView verb;
    @OnClick(R2.id.item_following_title_verb) void clickVerb() {
        if (user != null
                && callback != null
                && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onVerbClicked(user.location, getAdapterPosition());
        }
    }

    @Nullable private User user;
    private boolean avatarVisibility;

    @Nullable private FollowingAdapter.ItemEventCallback callback;

    public static class Factory implements FollowingHolder.Factory {

        private int viewType;
        @Nullable FollowingAdapter.ItemEventCallback callback;

        public Factory(int viewType, @Nullable FollowingAdapter.ItemEventCallback callback) {
            this.viewType = viewType;
            this.callback = callback;
        }

        @NonNull
        @Override
        public FollowingHolder createHolder(@NonNull ViewGroup parent) {
            return new TitleFeedHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_title, parent, false),
                    callback
            );
        }

        @Override
        public boolean isMatch(Object data) {
            return data instanceof User;
        }

        @Override
        public int getType() {
            return viewType;
        }
    }

    private TitleFeedHolder(View itemView, @Nullable FollowingAdapter.ItemEventCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.callback = callback;
    }

    @Override
    public void onBindView(FollowingAdapter.ItemData data, boolean update) {
        if (!(data.data instanceof User)) {
            return;
        }

        StaggeredGridLayoutManager.LayoutParams params
                = (StaggeredGridLayoutManager.LayoutParams) background.getLayoutParams();
        params.setFullSpan(true);
        background.setLayoutParams(params);

        user = (User) data.data;
        if (user == null) {
            return;
        }

        avatarVisibility = false;
        setAvatarVisibility(true);

        ImageHelper.loadAvatar(avatar.getContext(), avatar, user, null);
        actor.setText(user.name);

        if (!TextUtils.isEmpty(user.location)) {
            verb.setVisibility(View.VISIBLE);
            verb.setText(user.location);
        } else {
            verb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    public void setAvatarVisibility(boolean visibility) {
        if (visibility != avatarVisibility) {
            avatarVisibility = visibility;
            avatar.setAlpha(visibility ? 1f : 0f);
            avatar.setEnabled(visibility);
        }
    }
}
