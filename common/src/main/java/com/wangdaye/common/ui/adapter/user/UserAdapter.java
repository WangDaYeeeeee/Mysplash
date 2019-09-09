package com.wangdaye.common.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.unsplash.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User adapter.
 *
 * Adapter for {@link RecyclerView} to show users.
 *
 * */

public class UserAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<User> itemList;
    @Nullable private ItemEventCallback callback;

    public UserAdapter(List<User> list) {
        super();
        this.itemList = list;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return new FooterHolder(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserHolder && position < getRealItemCount()) {
            ((UserHolder) holder).onBindView(itemList.get(position), callback);
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).onBindView();
        }
    }

    @Override
    public void onViewRecycled(@NotNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof UserHolder) {
            ((UserHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? -1 : 1;
    }

    public interface ItemEventCallback {
        void onStartUserActivity(View avatar, View background, User user, int index);
        void onPortfolioButtonClicked(User user);
    }

    public UserAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

