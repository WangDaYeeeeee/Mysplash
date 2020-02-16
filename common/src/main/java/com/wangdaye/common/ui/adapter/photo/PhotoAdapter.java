package com.wangdaye.common.ui.adapter.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;

import java.util.List;

/**
 * Photo adapter.
 *
 * Adapter for {@link RecyclerView} to show photos.
 *
 * */

public class PhotoAdapter extends BaseAdapter<Photo, PhotoModel, PhotoHolder> {

    private boolean showDeleteButton;
    @Nullable private ItemEventCallback callback;

    public PhotoAdapter(Context context, List<Photo> list) {
        this(context, list, false);
    }

    public PhotoAdapter(Context context, List<Photo> list, boolean showDeleteButton) {
        super(context, list);
        this.showDeleteButton = showDeleteButton;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_photo, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull PhotoHolder holder, PhotoModel model) {
        holder.onBindView(model, showDeleteButton, false, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull PhotoHolder holder, PhotoModel model,
                                    @NonNull List<Object> payloads) {
        holder.onBindView(model, showDeleteButton, !payloads.isEmpty(), callback);
    }

    @Override
    public void onViewRecycled(@NonNull PhotoHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected PhotoModel getViewModel(Photo model) {
        return new PhotoModel(getContext(), model);
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
        notifyDataSetChanged();
    }

    // interface.

    public interface ItemEventCallback {
        void onStartPhotoActivity(View image, View background, int adapterPosition);
        void onStartUserActivity(View avatar, View background, User user, int index);
        void onDeleteButtonClicked(Photo photo, int adapterPosition);
        void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike);
        void onCollectButtonClicked(Photo photo, int adapterPosition);
        void onDownloadButtonClicked(Photo photo, int adapterPosition);
    }

    public PhotoAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

