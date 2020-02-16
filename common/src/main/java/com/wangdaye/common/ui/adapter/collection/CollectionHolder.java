package com.wangdaye.common.ui.adapter.collection;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.image.transformation.CircleTransformation;
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

    void onBindView(CollectionModel model, @Nullable CollectionAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        card.setCoverImage(image);
        card.setLongPressDragChildList(Collections.singletonList(avatar));
        card.setCancelFlagMarginTop(cancelFlagMarginTop);
        card.setOnClickListener(v -> {
            if (callback != null) {
                callback.onCollectionClicked(avatar, card, model.collection);
            }
        });

        title.setText(model.title);
        subtitle.setText(model.subtitle);
        name.setText(model.authorName);

        if (TextUtils.isEmpty(model.coverUrl)) {
            image.setShowShadow(false);
        } else {
            image.setShowShadow(true);
            image.setSize(model.coverSize[0], model.coverSize[1]);

            ImageHelper.setImageViewSaturation(image, model.hasFadeIn ? 1 : 0);
            ImageHelper.loadImage(context, image, model.coverUrl, model.thumbUrl, model.coverSize, null, () -> {
                if (!model.hasFadeIn) {
                    model.hasFadeIn = true;
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(context, image, duration);
                }
            });
            card.setCardBackgroundColor(model.coverColor);
        }

        if (TextUtils.isEmpty(model.authorAvatarUrl)) {
            ImageHelper.loadImage(context, avatar, R.drawable.default_avatar,
                    model.authorAvatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        } else {
            ImageHelper.loadImage(context, avatar, model.authorAvatarUrl, R.drawable.default_avatar_round,
                    model.authorAvatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        }
        avatar.setOnClickListener(v -> {
            if (callback != null) {
                callback.onUserClicked(avatar, card, model.collection.user);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setTransitionName(model.collection.id + "-background");
            avatar.setTransitionName(model.collection.user.username + "-avatar");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        ImageHelper.releaseImageView(avatar);
    }
}
