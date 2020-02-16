package com.wangdaye.main.ui.following.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.main.ui.following.adapter.holder.FollowingHolder;
import com.wangdaye.main.ui.following.adapter.holder.PhotoFeedHolder;
import com.wangdaye.main.ui.following.adapter.holder.TitleFeedHolder;
import com.wangdaye.main.ui.following.adapter.model.FollowingModel;
import com.wangdaye.main.ui.following.adapter.model.PhotoFeedModel;
import com.wangdaye.main.ui.following.adapter.model.TitleFeedModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Following adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Photo}.
 *
 * */

public class FollowingAdapter extends BaseAdapter<Photo, FollowingModel, FollowingHolder> {

    private List<FollowingModel> photoItemList;
    private List<FollowingHolder.Factory> factoryList;

    public FollowingAdapter(Context context, List<Photo> list, ItemEventCallback callback) {
        super(context, list);

        this.factoryList = new ArrayList<>();
        factoryList.add(new TitleFeedHolder.Factory(callback));
        factoryList.add(new PhotoFeedHolder.Factory(callback));
    }

    @NonNull
    @Override
    public FollowingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return factoryList.get(viewType).createHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingHolder holder, FollowingModel model) {
        holder.onBindView(model, false);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingHolder holder, FollowingModel model,
                                 @NonNull List<Object> payloads) {
        holder.onBindView(model, !payloads.isEmpty());
    }

    @Override
    public void onViewRecycled(@NonNull FollowingHolder holder) {
        holder.onRecycled();
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < factoryList.size(); i ++) {
            if (factoryList.get(i).isMatch(getItem(position))) {
                return i;
            }
        }

        throw new RuntimeException("Invalid type of ViewHolder.");
    }

    @Deprecated
    protected FollowingModel getViewModel(Photo model) {
        throw new RuntimeException("Deprecated method.");
    }

    private List<FollowingModel> getPhotoItemList() {
        if (photoItemList == null) {
            photoItemList = new ArrayList<>();
        }
        return photoItemList;
    }

    // control.

    public User getUser(int adapterPosition) {
        return getItem(adapterPosition) instanceof TitleFeedModel
                ? ((TitleFeedModel) getItem(adapterPosition)).user
                : getItem(adapterPosition).photo.user;
    }

    public boolean isFooterView(int adapterPosition) {
        return getItemCount() > adapterPosition
                && (adapterPosition + 1 == getItemCount()
                || getItem(adapterPosition + 1) instanceof TitleFeedModel);
    }

    public boolean isPhotoItem(int adapterPosition) {
        return getItem(adapterPosition) instanceof PhotoFeedModel;
    }

    @Deprecated
    @Override
    public void addItem(Photo model) {
        throw new RuntimeException("Deprecated method.");
    }

    @Override
    public void addItems(@NonNull List<Photo> list) {
        List<FollowingModel> vmList = new ArrayList<>(getCurrentList());
        List<FollowingModel> photoList = new ArrayList<>(getPhotoItemList());

        appendItemList(vmList, photoList, list);
        submitList(vmList, () -> {
            getPhotoItemList().clear();
            getPhotoItemList().addAll(photoList);
        });
    }

    @Deprecated
    @Override
    public void removeItem(Photo model) {
        throw new RuntimeException("Deprecated method.");
    }

    @Override
    public void updateItem(Photo model) {
        List<FollowingModel> vmList = new ArrayList<>(getCurrentList());
        List<FollowingModel> photoList = new ArrayList<>(getPhotoItemList());

        PhotoFeedModel newVM = new PhotoFeedModel(getContext(), model, 0, 0);
        for (int i = 0; i < vmList.size(); i ++) {
            if (vmList.get(i).areItemsTheSame(newVM)) {
                FollowingModel oldVM = vmList.get(i);
                newVM.adapterPosition = oldVM.adapterPosition;
                newVM.photoPosition = oldVM.photoPosition;

                vmList.set(i, newVM);
                photoList.set(oldVM.photoPosition, newVM);
            }
        }

        submitList(vmList, () -> {
            getPhotoItemList().clear();
            getPhotoItemList().addAll(photoList);
        });
    }

    public void update(@NonNull List<Photo> list) {
        List<FollowingModel> vmList = new ArrayList<>();
        List<FollowingModel> photoList = new ArrayList<>();
        appendItemList(vmList, photoList, list);

        submitList(vmList, () -> {
            getPhotoItemList().clear();
            getPhotoItemList().addAll(photoList);
        });
    }

    private void appendItemList(List<FollowingModel> itemList, List<FollowingModel> photoItemList,
                                List<Photo> appends) {
        for (Photo append : appends) {
            appendItemList(itemList, photoItemList, append);
        }
    }

    private void appendItemList(List<FollowingModel> itemList, List<FollowingModel> photoItemList,
                                Photo append) {
        int photoCount = photoItemList.size();

        if (itemList.size() == 0) {
            itemList.add(new TitleFeedModel(append, itemList.size(), photoCount));
            itemList.add(new PhotoFeedModel(getContext(), append, itemList.size(), photoCount));
        } else {
            int lastIndex = itemList.size() - 1;
            Photo photo = itemList.get(lastIndex).photo;
            if (!photo.user.username.equals(append.user.username)) {
                itemList.add(new TitleFeedModel(append, itemList.size(), photoCount));
                itemList.add(new PhotoFeedModel(getContext(), append, itemList.size(), photoCount));
            } else {
                itemList.add(new PhotoFeedModel(getContext(), append, itemList.size(), photoCount));
            }
        }
        photoItemList.add(itemList.get(itemList.size() - 1));
    }

    // interface.

    public interface ItemEventCallback {
        void onStartPhotoActivity(View image, View background, int adapterPosition, int photoPosition);
        void onStartUserActivity(View avatar, View background, User user,
                                 @ProfilePager.ProfilePagerRule int index);
        void onVerbClicked(String verb, int adapterPosition);
        void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike);
        void onCollectButtonClicked(Photo photo, int adapterPosition);
        void onDownloadButtonClicked(Photo photo, int adapterPosition);
    }
}

