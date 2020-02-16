package com.wangdaye.main.ui.following.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.main.ui.following.adapter.model.FollowingModel;

/**
 * Following holder.
 * */
public abstract class FollowingHolder extends RecyclerView.ViewHolder {

    public interface Factory {

        @NonNull FollowingHolder createHolder(@NonNull ViewGroup parent);
        boolean isMatch(FollowingModel model);
    }

    FollowingHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindView(FollowingModel model, boolean update);

    public abstract void onRecycled();
}
