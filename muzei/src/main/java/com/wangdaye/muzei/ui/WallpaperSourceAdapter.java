package com.wangdaye.muzei.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.muzei.R;
import com.wangdaye.muzei.R2;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Wallpaper source adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link MuzeiWallpaperSource}.
 *
 * */

public class WallpaperSourceAdapter extends RecyclerView.Adapter<WallpaperSourceAdapter.ViewHolder> {

    public MysplashActivity activity;
    public List<MuzeiWallpaperSource> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @OnClick(R2.id.item_wallpaper_source) void clickItem() {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            ComponentFactory.getCollectionModule().startCollectionActivity(
                     activity,
                    String.valueOf(itemList.get(getAdapterPosition()).collectionId)
            );
        }

        @OnClick(R2.id.item_wallpaper_source_deleteBtn) void deleteItem() {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            itemList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());

            if (itemList.size() == 0) {
                itemList.add(MuzeiWallpaperSource.mysplashSource());
                notifyItemInserted(0);
            }
        }

        @BindView(R2.id.item_wallpaper_source_cover) ImageView cover;
        @BindView(R2.id.item_wallpaper_source_title) TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(MuzeiWallpaperSource source) {
            if (TextUtils.isEmpty(source.coverUrl)) {
                ImageHelper.loadResourceImage(cover.getContext(), cover, R.drawable.default_collection_cover);
            } else {
                ImageHelper.loadImageFromUrl(cover.getContext(), cover, source.coverUrl, false, null);
            }
            title.setText(source.title.toUpperCase());
        }

        void onRecycled() {
            ImageHelper.releaseImageView(cover);
        }
    }

    public WallpaperSourceAdapter(MysplashActivity activity, List<MuzeiWallpaperSource> list) {
        this.activity = activity;
        this.itemList = list;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper_source, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        holder.onBindView(itemList.get(position));
    }

    @Override
    public void onViewRecycled(@NotNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
