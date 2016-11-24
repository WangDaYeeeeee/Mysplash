package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Download adapter. (Recycler view.)
 * */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    // widget
    private Context c;

    // data
    public List<DownloadMission> itemList;

    /** <br> life cycle. */

    public DownloadAdapter(Context c) {
        this.c = c;

        this.itemList = new ArrayList<>();
        List<DownloadMissionEntity> entityList = DatabaseHelper.getInstance(c).readDownloadEntity();
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

        Cursor cursor = DownloadHelper.getInstance(c).getMissionCursor(itemList.get(position).entity.missionId);
        if (cursor != null) {
            if (DownloadHelper.isMissionFailed(cursor)) {
                holder.drawFailedStatus();
                itemList.get(position).process = -1;
            } else if (DownloadHelper.isMissionSuccess(cursor)) {
                holder.drawSuccessStatus();
                itemList.get(position).process = 100;
            } else {
                holder.drawProcessStatus(itemList.get(position).entity, cursor);
                itemList.get(position).process = DownloadHelper.getMissionProcess(cursor);
            }
        } else {
            holder.drawProcessStatus(itemList.get(position).entity);
            itemList.get(position).process = 0;
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

    public int getRealItemCount() {
        return getItemCount();
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        CardView card;
        public ImageView image;
        public TextView title;
        ImageButton cancelBtn;
        ImageButton retryBtn;

        // life cycle.

        ViewHolder(View itemView) {
            super(itemView);

            this.card = (CardView) itemView.findViewById(R.id.item_download_card);
            this.image = (ImageView) itemView.findViewById(R.id.item_download_image);
            this.title = (TextView) itemView.findViewById(R.id.item_download_title);

            this.cancelBtn = (ImageButton) itemView.findViewById(R.id.item_download_cancelBtn);
            cancelBtn.setOnClickListener(this);

            this.retryBtn = (ImageButton) itemView.findViewById(R.id.item_download_retryBtn);
            retryBtn.setOnClickListener(this);
        }

        // UI.

        public void drawProcessStatus(DownloadMissionEntity entity, Cursor cursor) {
            title.setText(
                    entity.photoId.toUpperCase() + " : " + DownloadHelper.getMissionProcess(cursor) + "%");
            retryBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
        }

        public void drawProcessStatus(DownloadMissionEntity entity) {
            title.setText(
                    entity.photoId.toUpperCase() + " : " + "0.0%");
            retryBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
        }

        void drawSuccessStatus() {
            title.setText(c.getString(R.string.feedback_download_success));
            retryBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }

        public void drawFailedStatus() {
            title.setText(c.getString(R.string.feedback_download_photo_failed));
            retryBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_download_cancelBtn:
                    DownloadHelper.getInstance(c)
                            .removeMission(
                                    c,
                                    itemList.get(getAdapterPosition()).entity.missionId);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;

                case R.id.item_download_retryBtn:
                    DownloadHelper.getInstance(c).restartMission(c, getAdapterPosition());
                    itemList.add(itemList.get(getAdapterPosition()));
                    itemList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
