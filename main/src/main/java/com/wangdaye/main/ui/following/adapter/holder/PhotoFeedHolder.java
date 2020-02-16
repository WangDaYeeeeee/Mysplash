package com.wangdaye.main.ui.following.adapter.holder;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.image.transformation.CircleTransformation;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;
import com.wangdaye.main.ui.following.adapter.model.FollowingModel;
import com.wangdaye.main.ui.following.adapter.model.PhotoFeedModel;

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

        private FollowingAdapter.ItemEventCallback callback;

        public Factory(FollowingAdapter.ItemEventCallback callback) {
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
        public boolean isMatch(FollowingModel model) {
            return model instanceof PhotoFeedModel;
        }
    }

    private PhotoFeedHolder(View itemView, @Nullable FollowingAdapter.ItemEventCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.callback = callback;
    }

    @Override
    public void onBindView(FollowingModel model, boolean update) {
        Context context = itemView.getContext();
        PhotoFeedModel photoFeedModel = (PhotoFeedModel) model;

        this.photo = model.photo;
        this.photoPosition = model.photoPosition;

        card.setCoverImage(image);
        card.setLongPressDragChildList(
                Arrays.asList(avatar, downloadButton, collectionButton, likeButton)
        );

        image.setSize(photoFeedModel.photoSize[0], photoFeedModel.photoSize[1]);
        image.setShowShadow(true);

        if (!update) {
            ImageHelper.setImageViewSaturation(image, photoFeedModel.hasFadeIn ? 1 : 0);
            ImageHelper.loadImage(context, image, photoFeedModel.photoUrl, photoFeedModel.thumbUrl, 
                    photoFeedModel.photoSize, null, () -> {
                if (!photoFeedModel.hasFadeIn) {
                    photoFeedModel.hasFadeIn = true;
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(context, image, duration);
                }
            });
            card.setCardBackgroundColor(photoFeedModel.photoColor);

            if (TextUtils.isEmpty(photoFeedModel.authorAvatarUrl)) {
                ImageHelper.loadImage(context, avatar, R.drawable.default_avatar,
                        photoFeedModel.authorAvatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
            } else {
                ImageHelper.loadImage(context, avatar, photoFeedModel.authorAvatarUrl, R.drawable.default_avatar_round,
                        photoFeedModel.authorAvatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
            }
        }

        title.setText(photoFeedModel.authorName);

        downloadButton.setProgressColor(Color.WHITE);
        if (photoFeedModel.downloading) {
            downloadButton.setProgressState();
        } else {
            downloadButton.setResultState(R.drawable.ic_download_white);
        }

        if (photoFeedModel.collected) {
            collectionButton.setImageResource(R.drawable.ic_item_collected);
        } else {
            collectionButton.setImageResource(R.drawable.ic_item_collect);
        }

        likeButton.setProgressColor(Color.WHITE);
        if (photoFeedModel.likeProgressing) {
            likeButton.setProgressState();
        } else {
            likeButton.setResultState(
                    photoFeedModel.liked ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_white);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-cover");
            card.setTransitionName(photo.id + "-background");
        }

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
