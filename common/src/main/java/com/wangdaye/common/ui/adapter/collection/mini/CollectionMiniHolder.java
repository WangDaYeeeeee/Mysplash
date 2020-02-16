package com.wangdaye.common.ui.adapter.collection.mini;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularProgressIcon;

import butterknife.BindView;
import butterknife.ButterKnife;

class CollectionMiniHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_collection_mini_image) AppCompatImageView image;
    @BindView(R2.id.item_collection_mini_title) TextView title;
    @BindView(R2.id.item_collection_mini_subtitle) TextView subtitle;
    @BindView(R2.id.item_collection_mini_lockIcon) AppCompatImageView lockIcon;
    @BindView(R2.id.item_collection_mini_icon) CircularProgressIcon stateIcon;

    CollectionMiniHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(@NonNull CollectionMiniModel model, Photo photo, boolean update,
                    CollectionMiniAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        itemView.findViewById(R.id.item_collection_mini_card).setOnClickListener(v -> {
            if (callback == null || getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            if (model.header) {
                callback.onCreateCollection();
            } else if (stateIcon.isUsable()) {
                stateIcon.setProgressState();
                if (photo.current_user_collections != null) {
                    for (Collection c : photo.current_user_collections) {
                        if (model.collection.id == c.id) {
                            // delete photo.
                            callback.onAddPhotoToCollectionOrRemoveIt(
                                    model.collection,
                                    photo,
                                    getAdapterPosition(),
                                    false
                            );
                            return;
                        }
                    }
                }
                callback.onAddPhotoToCollectionOrRemoveIt(
                        model.collection,
                        photo,
                        getAdapterPosition(),
                        true
                );
            }
        });

        if (model.header) {
            ImageHelper.loadImage(context, image, R.drawable.default_collection_creator);
            title.setText(context.getString(R.string.feedback_create_collection).toUpperCase());
            subtitle.setVisibility(View.GONE);
            lockIcon.setVisibility(View.GONE);
            stateIcon.setResultState(android.R.color.transparent);
            return;
        }

        subtitle.setVisibility(View.VISIBLE);
        lockIcon.setVisibility(View.VISIBLE);

        title.setText(model.title);
        subtitle.setText(model.subtitle);

        if (!update) {
            if (model.coverUrl != null) {
                ImageHelper.loadImage(context, image, model.coverUrl, null, model.coverSize, null, null);
            } else {
                ImageHelper.loadImage(context, image, R.drawable.default_collection_cover);
            }
        }

        if (model.privateCollection) {
            lockIcon.setAlpha(1f);
        } else {
            lockIcon.setAlpha(0f);
        }

        stateIcon.setProgressColor(Color.WHITE);
        if (model.progressing) {
            stateIcon.setProgressState();
        } else {
            stateIcon.setResultState(model.collected ? R.drawable.ic_state_succeed : android.R.color.transparent);
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }
}
