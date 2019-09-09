package com.wangdaye.common.ui.adapter.collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.component.ComponentFactory;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

class CollectionHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_collection) LongPressDragCardView card;
    @BindView(R2.id.item_collection_cover) CoverImageView image;

    @BindView(R2.id.item_collection_title) TextView title;
    @BindView(R2.id.item_collection_subtitle) TextView subtitle;
    @BindView(R2.id.item_collection_avatar) CircularImageView avatar;
    @BindView(R2.id.item_collection_name) TextView name;

    private int cancelFlagMarginTop;

    CollectionHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        cancelFlagMarginTop = (int) new DisplayUtils(itemView.getContext()).dpToPx(98);
    }

    void onBindView(Collection collection,
                    @Nullable CollectionAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        card.setCoverImage(image);
        card.setLongPressDragChildList(Collections.singletonList(avatar));
        card.setCancelFlagMarginTop(cancelFlagMarginTop);
        card.setOnClickListener(v -> {
            if (callback != null) {
                callback.onCollectionClicked(avatar, card, collection);
            }
        });

        if (collection.cover_photo != null
                && collection.cover_photo.width != 0
                && collection.cover_photo.height != 0) {
            image.setSize(
                    collection.cover_photo.width,
                    collection.cover_photo.height);
        }

        if (collection.cover_photo != null) {
            setCardText(context, collection, true);
            ImageHelper.loadCollectionCover(image.getContext(), image, collection, () -> {
                collection.cover_photo.loadPhotoSuccess = true;
                if (!collection.cover_photo.hasFadedIn) {
                    collection.cover_photo.hasFadedIn = true;
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(context, image, duration);
                }
                setCardText(context, collection, false);
            });
            card.setCardBackgroundColor(
                    ImageHelper.computeCardBackgroundColor(
                            image.getContext(),
                            collection.cover_photo.color));
        } else {
            setCardText(context, collection, false);
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
        }

        ImageHelper.loadAvatar(avatar.getContext(), avatar, collection.user, null);
        avatar.setOnClickListener(v -> {
            if (callback != null) {
                callback.onUserClicked(avatar, card, collection.user);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setTransitionName(collection.id + "-background");
            avatar.setTransitionName(collection.user.username + "-avatar");
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCardText(Context context, Collection collection, boolean setNull) {
        if (setNull) {
            title.setText("");
            subtitle.setText("");
            name.setText("");
            image.setShowShadow(false);
        } else {
            title.setText(collection.title.toUpperCase());
            subtitle.setText(collection.total_photos
                    + " " + context.getResources().getStringArray(R.array.user_tabs)[0]);
            name.setText(collection.user.name);
            if (collection.cover_photo == null) {
                image.setShowShadow(false);
            } else {
                image.setShowShadow(true);
            }
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        ImageHelper.releaseImageView(avatar);
    }
}
