package com.wangdaye.mysplash.main.following.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Following holder.
 * */
public abstract class FollowingHolder extends RecyclerView.ViewHolder {

    public interface Factory {

        @NonNull FollowingHolder createHolder(@NonNull ViewGroup parent);
        boolean isMatch(Object data);
        int getType();
    }

    FollowingHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void onBindView(FollowingAdapter.ItemData data, boolean update);

    abstract void onRecycled();
}
