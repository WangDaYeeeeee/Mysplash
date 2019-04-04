package com.wangdaye.mysplash.common.ui.adapter.user;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

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

    public UserAdapter(Context context, List<User> list) {
        super(context);
        this.itemList = list;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (position >= itemList.size()) {
            return;
        }
        if (holder instanceof UserHolder) {
            ((UserHolder) holder).onBindView(itemList.get(position), callback);
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

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
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

