package com.wangdaye.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.ComponentFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection mini adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection} in mini style.
 *
 * */

public class CollectionMiniAdapter extends RecyclerView.Adapter<CollectionMiniHolder> {

    private Photo photo;
    @Nullable private ItemEventCallback callback;

    public CollectionMiniAdapter(Photo p) {
        this.photo = p;
    }

    @NonNull
    @Override
    public CollectionMiniHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_mini, parent, false);
        return new CollectionMiniHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionMiniHolder holder, int position) {
        if (position == 0) {
            holder.onBindView(null, true, photo, callback);
        } else {
            holder.onBindView(
                    AuthManager.getInstance()
                            .getCollectionsManager()
                            .getCollectionList()
                            .get(position - 1),
                    false,
                    photo,
                    callback
            );
        }
    }

    @Override
    public void onViewRecycled(@NonNull CollectionMiniHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return AuthManager.getInstance()
                .getCollectionsManager()
                .getCollectionList()
                .size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setPhoto(Photo p) {
        this.photo = p;
    }

    public void updateItem(Collection collection, Photo photo) {
        this.photo = photo;

        List<Collection> list = AuthManager.getInstance()
                .getCollectionsManager()
                .getCollectionList();
        for (int i = 0; i < list.size(); i ++) {
            if (list.get(i).id == collection.id) {
                notifyItemChanged(i + 1);
                return;
            }
        }
    }

    public interface ItemEventCallback {
        void onCreateCollection();
        void onAddPhotoToCollectionOrRemoveIt(Collection collection, Photo photo,
                                              int adapterPosition, boolean add);
    }

    public CollectionMiniAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

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
    void onBindView(Collection collection, boolean header, Photo photo,
                    CollectionMiniAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        itemView.findViewById(R.id.item_collection_mini_card).setOnClickListener(v -> {
            if (callback == null || getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            if (header) {
                callback.onCreateCollection();
            } else if (stateIcon.isUsable()) {
                stateIcon.setProgressState();
                collection.editing = true;
                for (int i = 0;
                     photo.current_user_collections != null && i < photo.current_user_collections.size();
                     i ++) {
                    if (collection.id == photo.current_user_collections.get(i).id) {
                        // delete photo.
                        callback.onAddPhotoToCollectionOrRemoveIt(
                                collection,
                                photo,
                                getAdapterPosition(),
                                false
                        );
                        return;
                    }
                }
                callback.onAddPhotoToCollectionOrRemoveIt(
                        collection,
                        photo,
                        getAdapterPosition(),
                        true
                );
            }
        });

        if (header) {
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_creator);
            title.setText(context.getString(R.string.feedback_create_collection).toUpperCase());
            subtitle.setVisibility(View.GONE);
            lockIcon.setVisibility(View.GONE);
            stateIcon.setResultState(android.R.color.transparent);
            return;
        }

        subtitle.setVisibility(View.VISIBLE);
        lockIcon.setVisibility(View.VISIBLE);

        title.setText(collection.title.toUpperCase());
        subtitle.setText(
                collection.total_photos
                        + " " + context.getResources().getStringArray(R.array.user_tabs)[0]
        );

        if (collection.cover_photo != null) {
            ImageHelper.loadCollectionCover(image.getContext(), image, collection, true, null);
        } else {
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
        }

        if (collection.privateX) {
            lockIcon.setAlpha(1f);
        } else {
            lockIcon.setAlpha(0f);
        }

        stateIcon.setProgressColor(Color.WHITE);
        if (collection.editing) {
            stateIcon.setProgressState();
        } else {
            for (int i = 0;
                 photo.current_user_collections != null && i < photo.current_user_collections.size();
                 i ++) {
                if (collection.id == photo.current_user_collections.get(i).id) {
                    stateIcon.setResultState(R.drawable.ic_state_succeed);
                    return;
                }
            }
            stateIcon.setResultState(android.R.color.transparent);
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }
}

