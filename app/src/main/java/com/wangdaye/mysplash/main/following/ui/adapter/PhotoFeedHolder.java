package com.wangdaye.mysplash.main.following.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.ui.widget.CoverImageView;
import com.wangdaye.mysplash.user.ui.UserActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Photo holder.
 *
 * CollectionHolder class for {@link FollowingAdapter} to show photo data.
 *
 * */
public class PhotoFeedHolder extends FollowingHolder {

    @BindView(R.id.item_following_photo_card) CardView card;
    @BindView(R.id.item_following_photo_image) CoverImageView image;

    @BindView(R.id.item_following_photo_avatar) CircleImageView avatar;
    @BindView(R.id.item_following_photo_title) TextView title;

    @BindView(R.id.item_following_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R.id.item_following_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    private int photoPosition;
    private int columnCount;

    @Nullable private FollowingAdapter.ItemEventCallback callback;

    public static class Factory implements FollowingHolder.Factory {

        private int columnCount;
        private int viewType;
        private FollowingAdapter.ItemEventCallback callback;

        public Factory(int columnCount, int viewType, FollowingAdapter.ItemEventCallback callback) {
            this.columnCount = columnCount;
            this.viewType = viewType;
            this.callback = callback;
        }

        @NonNull
        @Override
        public FollowingHolder createHolder(@NonNull ViewGroup parent) {
            return new PhotoFeedHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_photo, parent, false),
                    columnCount,
                    callback);
        }

        @Override
        public boolean isMatch(Object data) {
            return data instanceof Photo;
        }

        @Override
        public int getType() {
            return viewType;
        }
    }

    private PhotoFeedHolder(View itemView, int columnCount,
                            @Nullable FollowingAdapter.ItemEventCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.columnCount = columnCount;
        this.callback = callback;
    }

    @Override
    void onBindView(FollowingAdapter.ItemData data, boolean update) {
        if (!(data.data instanceof Photo)) {
            return;
        }

        Context context = itemView.getContext();

        this.photo = (Photo) data.data;
        this.photoPosition = data.photoPosition;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        int margin = context.getResources().getDimensionPixelSize(R.dimen.normal_margin);
        if (columnCount > 1) {
            params.setMarginStart(0);
            params.setMarginEnd(margin);
            params.setMargins(0, 0, margin, margin);
            card.setLayoutParams(params);
        } else {
            params.setMargins(
                    context.getResources().getDimensionPixelSize(R.dimen.large_icon_size), 0, margin, margin);
            card.setLayoutParams(params);
        }

        image.setSize(photo.width, photo.height);

        if (!update) {
            ImageHelper.loadAvatar(avatar.getContext(), avatar, photo.user, null);

            title.setText("");
            image.setShowShadow(false);

            ImageHelper.loadRegularPhoto(image.getContext(), image, photo, () -> {
                photo.loadPhotoSuccess = true;
                if (!photo.hasFadedIn) {
                    photo.hasFadedIn = true;
                    ImageHelper.startSaturationAnimation(image.getContext(), image);
                }
                title.setText(photo.user.name);
                image.setShowShadow(true);
            });
        }

        if (photo.current_user_collections != null && photo.current_user_collections.size() != 0) {
            collectionButton.setImageResource(R.drawable.ic_item_collected);
        } else {
            collectionButton.setImageResource(R.drawable.ic_item_collect);
        }

        likeButton.setProgressColor(Color.WHITE);
        if (photo.settingLike) {
            likeButton.setProgressState();
        } else {
            likeButton.setResultState(
                    photo.liked_by_user ? R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
        }

        card.setCardBackgroundColor(
                ImageHelper.computeCardBackgroundColor(card.getContext(), photo.color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-cover");
            card.setTransitionName(photo.id + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        likeButton.recycleImageView();
    }

    // interface.

    @OnClick(R.id.item_following_photo_card) void clickItem() {
        if (callback != null) {
            callback.onStartPhotoActivity(image, card, getAdapterPosition(), photoPosition);
        }
    }

    @OnClick(R.id.item_following_photo_avatar) void checkAuthor() {
        if (callback != null) {
            callback.onStartUserActivity(avatar, card, photo.user, UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_following_photo_likeButton) void likePhoto() {
        if (likeButton.isUsable() && callback != null) {
            callback.onLikeButtonClicked(photo, getAdapterPosition(), !photo.liked_by_user);
        }
    }

    @OnClick(R.id.item_following_photo_collectionButton) void collectPhoto() {
        if (callback != null) {
            callback.onCollectButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_following_photo_downloadButton) void downloadPhoto() {
        if (callback != null) {
            callback.onDownloadButtonClicked(photo, getAdapterPosition());
        }
    }
}
