package com.wangdaye.common.ui.adapter.collection.mini;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.wangdaye.base.unsplash.Collection;

import java.util.List;

public class CollectionMiniDiffCallback extends DiffUtil.Callback {

    private final List<Collection> oldList;
    private final List<Collection> newList;
    private final @Nullable Collection updateItem;

    public CollectionMiniDiffCallback(List<Collection> oldList, List<Collection> newList,
                                      @Nullable Collection updateItem) {
        this.oldList = oldList;
        this.newList = newList;
        this.updateItem = updateItem;
    }

    @Override
    public int getOldListSize() {
        return oldList.size() + 1;
    }

    @Override
    public int getNewListSize() {
        return oldList.size() + 1;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldItemPosition == 0 && newItemPosition == 0) {
            return true;
        }
        return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldItemPosition == 0 && newItemPosition == 0) {
            return true;
        }

        return updateItem != null && updateItem.id == newList.get(newItemPosition).id;
    }
}
