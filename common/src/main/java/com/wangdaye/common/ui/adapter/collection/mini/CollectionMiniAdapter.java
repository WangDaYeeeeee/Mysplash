package com.wangdaye.common.ui.adapter.collection.mini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;

import java.util.List;

/**
 * Collection mini adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection} in mini style.
 *
 * */

public class CollectionMiniAdapter extends BaseAdapter<ProgressCollection, CollectionMiniModel, CollectionMiniHolder> {

    private Photo photo;
    @Nullable private ItemEventCallback callback;

    public CollectionMiniAdapter(Context context, List<ProgressCollection> list, Photo p) {
        super(context);
        this.update(p, list);
    }

    @NonNull
    @Override
    public CollectionMiniHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_mini, parent, false);
        return new CollectionMiniHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionMiniHolder holder, CollectionMiniModel model) {
        holder.onBindView(model, photo, false, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionMiniHolder holder, CollectionMiniModel model,
                                    @NonNull List<Object> payloads) {
        holder.onBindView(model, photo, !payloads.isEmpty(), callback);
    }

    @Override
    public void onViewRecycled(@NonNull CollectionMiniHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected CollectionMiniModel getViewModel(ProgressCollection model) {
        if (model.collection != null) {
            return new CollectionMiniModel(getContext(), photo, model.collection, model.progressing);
        } else {
            return new CollectionMiniModel();
        }
    }

    @Deprecated
    @Override
    public void addItem(ProgressCollection model) {
        throw new RuntimeException("Deprecated method.");
    }

    @Deprecated
    @Override
    public void addItems(@NonNull List<ProgressCollection> list) {
        throw new RuntimeException("Deprecated method.");
    }

    @Deprecated
    @Override
    public void removeItem(ProgressCollection model) {
        throw new RuntimeException("Deprecated method.");
    }

    @Deprecated
    @Override
    public void updateItem(ProgressCollection model) {
        throw new RuntimeException("Deprecated method.");
    }

    public void update(Photo photo, @NonNull List<ProgressCollection> list) {
        this.photo = photo;

        list.add(0, new ProgressCollection(null, false));
        submitList(getViewModelList(list), null);
    }

    @Deprecated
    @Override
    public void update(@NonNull List<ProgressCollection> list) {
        // do nothing.
    }

    public interface ItemEventCallback {
        void onCreateCollection();
        void onAddPhotoToCollectionOrRemoveIt(Collection collection, Photo photo,
                                              int adapterPosition, boolean add);
    }

    public CollectionMiniAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

