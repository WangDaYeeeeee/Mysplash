package com.wangdaye.main.ui.following.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.image.transformation.CircleTransformation;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;
import com.wangdaye.main.ui.following.adapter.model.FollowingModel;
import com.wangdaye.main.ui.following.adapter.model.TitleFeedModel;

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

        @Nullable FollowingAdapter.ItemEventCallback callback;

        public Factory(@Nullable FollowingAdapter.ItemEventCallback callback) {
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
        public boolean isMatch(FollowingModel model) {
            return model instanceof TitleFeedModel;
        }
    }

    private TitleFeedHolder(View itemView, @Nullable FollowingAdapter.ItemEventCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.callback = callback;
    }

    @Override
    public void onBindView(FollowingModel model, boolean update) {
        StaggeredGridLayoutManager.LayoutParams params
                = (StaggeredGridLayoutManager.LayoutParams) background.getLayoutParams();
        params.setFullSpan(true);
        background.setLayoutParams(params);

        Context context = itemView.getContext();

        user = ((TitleFeedModel) model).user;
        if (user == null) {
            return;
        }

        avatarVisibility = false;
        setAvatarVisibility(true);

        if (TextUtils.isEmpty(((TitleFeedModel) model).avatarUrl)) {
            ImageHelper.loadImage(context, avatar, R.drawable.default_avatar,
                    ((TitleFeedModel) model).avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        } else {
            ImageHelper.loadImage(context, avatar, ((TitleFeedModel) model).avatarUrl, R.drawable.default_avatar_round,
                    ((TitleFeedModel) model).avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        }

        actor.setText(((TitleFeedModel) model).title);

        if (!TextUtils.isEmpty(((TitleFeedModel) model).subtitle)) {
            verb.setVisibility(View.VISIBLE);
            verb.setText(((TitleFeedModel) model).subtitle);
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
