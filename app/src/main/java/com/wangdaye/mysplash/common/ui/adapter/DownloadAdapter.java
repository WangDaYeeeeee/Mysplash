package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.util.ArrayList;
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

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder>
        implements DownloadRepeatDialog.OnCheckOrDownloadListener {

    private Context c;
    private OnRetryListener listener;

    public List<DownloadMission> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_download_image)
        ImageView image;

        @BindView(R.id.item_download_stateIcon)
        CircularProgressIcon stateIcon;

        @BindView(R.id.item_download_title)
        TextView title;

        @BindView(R.id.item_download_retry_check_btn)
        ImageButton retryCheckBtn;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(int position) {
            ImageHelper.loadImageFromUrl(
                    c, image, itemList.get(position).entity.getPhotoUri(), false, null);

            switch (itemList.get(position).entity.result) {
                case DownloadHelper.RESULT_DOWNLOADING:
                    stateIcon.forceSetProgressState();
                    title.setText(
                            itemList.get(position).entity.getNotificationTitle().toUpperCase()
                                    + " : "
                                    + ((int) (itemList.get(position).process)) + "%");
                    retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                    break;

                case DownloadHelper.RESULT_SUCCEED:
                    stateIcon.forceSetResultState(R.drawable.ic_item_state_succeed);
                    title.setText(itemList.get(position).entity.getNotificationTitle().toUpperCase());
                    retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                    break;

                case DownloadHelper.RESULT_FAILED:
                    stateIcon.forceSetResultState(R.drawable.ic_item_state_error);
                    title.setText(itemList.get(position).entity.getNotificationTitle().toUpperCase());
                    retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                    break;
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
            stateIcon.recycleImageView();
        }

        public void drawProcessStatus(DownloadMission mission, boolean switchState) {
            if (switchState) {
                stateIcon.setProgressState();
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
            }
            title.setText(
                    mission.entity.getNotificationTitle().toUpperCase() + " : " + ((int) mission.process) + "%");
        }

        // interface.

        @OnClick(R.id.item_download_card) void clickItem() {
            if (itemList.get(getAdapterPosition()).entity.downloadType == DownloadHelper.COLLECTION_TYPE) {
                IntentHelper.startCollectionActivity(
                        Mysplash.getInstance().getTopActivity(),
                        itemList.get(getAdapterPosition()).entity.title.replaceAll("#", ""));
            } else {
                IntentHelper.startPhotoActivity(
                        Mysplash.getInstance().getTopActivity(),
                        itemList.get(getAdapterPosition()).entity.title);
            }
        }

        @OnClick(R.id.item_download_closeBtn) void remove() {
            DownloadHelper.getInstance(c)
                    .removeMission(
                            c,
                            itemList.get(getAdapterPosition()).entity.missionId);
            itemList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        @OnClick(R.id.item_download_retry_check_btn) void checkOrRetry() {
            DownloadMissionEntity entity = itemList.get(getAdapterPosition()).entity;
            if (entity.result == DownloadHelper.RESULT_SUCCEED) {
                if (entity.downloadType == DownloadHelper.COLLECTION_TYPE) {
                    if (FileUtils.isCollectionExists(c, entity.title)) {
                        IntentHelper.startCheckCollectionActivity(c, entity.title);
                        return;
                    }
                } else {
                    if (FileUtils.isPhotoExists(c, entity.title)) {
                        IntentHelper.startCheckPhotoActivity(c, entity.title);
                        return;
                    }
                }
                NotificationHelper.showSnackbar(c.getString(R.string.feedback_file_does_not_exist));
            } else {
                // If there is another mission that is downloading the same thing, we cannot restart
                // this mission.
                int limitCount = entity.result == DownloadHelper.RESULT_DOWNLOADING ? 1 : 0;
                if (DatabaseHelper.getInstance(c).readDownloadingEntityCount(entity.title) > limitCount) {
                    NotificationHelper.showSnackbar(c.getString(R.string.feedback_download_repeat));
                } else if (FileUtils.isPhotoExists(c, entity.title)
                        || FileUtils.isCollectionExists(c, entity.title)) {
                    MysplashActivity activity = Mysplash.getInstance().getTopActivity();
                    if (activity != null) {
                        DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                        dialog.setDownloadKey(entity);
                        dialog.setOnCheckOrDownloadListener(DownloadAdapter.this);
                        dialog.show(activity.getFragmentManager(), null);
                    }
                } else if (listener != null) {
                    listener.onRetry(entity);
                }
            }
        }
    }

    public DownloadAdapter(Context c, OnRetryListener l) {
        this.c = c;
        this.listener = l;

        this.itemList = new ArrayList<>();
        List<DownloadMissionEntity> entityList;
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadHelper.RESULT_FAILED);
        for (int i = 0; i < entityList.size(); i ++) {
            itemList.add(
                    new DownloadMission(
                            entityList.get(i)));
        }
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadHelper.RESULT_DOWNLOADING);
        for (int i = 0; i < entityList.size(); i ++) {
            itemList.add(
                    DownloadHelper.getInstance(c)
                            .getDownloadMission(
                                    c,
                                    entityList.get(i).missionId));
        }
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadHelper.RESULT_SUCCEED);
        for (int i = 0; i < entityList.size(); i ++) {
            itemList.add(
                    new DownloadMission(
                            entityList.get(i)));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
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

    // interface.

    // on retry listener.

    public interface OnRetryListener {
        void onRetry(DownloadMissionEntity entity);
    }

    // on check or download listener.

    @Override
    public void onCheck(Object obj) {
        DownloadMissionEntity entity = (DownloadMissionEntity) obj;
        if (entity.downloadType == DownloadHelper.COLLECTION_TYPE) {
            IntentHelper.startCheckCollectionActivity(c, entity.title);
        } else {
            IntentHelper.startCheckPhotoActivity(c, entity.title);
        }
    }

    @Override
    public void onDownload(Object obj) {
        if (listener != null) {
            listener.onRetry((DownloadMissionEntity) obj);
        }
    }
}
