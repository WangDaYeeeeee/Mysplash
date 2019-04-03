package com.wangdaye.mysplash.common.ui.adapter.collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.CoverImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class CollectionHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_collection) CardView card;
    @BindView(R.id.item_collection_cover) CoverImageView image;

    @BindView(R.id.item_collection_title) TextView title;
    @BindView(R.id.item_collection_subtitle) TextView subtitle;
    @BindView(R.id.item_collection_avatar) CircleImageView avatar;
    @BindView(R.id.item_collection_name) TextView name;

    CollectionHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(Collection collection, int columnCount,
                    @Nullable CollectionAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

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
                    ImageHelper.startSaturationAnimation(context, image);
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
