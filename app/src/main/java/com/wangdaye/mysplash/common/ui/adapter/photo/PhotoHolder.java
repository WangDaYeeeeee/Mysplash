package com.wangdaye.mysplash.common.ui.adapter.photo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.MultiColumnAdapter;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.ui.widget.CircularImageView;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.ui.widget.CoverImageView;
import com.wangdaye.mysplash.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.mysplash.user.ui.UserActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class PhotoHolder extends MultiColumnAdapter.ViewHolder {

    @BindView(R.id.item_photo) LongPressDragCardView card;
    @BindView(R.id.item_photo_image) CoverImageView image;

    @BindView(R.id.item_photo_avatar) CircularImageView avatar;
    @BindView(R.id.item_photo_title) TextView title;

    @BindView(R.id.item_photo_deleteButton) AppCompatImageButton deleteButton;

    @BindView(R.id.item_photo_downloadButton) CircularProgressIcon downloadButton;
    @BindView(R.id.item_photo_collectionButton) AppCompatImageButton collectionButton;
    @BindView(R.id.item_photo_likeButton) CircularProgressIcon likeButton;

    private Photo photo;
    @Nullable private PhotoAdapter.ItemEventCallback callback;

    PhotoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(View container, int columnCount,
                              int gridMarginPixel, int singleColumnMarginPixel) {
        setLayoutParamsForGridItemMargin(container, columnCount, gridMarginPixel, singleColumnMarginPixel);
    }

    void onBindView(Photo photo, boolean showDeleteButton,
                    int columnCount, int gridMarginPixel, int singleColumnMarginPixel,
                    boolean update, @Nullable PhotoAdapter.ItemEventCallback callback) {
        onBindView(card, columnCount, gridMarginPixel, singleColumnMarginPixel);

        Context context = itemView.getContext();

        this.photo = photo;
        this.callback = callback;

        if (columnCount > 1) {
            card.setRadius(context.getResources().getDimensionPixelSize(R.dimen.material_card_radius));
        } else {
            card.setRadius(0);
        }
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

        downloadButton.setProgressColor(Color.WHITE);
        if (DownloadHelper.getInstance(context).readDownloadingEntityCount(context, photo.id) > 0) {
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

    @OnClick(R.id.item_photo) void clickItem() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartPhotoActivity(image, card, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_deleteButton) void deletePhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDeleteButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_avatar) void checkAuthor() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onStartUserActivity(avatar, card, photo.user, UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_photo_likeButton) void likePhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onLikeButtonClicked(photo, getAdapterPosition(), !photo.liked_by_user);
        }
    }

    @OnClick(R.id.item_photo_collectionButton) void collectPhoto() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onCollectButtonClicked(photo, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_photo_downloadButton) void downloadPhoto() {
        if (downloadButton.isUsable()
                && callback != null
                && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDownloadButtonClicked(photo, getAdapterPosition());
        }
    }
}
