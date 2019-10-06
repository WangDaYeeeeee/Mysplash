package com.wangdaye.common.ui.adapter.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Collection adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class CollectionAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Collection> itemList;

    @Nullable private ItemEventCallback callback;

    public CollectionAdapter(List<Collection> list) {
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
            return new CollectionHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_collection, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CollectionHolder && position < getRealItemCount()) {
            ((CollectionHolder) holder).onBindView(
                    itemList.get(position),
                    callback
            );
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).onBindView();
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CollectionHolder) {
            ((CollectionHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) {
            return -1;
        } else {
            return 1;
        }
    }

    public List<Collection> getItemList() {
        return itemList;
    }

    public interface ItemEventCallback {
        void onCollectionClicked(View avatar, View background, Collection c);
        void onUserClicked(View avatar, View background, User u);
    }

    public CollectionAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        callback = c;
        return this;
    }
}

