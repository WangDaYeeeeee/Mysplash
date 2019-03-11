package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.download.DownloadMission;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

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

    public List<DownloadMission> itemList;
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
        void onDelete(DownloadMissionEntity entity, int adapterPosition);
        void onRetry(DownloadMissionEntity entity, int adapterPosition);
    }

    public void setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
    }
}

class DownloadHolder extends RecyclerView.ViewHolder
        implements DownloadRepeatDialog.OnCheckOrDownloadListener {

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
                    image.getContext(), image, mission.entity.getPhotoUri(), false, null);
        }

        stateIcon.setProgressColor(Color.WHITE);
        switch (mission.entity.result) {
            case DownloaderService.RESULT_DOWNLOADING:
                stateIcon.setProgressState();
                title.setText(
                        mission.entity.getNotificationTitle().toUpperCase()
                                + " : "
                                + ((int) (mission.process)) + "%");
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;

            case DownloaderService.RESULT_SUCCEED:
                stateIcon.setResultState(R.drawable.ic_item_state_succeed);
                title.setText(mission.entity.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                break;

            case DownloaderService.RESULT_FAILED:
                stateIcon.setResultState(R.drawable.ic_item_state_error);
                title.setText(mission.entity.getNotificationTitle().toUpperCase());
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;
        }
    }

    private void checkDownloadMission(Context context, DownloadMissionEntity entity) {
        if (entity.downloadType == DownloaderService.COLLECTION_TYPE) {
            if (FileUtils.isCollectionExists(context, entity.title)) {
                IntentHelper.startCheckCollectionActivity(context, entity.title);
                return;
            }
        } else {
            if (FileUtils.isPhotoExists(context, entity.title)) {
                IntentHelper.startCheckPhotoActivity(context, entity.title);
                return;
            }
        }
        NotificationHelper.showSnackbar(context.getString(R.string.feedback_file_does_not_exist));
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
        stateIcon.recycleImageView();
    }

    // interface.

    @OnClick(R.id.item_download_card) void clickItem() {
        if (Mysplash.getInstance().getTopActivity() == null) {
            return;
        }
        if (mission.entity.downloadType == DownloaderService.COLLECTION_TYPE) {
            IntentHelper.startCollectionActivity(
                    Mysplash.getInstance().getTopActivity(),
                    mission.entity.title.replaceAll("#", ""));
        } else {
            IntentHelper.startPhotoActivity(
                    Mysplash.getInstance().getTopActivity(),
                    mission.entity.title);
        }
    }

    @OnClick(R.id.item_download_closeBtn) void clickDeleteButton() {
        if (callback != null) {
            callback.onDelete(mission.entity, getAdapterPosition());
        }
    }

    @OnClick(R.id.item_download_retry_check_btn) void clickRetryOrCheckButton() {
        Context context = itemView.getContext();
        if (mission.entity.result == DownloaderService.RESULT_SUCCEED) {
            checkDownloadMission(context, mission.entity);
        } else {
            // If there is another mission that is downloading the same thing, we cannot restart
            // this mission.
            int limitCount = mission.entity.result == DownloaderService.RESULT_DOWNLOADING ? 1 : 0;
            if (DatabaseHelper.getInstance(context)
                    .readDownloadingEntityCount(mission.entity.title) > limitCount) {
                NotificationHelper.showSnackbar(context.getString(R.string.feedback_download_repeat));
            } else if (FileUtils.isPhotoExists(context, mission.entity.title)
                    || FileUtils.isCollectionExists(context, mission.entity.title)) {
                MysplashActivity activity = Mysplash.getInstance().getTopActivity();
                if (activity != null) {
                    DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                    dialog.setDownloadKey(mission.entity);
                    dialog.setOnCheckOrDownloadListener(this);
                    dialog.show(activity.getSupportFragmentManager(), null);
                }
            } else if (callback != null) {
                callback.onRetry(mission.entity, getAdapterPosition());
            }
        }
    }

    // on check or download listener.

    @Override
    public void onCheck(Object obj) {
        if (mission != null
                && mission.entity != null
                && mission.entity.result == DownloaderService.RESULT_SUCCEED) {
            checkDownloadMission(Mysplash.getInstance().getTopActivity(), mission.entity);
        }
    }

    @Override
    public void onDownload(Object obj) {
        if (callback != null && mission != null && mission.entity != null) {
            callback.onRetry(mission.entity, getAdapterPosition());
        }
    }
}