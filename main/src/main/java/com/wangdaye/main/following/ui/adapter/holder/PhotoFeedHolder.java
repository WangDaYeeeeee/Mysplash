package com.wangdaye.main.following.ui.adapter.holder;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.following.ui.adapter.FollowingAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

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

    @BindView(R2.id.item_following_photo_card) LongPressDragCardView card;
    @BindView(R2.id.item_following_photo_image) CoverImageView image;

    @BindView(R2.id.item_following_photo_avatar) CircularImageView avatar;
    @BindView(R2.id.item_following_photo_title) TextView title;

    @BindView(R2.id.item_following_photo_downloadButton) CircularProgressIcon downloadButton;
    @BindView(R2.id.item_following_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R2.id.item_following_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    private int photoPosition;

    @Nullable private FollowingAdapter.ItemEventCallback callback;

    public static class Factory implements FollowingHolder.Factory {

        private int viewType;
        private FollowingAdapter.ItemEventCallback callback;

        public Factory(int viewType, FollowingAdapter.ItemEventCallback callback) {
            this.viewType = viewType;
            this.callback = callback;
        }

        @NonNull
        @Override
        public FollowingHolder createHolder(@NonNull ViewGroup parent) {
            return new PhotoFeedHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_following_photo, parent, false),
                    callback
            );
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

    private PhotoFeedHolder(View itemView, @Nullable FollowingAdapter.ItemEventCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.callback = callback;
    }

    @Override
    public void onBindView(FollowingAdapter.ItemData data, boolean update) {
        if (!(data.data instanceof Photo)) {
            return;
        }

        Context context = itemView.getContext();

        this.photo = (Photo) data.data;
        this.photoPosition = data.photoPosition;

        card.setCoverImage(image);
        card.setRadius(context.getResources().getDimensionPixelSize(R.dimen.material_card_radius));
        card.setLongPressDragChildList(
                Arrays.asList(avatar, downloadButton, collectionButton, likeButton)
        );

        image.setSize(photo.width, photo.height);

        if (!update) {
            ImageHelper.loadAvatar(avatar.getContext(), avatar, photo.user, null);

            title.setText("");
            image.setShowShadow(false);

            ImageHelper.loadRegularPhoto(image.getContext(), image, photo, () -> {
                photo.loadPhotoSuccess = true;
                if (!photo.hasFadedIn) {
                    photo.hasFadedIn = true;
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(image.getContext(), image, duration);
                }
                title.setText(photo.user.name);
                image.setShowShadow(true);
            });
        }

        downloadButton.setProgressColor(Color.WHITE);
        if (ComponentFactory.getDownloaderService().isDownloading(context, photo.id)) {
            downloadButton.setProgressState();
        } else {
            downloadButton.setResultState(R.drawable.ic_download_white);
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
                    photo.liked_by_user
                            ? R.drawable.ic_heart_red
                            : R.drawable.ic_heart_outline_white
            );
        }

        card.setCardBackgroundColor(
                ImageHelper.computeCardBackgroundColor(card.getContext(), photo.color)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-cover");
            card.setTransitionName(photo.id + "-background");
        }
    }

    @Override
    public void onRecycled() {
        ImageHelper.releaseImageView(image);
    }

    // interface.

    @OnClick(R2.id.item_following_photo_card) void clickItem() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartPhotoActivity(image, card, getAdapterPosition(), photoPosition);
        }
    }

    @OnClick(R2.id.item_following_photo_avatar) void checkAuthor() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartUserActivity(avatar, card, photo.user, ProfilePager.PAGE_PHOTO);
        }
    }

    @OnClick(R2.id.item_following_photo_likeButton) void likePhoto() {
        if (likeButton.isUsable()
                && callback != null
                && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onLikeButtonClicked(photo, getAdapterPosition(), !photo.liked_by_user);
        }
    }

    @OnClick(R2.id.item_following_photo_collectionButton) void collectPhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onCollectButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R2.id.item_following_photo_downloadButton) void downloadPhoto() {
        if (downloadButton.isUsable()
                && callback != null
                && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDownloadButtonClicked(photo, getAdapterPosition());
        }
    }
}
