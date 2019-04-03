package com.wangdaye.mysplash.common.ui.adapter.collection;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.network.json.Collection;

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
    private int columnCount;

    @Nullable private ItemEventCallback callback;

    public CollectionAdapter(Context context, List<Collection> list, int columnCount) {
        super(context);
        this.itemList = list;
        this.columnCount = columnCount;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection, parent, false);
            return new CollectionHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CollectionHolder && position < itemList.size()) {
            ((CollectionHolder) holder).onBindView(itemList.get(position), columnCount, callback);
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
        return position;
    }

    @Override
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
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

