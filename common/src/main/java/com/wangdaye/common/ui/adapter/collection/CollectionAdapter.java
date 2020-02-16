package com.wangdaye.common.ui.adapter.collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
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

public class CollectionAdapter extends BaseAdapter<Collection, CollectionModel, CollectionHolder> {

    @Nullable private ItemEventCallback callback;

    public CollectionAdapter(Context context, List<Collection> list) {
        super(context, list);
    }

    @NotNull
    @Override
    public CollectionHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new CollectionHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_collection, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionHolder holder, CollectionModel model) {
        holder.onBindView(model, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionHolder holder, CollectionModel model, @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    public void onViewRecycled(@NonNull CollectionHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected CollectionModel getViewModel(Collection model) {
        return new CollectionModel(getContext(), model);
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

