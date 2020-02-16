package com.wangdaye.main.ui.following.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.wangdaye.main.ui.following.adapter.model.FollowingModel;

import java.util.List;

public class FollowingDiffCallback extends DiffUtil.Callback {

    private final List<FollowingModel> oldList;
    private final List<FollowingModel> newList;

    FollowingDiffCallback(List<FollowingModel> oldList, List<FollowingModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).areItemsTheSame(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).areContentsTheSame(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getChangePayload(newList.get(newItemPosition));
    }
}
