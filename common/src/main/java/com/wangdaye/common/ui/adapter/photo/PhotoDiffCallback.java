package com.wangdaye.common.ui.adapter.photo;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.wangdaye.base.unsplash.Photo;

import java.util.List;

public class PhotoDiffCallback extends DiffUtil.Callback {

    private final List<Photo> oldList;
    private final List<Photo> newList;

    public PhotoDiffCallback(List<Photo> oldList, List<Photo> newList) {
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
        return oldList.get(oldItemPosition).id.equals(newList.get(newItemPosition).id);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Photo oldP = oldList.get(oldItemPosition);
        final Photo newP = newList.get(newItemPosition);
        return oldP.liked_by_user == newP.liked_by_user
                && oldP.settingLike == newP.settingLike
                && oldP.downloading == newP.downloading
                && (oldP.current_user_collections != null && oldP.current_user_collections.size() == 0)
                == (newP.current_user_collections != null && newP.current_user_collections.size() == 0);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return 1;
    }
}
