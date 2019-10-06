package com.wangdaye.main.following.ui.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.main.following.ui.adapter.FollowingAdapter;

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

    public abstract void onBindView(FollowingAdapter.ItemData data, boolean update);

    public abstract void onRecycled();
}
