package com.wangdaye.mysplash.me.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * My follow adapter.
 *
 * Adapter for {@link RecyclerView} to show follow information.
 *
 * */

public class MyFollowAdapter extends FooterAdapter<MyFollowHolder> {

    private List<User> itemList;
    @Nullable private ItemEventCallback callback;

    public MyFollowAdapter(Context context, List<User> list) {
        super(context);
        this.itemList = list;
    }

    @NotNull
    @Override
    public MyFollowHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_follow_user, parent, false);
        return new MyFollowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull MyFollowHolder holder, int position) {
        holder.onBindView(itemList.get(position), callback);
    }

    public void onViewRecycled(@NotNull MyFollowHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    protected boolean hasFooter(Context context) {
        return false;
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ItemEventCallback {
        void onFollowItemClicked(View avatar, View background, User user);
        void onFollowUserOrCancel(User user, int adapterPosition, boolean follow);
    }

    public MyFollowAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        callback = c;
        return this;
    }
}

