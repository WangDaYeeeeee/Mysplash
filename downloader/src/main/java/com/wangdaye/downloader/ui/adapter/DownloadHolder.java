package com.wangdaye.downloader.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularProgressIcon;
import com.wangdaye.downloader.R;
import com.wangdaye.downloader.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    void onBindView(DownloadModel model, boolean update,
                    @Nullable DownloadAdapter.ItemEventCallback callback) {
        this.task = model.task;
        this.callback = callback;

        if (!update) {
            ImageHelper.loadImage(image.getContext(), image, model.coverUrl);
        }

        title.setText(model.title);

        stateIcon.setProgressColor(Color.WHITE);
        if (model.downloading) {
            stateIcon.setProgressState();
            retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
        } else if (model.succeed) {
            stateIcon.setResultState(R.drawable.ic_state_succeed);
            retryCheckBtn.setImageResource(R.drawable.ic_item_check);
        } else {
            stateIcon.setResultState(R.drawable.ic_state_error);
            retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
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
