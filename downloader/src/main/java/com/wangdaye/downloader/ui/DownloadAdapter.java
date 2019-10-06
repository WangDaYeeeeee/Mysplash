package com.wangdaye.downloader.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.downloader.R;
import com.wangdaye.downloader.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Download adapter.
 *
 * Adapter for {@link RecyclerView} to show download missions.
 *
 * */

public class DownloadAdapter extends FooterAdapter<DownloadHolder> {

    private List<DownloadTask> itemList;

    @Nullable private ItemEventCallback callback;

    public DownloadAdapter(List<DownloadTask> itemList) {
        super();
        this.itemList = itemList;
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
    public void onBindViewHolder(@NonNull DownloadHolder holder, int position) {
        holder.onBindView(
                itemList.get(position), false,
                callback
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.onBindView(
                    itemList.get(position), true,
                    callback
            );
        }
    }

    @Override
    public void onViewRecycled(@NonNull DownloadHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    protected boolean hasFooter() {
        return false;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getRealItemCount() {
        return getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ItemEventCallback {
        void onPhotoItemClicked(String photoId);
        void onCollectionItemClicked(String collectionId);
        void onDelete(DownloadTask task, int adapterPosition);
        void onCheck(DownloadTask task, int adapterPosition);
        void onRetry(DownloadTask task, int adapterPosition);
    }

    public DownloadAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

class DownloadHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_download_card) CardView card;
    @BindView(R2.id.item_download_image) AppCompatImageView image;
    @BindView(R2.id.item_download_stateIcon) CircularProgressIcon stateIcon;
    @BindView(R2.id.item_download_title) TextView title;
    @BindView(R2.id.item_download_retry_check_btn) AppCompatImageButton retryCheckBtn;

    private DownloadTask task;
    @Nullable private DownloadAdapter.ItemEventCallback callback;

    DownloadHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(DownloadTask task, boolean update,
                    @Nullable DownloadAdapter.ItemEventCallback callback) {
        this.task = task;
        this.callback = callback;

        if (!update) {
            ImageHelper.loadImageFromUrl(
                    image.getContext(),
                    image, task.photoUri,
                    false,
                    null
            );
        }

        stateIcon.setProgressColor(Color.WHITE);
        switch (task.result) {
            case DownloadTask.RESULT_DOWNLOADING:
                stateIcon.setProgressState();
                title.setText(
                        task.getNotificationTitle().toUpperCase()
                                + " : "
                                + ((int) (task.process)) + "%"
                );
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;

            case DownloadTask.RESULT_SUCCEED:
                stateIcon.setResultState(R.drawable.ic_state_succeed);
                title.setText(task.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                break;

            case DownloadTask.RESULT_FAILED:
                stateIcon.setResultState(R.drawable.ic_state_error);
                title.setText(task.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }

    // interface.

    @OnClick(R2.id.item_download_card) void clickItem() {
        if (callback == null) {
            return;
        }
        if (task.downloadType == DownloadTask.COLLECTION_TYPE) {
            callback.onCollectionItemClicked(
                    task.title.replaceAll("#", "")
            );
        } else {
            callback.onPhotoItemClicked(task.title);
        }
    }

    @OnClick(R2.id.item_download_closeBtn) void clickDeleteButton() {
        if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
            callback.onDelete(task, getAdapterPosition());
        }
    }

    @OnClick(R2.id.item_download_retry_check_btn) void clickRetryOrCheckButton() {
        if (callback == null || getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }
        if (task.result == DownloadTask.RESULT_SUCCEED) {
            callback.onCheck(task, getAdapterPosition());
        } else {
            callback.onRetry(task, getAdapterPosition());
        }
    }
}