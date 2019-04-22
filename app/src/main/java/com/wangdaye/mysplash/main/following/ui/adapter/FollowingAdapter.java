package com.wangdaye.mysplash.main.following.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Following adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Photo}.
 *
 * */

public class FollowingAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Photo> photoList; // this list is used to save the feed data.
    private List<ItemData> itemList; // this list is used to save the display information of view holder.
    private List<ItemData> photoItemList; // this list will only restore item data of photos.

    private List<FollowingHolder.Factory> factoryList;
    private boolean hasFooter;

    /**
     * This class is used to save the view holder's information.
     * */
    class ItemData {

        int photoPosition;
        int adapterPosition;

        Object data;

        ItemData(int photoPosition, int adapterPosition, Object data) {
            this.photoPosition = photoPosition;
            this.adapterPosition = adapterPosition;
            this.data = data;
        }
    }

    public FollowingAdapter(Context context, List<Photo> list,
                            List<FollowingHolder.Factory> factoryList) {
        super(context);
        this.photoList = list;
        this.itemList = new ArrayList<>();
        this.photoItemList = new ArrayList<>();
        buildTypeList(0);

        this.factoryList = factoryList;

        this.hasFooter = hasFooter(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return FooterHolder.buildInstance(parent);
        }

        return factoryList.get(viewType).createHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!isFooter(position)) {
            ((FollowingHolder) holder).onBindView(itemList.get(position), false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty() || isFooter(position)) {
            onBindViewHolder(holder, position);
        } else {
            ((FollowingHolder) holder).onBindView(itemList.get(position), true);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) holder).onRecycled();
        } else if (holder instanceof PhotoFeedHolder) {
            ((PhotoFeedHolder) holder).onRecycled();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getRealItemCount() {
        return photoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) {
            return -1;
        }

        for (int i = 0; i < factoryList.size(); i ++) {
            if (factoryList.get(i).isMatch(itemList.get(position).data)) {
                return factoryList.get(i).getType();
            }
        }

        throw new RuntimeException("Invalid type of ViewHolder.");
    }

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
    }

    public int getTypeItemCount() {
        return itemList.size();
    }

    // control.

    public void buildTypeList(int photoListFromIndex) {
        for (int i = photoListFromIndex; i < photoList.size(); i ++) {
            if (itemList.size() == 0) {
                itemList.add(new ItemData(i, itemList.size(), photoList.get(i).user));
                itemList.add(new ItemData(i, itemList.size(), photoList.get(i)));
            } else {
                int lastTypeIndex = itemList.size() - 1;
                if (!(itemList.get(lastTypeIndex).data instanceof Photo)
                        || !((Photo) itemList.get(lastTypeIndex).data).user.username
                        .equals(photoList.get(i).user.username)) {
                    itemList.add(new ItemData(i, itemList.size(), photoList.get(i).user));
                    itemList.add(new ItemData(i, itemList.size(), photoList.get(i)));
                } else {
                    itemList.add(new ItemData(i, itemList.size(), photoList.get(i)));
                }
            }
            photoItemList.add(itemList.get(itemList.size() - 1));
        }
    }

    @Nullable
    public User getUser(int adapterPosition) {
        if (itemList.get(adapterPosition).data instanceof User) {
            return (User) itemList.get(adapterPosition).data;
        }
        return photoList.get(itemList.get(adapterPosition).photoPosition).user;
    }

    public int getPhotoHolderAdapterPosition(int photoPosition) {
        return photoItemList.get(photoPosition).adapterPosition;
    }

    public boolean isFooterView(int adapterPosition) {
        return itemList.size() > adapterPosition
                && (adapterPosition + 1 == itemList.size()
                || itemList.get(adapterPosition + 1).data instanceof User);
    }

    public void setTitleAvatarVisibility(RecyclerView.ViewHolder lastHolder,
                                         RecyclerView.ViewHolder newHolder) {
        if (lastHolder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) lastHolder).setAvatarVisibility(true);
        }
        if (newHolder instanceof TitleFeedHolder) {
            ((TitleFeedHolder) newHolder).setAvatarVisibility(false);
        }
    }

    public void updateItem(int position, Object payload) {
        ItemData item = itemList.get(position);
        item.data = photoList.get(item.photoPosition);

        photoItemList.get(item.photoPosition).data = photoList.get(item.photoPosition);

        notifyItemChanged(position, payload);
    }

    // interface.

    public interface ItemEventCallback {
        void onStartPhotoActivity(View image, View background, int adapterPosition, int photoPosition);
        void onStartUserActivity(View avatar, View background, User user, int index);
        void onVerbClicked(String verb, int adapterPosition);
        void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike);
        void onCollectButtonClicked(Photo photo, int adapterPosition);
        void onDownloadButtonClicked(Photo photo, int adapterPosition);
    }
}

