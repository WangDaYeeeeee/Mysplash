package com.wangdaye.mysplash.common.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Wallpaper source adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link WallpaperSource}.
 *
 * */

public class WallpaperSourceAdapter extends RecyclerView.Adapter<WallpaperSourceAdapter.ViewHolder> {

    private MysplashActivity c;
    public List<WallpaperSource> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_wallpaper_source_cover)
        ImageView cover;

        @BindView(R.id.item_wallpaper_source_title)
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(WallpaperSource source) {
            if (TextUtils.isEmpty(source.coverUrl)) {
                cover.setImageResource(R.color.colorTextContent_light);
            } else {
                ImageHelper.loadPhoto(c, cover, source.coverUrl, false, null);
            }
            title.setText(source.title.toUpperCase());
        }

        void onRecycled() {
            ImageHelper.releaseImageView(cover);
        }

        @OnClick(R.id.item_wallpaper_source) void clickItem() {
            IntentHelper.startCollectionActivity(
                    c,
                    String.valueOf(itemList.get(getAdapterPosition()).collectionId));
        }

        @OnClick(R.id.item_wallpaper_source_deleteBtn) void deleteItem() {
            itemList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());

            if (itemList.size() == 0) {
                itemList.add(WallpaperSource.buildDefaultSource());
                notifyItemInserted(0);
            }
        }
    }

    public WallpaperSourceAdapter(MysplashActivity c, List<WallpaperSource> list) {
        this.c = c;
        this.itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper_source, parent, false);
        return new WallpaperSourceAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(itemList.get(position));
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
}
