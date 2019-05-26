package com.wangdaye.mysplash.main.following.ui.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wangdaye.mysplash.common.basic.adapter.MultiColumnAdapter;
import com.wangdaye.mysplash.main.following.ui.adapter.FollowingAdapter;

/**
 * Following holder.
 * */
public abstract class FollowingHolder extends MultiColumnAdapter.ViewHolder {

    public interface Factory {

        @NonNull FollowingHolder createHolder(@NonNull ViewGroup parent);
        boolean isMatch(Object data);
        int getType();
    }

    FollowingHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindView(FollowingAdapter.ItemData data, boolean update,
                                    int columnCount, int gridMarginPixel, int singleColumnMarginPixel);

    public abstract void onRecycled();
}
