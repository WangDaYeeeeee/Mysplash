package com.wangdaye.mysplash._common.ui.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash._common.ui.widget.CircularProgressIcon;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Download adapter. (Recycler view.)
 * */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder>
        implements DownloadRepeatDialog.OnCheckOrDownloadListener {
    // widget
    private Context c;
    private OnRetryListener listener;

    // data
    public List<DownloadMission> itemList;

    /** <br> life cycle. */

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

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    /** <br> interface. */

    // on retry swipeListener.

    public interface OnRetryListener {
        void onRetry(DownloadMissionEntity entity);
    }

    // on check or download swipeListener.

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

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        ImageView image;
        CircularProgressIcon stateIcon;
        TextView title;
        ImageButton closeBtn;
        ImageButton retryCheckBtn;

        // life cycle.

        ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.item_download_card).setOnClickListener(this);

            this.image = (ImageView) itemView.findViewById(R.id.item_download_image);
            this.stateIcon = (CircularProgressIcon) itemView.findViewById(R.id.item_download_stateIcon);
            this.title = (TextView) itemView.findViewById(R.id.item_download_title);

            this.closeBtn = (ImageButton) itemView.findViewById(R.id.item_download_closeBtn);
            closeBtn.setOnClickListener(this);

            this.retryCheckBtn = (ImageButton) itemView.findViewById(R.id.item_download_retry_check_btn);
            retryCheckBtn.setOnClickListener(this);
        }

        // UI.

        void onBindView(int position) {
            ImageHelper.loadPhoto(c, image, itemList.get(position).entity.getPhotoUri(), false, null);

            switch (itemList.get(position).entity.result) {
                case DownloadHelper.RESULT_DOWNLOADING:
                    stateIcon.forceSetProgressState();
                    title.setText(
                            itemList.get(position).entity.getRealTitle().toUpperCase()
                                    + " : "
                                    + ((int) (itemList.get(position).process)) + "%");
                    retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                    break;

                case DownloadHelper.RESULT_SUCCEED:
                    stateIcon.forceSetResultState(R.drawable.ic_item_state_succeed);
                    title.setText(itemList.get(position).entity.getRealTitle().toUpperCase());
                    retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                    break;

                case DownloadHelper.RESULT_FAILED:
                    stateIcon.forceSetResultState(R.drawable.ic_item_state_error);
                    title.setText(itemList.get(position).entity.getRealTitle().toUpperCase());
                    retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                    break;
            }
        }

        public void drawProcessStatus(DownloadMission mission, boolean switchState) {
            if (switchState) {
                stateIcon.setProgressState();
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
            }
            title.setText(
                    mission.entity.getRealTitle().toUpperCase() + " : " + ((int) mission.process) + "%");
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
            stateIcon.recycleImageView();
        }

        // interface.

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() < 0) {
                return;
            }
            switch (view.getId()) {
                case R.id.item_download_card:
                    if (itemList.get(getAdapterPosition()).entity.downloadType == DownloadHelper.COLLECTION_TYPE) {
                        IntentHelper.startCollectionActivity(
                                Mysplash.getInstance().getTopActivity(),
                                itemList.get(getAdapterPosition()).entity.title.replaceAll("#", ""));
                    } else {
                        IntentHelper.startPhotoActivity(
                                Mysplash.getInstance().getTopActivity(),
                                itemList.get(getAdapterPosition()).entity.title);
                    }
                    break;

                case R.id.item_download_closeBtn:
                    DownloadHelper.getInstance(c)
                            .removeMission(
                                    c,
                                    itemList.get(getAdapterPosition()).entity.missionId);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;

                case R.id.item_download_retry_check_btn:
                    switch (itemList.get(getAdapterPosition()).entity.result) {
                        case DownloadHelper.RESULT_SUCCEED:
                            IntentHelper.startCheckPhotoActivity(
                                    c, itemList.get(getAdapterPosition()).entity.title);
                            break;

                        default:
                            int limitCount = itemList.get(getAdapterPosition())
                                    .entity.result == DownloadHelper.RESULT_DOWNLOADING ? 1 : 0;
                            DownloadMissionEntity entity = itemList.get(getAdapterPosition()).entity;
                            if (DatabaseHelper.getInstance(c).readDownloadingEntityCount(entity.title) > limitCount) {
                                NotificationHelper.showSnackbar(
                                        c.getString(R.string.feedback_download_repeat),
                                        Snackbar.LENGTH_SHORT);
                            } else if (FileUtils.isPhotoExists(c, entity.title)
                                    || FileUtils.isCollectionExists(c, entity.title)) {
                                DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                                dialog.setDownloadKey(entity);
                                dialog.setOnCheckOrDownloadListener(DownloadAdapter.this);
                                dialog.show(Mysplash.getInstance().getTopActivity().getFragmentManager(), null);
                            } else if (listener != null) {
                                listener.onRetry(entity);
                            }
                            break;
                    }
                    break;
            }
        }
    }
}
