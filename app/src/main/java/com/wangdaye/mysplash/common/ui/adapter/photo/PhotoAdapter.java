package com.wangdaye.mysplash.common.ui.adapter.photo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.MultiColumnAdapter;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import java.util.List;

/**
 * Photo adapter.
 *
 * Adapter for {@link RecyclerView} to show photos.
 *
 * */

public class PhotoAdapter extends MultiColumnAdapter<RecyclerView.ViewHolder> {

    private List<Photo> itemList;

    private boolean showDeleteButton;

    @Nullable private ItemEventCallback callback;

    public PhotoAdapter(Context context, List<Photo> list) {
        super(context);
        this.itemList = list;
        this.showDeleteButton = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return FooterHolder.buildInstance(parent);
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
                    getColumnCount(), getGridMarginPixel(), getSingleColumnMarginPixel(),
                    false, callback
            );
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
                    getColumnCount(), getGridMarginPixel(), getSingleColumnMarginPixel(),
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

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
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

