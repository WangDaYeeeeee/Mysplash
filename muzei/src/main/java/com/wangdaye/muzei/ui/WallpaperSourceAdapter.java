package com.wangdaye.muzei.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.muzei.R;
import com.wangdaye.muzei.R2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

public class WallpaperSourceAdapter
        extends BaseAdapter<MuzeiWallpaperSource, WallpaperSourceAdapter.ViewModel, WallpaperSourceAdapter.ViewHolder> {

    public MysplashActivity activity;

    class ViewModel implements BaseAdapter.ViewModel {

        MuzeiWallpaperSource source;

        ViewModel(MuzeiWallpaperSource source) {
            this.source = source;
        }

        @Override
        public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
            return newModel instanceof ViewModel
                    && ((ViewModel) newModel).source.collectionId == source.collectionId;
        }

        @Override
        public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
            return false;
        }

        @Override
        public Object getChangePayload(BaseAdapter.ViewModel newModel) {
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewModel model;

        @OnClick(R2.id.item_wallpaper_source) void clickItem() {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            ComponentFactory.getCollectionModule().startCollectionActivity(
                     activity,
                    String.valueOf(model.source.collectionId)
            );
        }

        @OnClick(R2.id.item_wallpaper_source_deleteBtn) void deleteItem() {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            if (getItemCount() > 1) {
                removeItem(model.source);
            } else {
                List<MuzeiWallpaperSource> list = new ArrayList<>();
                list.add(MuzeiWallpaperSource.mysplashSource());
                update(list);
            }
        }

        @BindView(R2.id.item_wallpaper_source_cover) ImageView cover;
        @BindView(R2.id.item_wallpaper_source_title) TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(ViewModel model) {
            this.model = model;

            if (TextUtils.isEmpty(model.source.coverUrl)) {
                ImageHelper.loadImage(cover.getContext(), cover, R.drawable.default_collection_cover);
            } else {
                ImageHelper.loadImage(cover.getContext(), cover, model.source.coverUrl);
            }
            title.setText(model.source.title.toUpperCase());
        }

        void onRecycled() {
            ImageHelper.releaseImageView(cover);
        }
    }

    public WallpaperSourceAdapter(MysplashActivity activity, List<MuzeiWallpaperSource> list) {
        super(activity, list);
        this.activity = activity;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper_source, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, ViewModel model) {
        holder.onBindView(model);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, ViewModel model,
                                    @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    public void onViewRecycled(@NotNull ViewHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected ViewModel getViewModel(MuzeiWallpaperSource model) {
        return new ViewModel(model);
    }
}
