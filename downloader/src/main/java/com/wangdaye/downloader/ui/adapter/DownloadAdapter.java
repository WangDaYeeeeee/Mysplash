package com.wangdaye.downloader.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.downloader.R;

import java.util.List;

/**
 * Download adapter.
 *
 * Adapter for {@link RecyclerView} to show download missions.
 *
 * */

public class DownloadAdapter extends BaseAdapter<DownloadTask, DownloadModel, DownloadHolder> {

    @Nullable private ItemEventCallback callback;

    public DownloadAdapter(Context context, List<DownloadTask> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_download, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull DownloadHolder holder, DownloadModel model) {
        holder.onBindView(model, false, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull DownloadHolder holder, DownloadModel model,
                                    @NonNull List<Object> payloads) {
        holder.onBindView(model, !payloads.isEmpty(), callback);
    }

    @Override
    public void onViewRecycled(@NonNull DownloadHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected DownloadModel getViewModel(DownloadTask model) {
        return new DownloadModel(model);
    }

    public interface ItemEventCallback {
        void onPhotoItemClicked(String photoId);
        void onCollectionItemClicked(String DownloadTaskId);
        void onDelete(DownloadTask task, int adapterPosition);
        void onCheck(DownloadTask task, int adapterPosition);
        void onRetry(DownloadTask task, int adapterPosition);
    }

    public DownloadAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

