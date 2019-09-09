package com.wangdaye.common.ui.adapter.photo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.component.ComponentFactory;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class PhotoHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_photo) LongPressDragCardView card;
    @BindView(R2.id.item_photo_image) CoverImageView image;

    @BindView(R2.id.item_photo_avatar) CircularImageView avatar;
    @BindView(R2.id.item_photo_title) TextView title;

    @BindView(R2.id.item_photo_deleteButton) AppCompatImageButton deleteButton;

    @BindView(R2.id.item_photo_downloadButton) CircularProgressIcon downloadButton;
    @BindView(R2.id.item_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R2.id.item_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    @Nullable private PhotoAdapter.ItemEventCallback callback;

    PhotoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(Photo photo, boolean showDeleteButton,
                    boolean update, @Nullable PhotoAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        this.photo = photo;
        this.callback = callback;

        card.setCoverImage(image);
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

        if (showDeleteButton) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        downloadButton.setProgressColor(Color.WHITE);
        if (callback != null && callback.isDownloading(context, photo)) {
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
                ImageHelper.computeCardBackgroundColor(card.getContext(), photo.color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setTransitionName(photo.id + "-cover");
            card.setTransitionName(photo.id + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }

    // interface.

    @OnClick(R2.id.item_photo) void clickItem() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartPhotoActivity(image, card, getAdapterPosition());
        }
    }

    @OnClick(R2.id.item_photo_deleteButton) void deletePhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDeleteButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R2.id.item_photo_avatar) void checkAuthor() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartUserActivity(avatar, card, photo.user, ProfilePager.PAGE_PHOTO);
        }
    }

    @OnClick(R2.id.item_photo_likeButton) void likePhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onLikeButtonClicked(photo, getAdapterPosition(), !photo.liked_by_user);
        }
    }

    @OnClick(R2.id.item_photo_collectionButton) void collectPhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onCollectButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R2.id.item_photo_downloadButton) void downloadPhoto() {
        if (downloadButton.isUsable()
                && callback != null
                && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDownloadButtonClicked(photo, getAdapterPosition());
        }
    }
}
