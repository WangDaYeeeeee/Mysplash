package com.wangdaye.common.ui.adapter.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo adapter.
 *
 * Adapter for {@link RecyclerView} to show photos.
 *
 * */

public class PhotoAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Photo> itemList;
    private boolean showDeleteButton;

    @Nullable private ItemEventCallback callback;

    public PhotoAdapter() {
        this(new ArrayList<>());
    }

    public PhotoAdapter(List<Photo> list) {
        super();
        this.itemList = list;
        this.showDeleteButton = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return new FooterHolder(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_photo, parent, false);
            return new PhotoHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoHolder && position < getRealItemCount()) {
            ((PhotoHolder) holder).onBindView(
                    itemList.get(position), showDeleteButton,
                    false, callback
            );
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).onBindView();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ((PhotoHolder) holder).onBindView(
                    itemList.get(position), showDeleteButton,
                    true, callback
            );
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof PhotoHolder) {
            ((PhotoHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? -1 : 1;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
        notifyDataSetChanged();
    }

    public List<Photo> getItemList() {
        return itemList;
    }

    @Override
    public void updateListByDiffUtil(List newList) {
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new PhotoDiffCallback(itemList, newList), false);
        itemList.clear();
        itemList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    // interface.

    public interface ItemEventCallback {
        void onStartPhotoActivity(View image, View background, int adapterPosition);
        void onStartUserActivity(View avatar, View background, User user, int index);
        void onDeleteButtonClicked(Photo photo, int adapterPosition);
        void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike);
        void onCollectButtonClicked(Photo photo, int adapterPosition);
        void onDownloadButtonClicked(Photo photo, int adapterPosition);
        boolean isDownloading(Context context, Photo photo);
    }

    public PhotoAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

