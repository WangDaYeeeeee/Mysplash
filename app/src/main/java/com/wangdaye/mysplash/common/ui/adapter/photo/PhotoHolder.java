package com.wangdaye.mysplash.common.ui.adapter.photo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class PhotoHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_photo) CardView card;
    @BindView(R.id.item_photo_image) CoverImageView image;

    @BindView(R.id.item_photo_avatar) CircleImageView avatar;
    @BindView(R.id.item_photo_title) TextView title;

    @BindView(R.id.item_photo_deleteButton) AppCompatImageButton deleteButton;

    @BindView(R.id.item_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R.id.item_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    @Nullable private PhotoAdapter.ItemEventCallback callback;

    PhotoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(Photo photo, boolean showDeleteButton, int columnCount, boolean update,
                    @Nullable PhotoAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        this.photo = photo;
        this.callback = callback;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        if (columnCount > 1) {
            int margin = context.getResources().getDimensionPixelSize(R.dimen.normal_margin);
            params.setMargins(0, 0, margin, margin);
            card.setLayoutParams(params);
            card.setRadius(context.getResources().getDimensionPixelSize(R.dimen.material_card_radius));
        } else {
            params.setMargins(0, 0, 0, 0);
            card.setLayoutParams(params);
            card.setRadius(0);
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

        if (showDeleteButton) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
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
                            ? R.drawable.ic_item_heart_red : R.drawable.ic_item_heart_outline);
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

    @OnClick(R.id.item_photo) void clickItem() {
        if (callback != null) {
            callback.onStartPhotoActivity(image, card, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_deleteButton) void deletePhoto() {
        if (callback != null) {
            callback.onDeleteButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_avatar) void checkAuthor() {
        if (callback != null) {
            callback.onStartUserActivity(avatar, card, photo.user, UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_photo_likeButton) void likePhoto() {
        if (likeButton.isUsable() && callback != null) {
            callback.onLikeButtonClicked(photo, getAdapterPosition(), !photo.liked_by_user);
        }
    }

    @OnClick(R.id.item_photo_collectionButton) void collectPhoto() {
        if (callback != null) {
            callback.onCollectButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_downloadButton) void downloadPhoto() {
        if (callback != null) {
            callback.onDownloadButtonClicked(photo, getAdapterPosition());
        }
    }
}
