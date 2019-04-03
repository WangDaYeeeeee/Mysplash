package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.download.DownloadMission;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.image.ImageHelper;

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

public class DownloadAdapter extends RecyclerView.Adapter<DownloadHolder> {

    private List<DownloadMission> itemList;

    @Nullable private ItemEventCallback callback;

    public DownloadAdapter(List<DownloadMission> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new DownloadHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, int position) {
        holder.onBindView(itemList.get(position), false, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.onBindView(itemList.get(position), true, callback);
        }
    }

    @Override
    public void onViewRecycled(@NonNull DownloadHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ItemEventCallback {
        void onPhotoItemClicked(String photoId);
        void onCollectionItemClicked(String collectionId);
        void onDelete(DownloadMissionEntity entity, int adapterPosition);
        void onCheck(DownloadMissionEntity entity, int adapterPosition);
        void onRetry(DownloadMissionEntity entity, int adapterPosition);
    }

    public DownloadAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}

class DownloadHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_download_image) AppCompatImageView image;
    @BindView(R.id.item_download_stateIcon) CircularProgressIcon stateIcon;
    @BindView(R.id.item_download_title) TextView title;
    @BindView(R.id.item_download_retry_check_btn) AppCompatImageButton retryCheckBtn;

    private DownloadMission mission;
    @Nullable private DownloadAdapter.ItemEventCallback callback;

    DownloadHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(DownloadMission mission, boolean update,
                    @Nullable DownloadAdapter.ItemEventCallback callback) {
        this.mission = mission;
        this.callback = callback;

        if (!update) {
            ImageHelper.loadImageFromUrl(
                    image.getContext(),
                    image, mission.entity.getPhotoUri(),
                    false,
                    null
            );
        }

        stateIcon.setProgressColor(Color.WHITE);
        switch (mission.entity.result) {
            case DownloadMissionEntity.RESULT_DOWNLOADING:
                stateIcon.setProgressState();
                title.setText(
                        mission.entity.getNotificationTitle().toUpperCase()
                                + " : "
                                + ((int) (mission.process)) + "%");
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;

            case DownloadMissionEntity.RESULT_SUCCEED:
                stateIcon.setResultState(R.drawable.ic_item_state_succeed);
                title.setText(mission.entity.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                break;

            case DownloadMissionEntity.RESULT_FAILED:
                stateIcon.setResultState(R.drawable.ic_item_state_error);
                title.setText(mission.entity.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        stateIcon.recycleImageView();
    }

    // interface.

    @OnClick(R.id.item_download_card) void clickItem() {
        if (callback == null) {
            return;
        }
        if (mission.entity.downloadType == DownloadMissionEntity.COLLECTION_TYPE) {
            callback.onCollectionItemClicked(
                    mission.entity.title.replaceAll("#", "")
            );
        } else {
            callback.onPhotoItemClicked(mission.entity.title);
        }
    }

    @OnClick(R.id.item_download_closeBtn) void clickDeleteButton() {
        if (callback != null) {
            callback.onDelete(mission.entity, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_download_retry_check_btn) void clickRetryOrCheckButton() {
        if (callback == null) {
            return;
        }
        if (mission.entity.result == DownloadMissionEntity.RESULT_SUCCEED) {
            callback.onCheck(mission.entity, getAdapterPosition());
        } else {
            callback.onRetry(mission.entity, getAdapterPosition());
        }
    }
}