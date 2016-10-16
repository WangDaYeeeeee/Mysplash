package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
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
import com.wangdaye.mysplash._common.utils.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Download adapter. (Recycler view.)
 * */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    // widget
    private Context c;

    /** <br> life cycle. */

    public DownloadAdapter(Context c) {
        this.c = c;
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
                .load(DownloadHelper.getInstance(c).entityList.get(position).photoUri)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.image);

        Cursor cursor = ((DownloadManager) c.getSystemService(DOWNLOAD_SERVICE)).query(
                new DownloadManager.Query().setFilterById(
                                        DownloadHelper.getInstance(c).entityList.get(position).missionId));
        cursor.moveToFirst();
        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
            holder.title.setText(c.getString(R.string.feedback_download_photo_failed));
            holder.retryBtn.setVisibility(View.VISIBLE);
        } else {
            holder.title.setText(
                    DownloadHelper.getInstance(c).entityList.get(position).photoId.toUpperCase()
                            + " : " + getProcess(cursor) + "%");
            holder.retryBtn.setVisibility(View.GONE);
        }
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return DownloadHelper.getInstance(c).entityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public DownloadMissionEntity getItem(int position) {
        return DownloadHelper.getInstance(c).entityList.get(position);
    }

    public int getProcess(Cursor cursor) {
        int soFar = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        int total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        return (int) (100.0 * soFar / total);
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

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_download_cancelBtn:
                    DownloadHelper.getInstance(c)
                            .removeMission(
                                    c,
                                    DownloadHelper.getInstance(c)
                                            .entityList
                                            .get(getAdapterPosition()).missionId);
                    notifyItemRemoved(getAdapterPosition());
                    break;

                case R.id.item_download_retryBtn:
                    DownloadHelper.getInstance(c).restartMission(c, getAdapterPosition());
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
