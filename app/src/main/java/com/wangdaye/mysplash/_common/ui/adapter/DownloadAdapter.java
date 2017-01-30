package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
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
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadMissionEntity.RESULT_FAILED);
        for (int i = 0; i < entityList.size(); i ++) {
            itemList.add(new DownloadMission(entityList.get(i)));
        }
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadMissionEntity.RESULT_DOWNLOADING);
        for (int i = 0; i < entityList.size(); i ++) {
            float process = DownloadHelper.getMissionProcess(
                    DownloadHelper.getInstance(c).getMissionCursor(
                            entityList.get(i).missionId));
            itemList.add(new DownloadMission(entityList.get(i), process));
        }
        entityList = DatabaseHelper.getInstance(c).readDownloadEntityList(DownloadMissionEntity.RESULT_SUCCEED);
        for (int i = 0; i < entityList.size(); i ++) {
            itemList.add(new DownloadMission(entityList.get(i)));
        }
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(c)
                .load(itemList.get(position).entity.photoUri)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.image);
        switch (itemList.get(position).entity.result) {
            case DownloadMissionEntity.RESULT_SUCCEED:
                Glide.with(c)
                        .load(R.drawable.ic_item_state_succeed)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.stateIcon);
                holder.title.setText(itemList.get(position).entity.getRealTitle().toUpperCase());
                holder.retryCheckBtn.setImageResource(R.drawable.ic_item_check);
                break;

            case DownloadMissionEntity.RESULT_DOWNLOADING:
                Glide.with(c)
                        .load(R.drawable.ic_item_state_downloading)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.stateIcon);
                holder.title.setText(
                        itemList.get(position).entity.getRealTitle().toUpperCase()
                                + " : "
                                + ((int) (itemList.get(position).process)) + "%");
                holder.retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;

            case DownloadMissionEntity.RESULT_FAILED:
                Glide.with(c)
                        .load(R.drawable.ic_item_state_error)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.stateIcon);
                holder.title.setText(itemList.get(position).entity.getRealTitle().toUpperCase());
                holder.retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
                break;
        }
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
        Glide.clear(holder.image);
    }

    public int getRealItemCount() {
        return getItemCount();
    }

    /** <br> interface. */

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

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        ImageView image;
        ImageView stateIcon;
        TextView title;
        ImageButton closeBtn;
        ImageButton retryCheckBtn;

        // life cycle.

        ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.item_download_card).setOnClickListener(this);

            this.image = (ImageView) itemView.findViewById(R.id.item_download_image);
            this.stateIcon = (ImageView) itemView.findViewById(R.id.item_download_stateIcon);
            this.title = (TextView) itemView.findViewById(R.id.item_download_title);

            this.closeBtn = (ImageButton) itemView.findViewById(R.id.item_download_closeBtn);
            closeBtn.setOnClickListener(this);

            this.retryCheckBtn = (ImageButton) itemView.findViewById(R.id.item_download_retry_check_btn);
            retryCheckBtn.setOnClickListener(this);
        }

        // UI.

        public void drawProcessStatus(DownloadMissionEntity entity, @Nullable Cursor cursor, boolean switchState) {
            float process = (cursor == null || cursor.getCount() == 0) ?
                    0 : DownloadHelper.getMissionProcess(cursor);
            if (switchState) {
                Glide.with(c)
                        .load(R.drawable.ic_item_state_downloading)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(stateIcon);
                retryCheckBtn.setImageResource(R.drawable.ic_item_retry);
            }
            title.setText(
                    entity.getRealTitle().toUpperCase() + " : " + ((int) process) + "%");
        }

        // interface.

        @Override
        public void onClick(View view) {
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
                    DatabaseHelper.getInstance(c)
                            .deleteDownloadEntity(
                                    itemList.get(getAdapterPosition()).entity.missionId);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;

                case R.id.item_download_retry_check_btn:
                    switch (itemList.get(getAdapterPosition()).entity.result) {
                        case DownloadMissionEntity.RESULT_SUCCEED:
                            IntentHelper.startCheckPhotoActivity(
                                    c, itemList.get(getAdapterPosition()).entity.title);
                            break;

                        default:
                            DownloadMissionEntity entity = itemList.get(getAdapterPosition()).entity;
                            if (DatabaseHelper.getInstance(c).readDownloadingEntityCount(entity.title) > 0) {
                                NotificationUtils.showSnackbar(
                                        c.getString(R.string.feedback_download_repeat),
                                        Snackbar.LENGTH_SHORT);
                            } else if (FileUtils.isPhotoExists(c, entity.title)
                                    || FileUtils.isCollectionExists(c, entity.title)) {
                                DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                                dialog.setDownlaodKey(entity);
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
