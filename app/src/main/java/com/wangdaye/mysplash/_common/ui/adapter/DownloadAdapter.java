package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.wangdaye.mysplash._common.data.tools.DownloadManager;

import java.util.List;

/**
 * Download adapter. (Recycler view.)
 * */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    // widget
    private Context c;
    private List<DownloadManager.Mission> itemList;
    private OnDownloadResponseListener listener;

    /** <br> life cycle. */

    public DownloadAdapter(Context c, List<DownloadManager.Mission> list) {
        this.c = c;
        this.itemList = list;
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
                .load(itemList.get(position).photo.urls.regular)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.image);

        if (itemList.get(position).failed) {
            holder.title.setText(c.getString(R.string.feedback_download_failed));
            holder.retryBtn.setVisibility(View.VISIBLE);
        } else {
            holder.title.setText(
                    itemList.get(position).photo.id.toUpperCase()
                            + " : " + itemList.get(position).progress + "%");
            holder.retryBtn.setVisibility(View.GONE);
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

    public int getItemAdapterPosition(int id) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == id) {
                return i;
            }
        }
        return DownloadManager.FAILED_CODE;
    }

    public DownloadManager.Mission getItem(int position) {
        return itemList.get(position);
    }

    // add.

    public void insertItem(DownloadManager.Mission item, int position) {
        itemList.add(position, item);
        notifyItemInserted(position);
    }

    // delete.

    public void removeItem(int id) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == id) {
                removeItemFromList(i);
                return;
            }
        }
    }

    private void removeItemFromList(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    // change.

    public void setItemProgress(int id, int percent) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == id) {
                itemList.get(i).progress = percent;
                return;
            }
        }
    }

    public void setItemFailed(int id) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == id) {
                itemList.get(i).failed = true;
                notifyItemChanged(i);
                return;
            }
        }
    }

    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> interface. */

    public interface OnDownloadResponseListener {
        void onCancelDownload(DownloadManager.Mission m);
        void onRetryDownload(DownloadManager.Mission m);
    }

    public void setOnDownloadResponseListener(OnDownloadResponseListener l) {
        this.listener = l;
    }

    /** <br> inner class. */

    // view holder.

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public CardView card;
        public ImageView image;
        public TextView title;
        public ImageButton cancelBtn;
        public ImageButton retryBtn;

        // life cycle.

        public ViewHolder(View itemView) {
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
                    if (listener != null) {
                        listener.onCancelDownload(itemList.get(getAdapterPosition()));
                    }
                    removeItemFromList(getAdapterPosition());
                    break;

                case R.id.item_download_retryBtn:
                    DownloadManager.Mission m = itemList.get(getAdapterPosition());
                    removeItemFromList(getAdapterPosition());
                    if (listener != null) {
                        listener.onRetryDownload(m);
                    }
                    break;
            }
        }
    }
}
