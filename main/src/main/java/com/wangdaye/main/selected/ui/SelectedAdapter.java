package com.wangdaye.main.selected.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Selected adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class SelectedAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private List<Collection> itemList;

    public SelectedAdapter(List<Collection> list) {
        super();
        this.itemList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            // footer.
            return new FooterHolder(parent);
        } else {
            return new SelectedHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_selected, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectedHolder && position < getRealItemCount()) {
            ((SelectedHolder) holder).onBindView(itemList.get(position));
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).onBindView();
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof SelectedHolder) {
            ((SelectedHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? -1 : 1;
    }

    public List<Collection> getItemList() {
        return itemList;
    }

    public void updateCollection(Collection c, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id == c.id) {
                c.editing = itemList.get(i).editing;
                if (c.cover_photo != null && itemList.get(i).cover_photo != null) {
                    c.cover_photo.loadPhotoSuccess = itemList.get(i).cover_photo.loadPhotoSuccess;
                    c.cover_photo.hasFadedIn = itemList.get(i).cover_photo.hasFadedIn;
                    c.cover_photo.settingLike = itemList.get(i).cover_photo.settingLike;
                }
                itemList.set(i, c);
                if (refreshView) {
                    notifyItemChanged(i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }
}

class SelectedHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_selected) CardView card;
    @BindView(R2.id.item_selected_cover) CoverImageView image;
    @BindView(R2.id.item_selected_title) TextView title;
    @BindView(R2.id.item_selected_content) TextView content;

    SelectedHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(Collection collection) {
        Context context = itemView.getContext();

        card.setOnClickListener(v -> {
            MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
            if (activity != null) {
                ComponentFactory.getCollectionModule().startCollectionActivity(activity, collection);
            }
        });

        if (collection.cover_photo != null
                && collection.cover_photo.width != 0
                && collection.cover_photo.height != 0) {
            image.setSize(
                    collection.cover_photo.width,
                    collection.cover_photo.height);
        }

        title.setText(collection.title);
        if (!TextUtils.isEmpty(collection.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(collection.description);
        } else {
            content.setVisibility(View.GONE);
        }

        if (collection.cover_photo != null) {
            ImageHelper.loadCollectionCover(image.getContext(), image, collection, true, null);
        } else {
            ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(image);
    }
}