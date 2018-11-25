package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FooterAdapter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Selected adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class SelectedAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private Context a;
    private List<Collection> itemList;

    private int columnCount;

    class ViewHolder extends RecyclerView.ViewHolder
            implements ImageHelper.OnLoadImageListener<Photo> {

        @BindView(R.id.item_selected)
        CardView card;

        @BindView(R.id.item_selected_cover)
        FreedomImageView image;

        @BindView(R.id.item_selected_title)
        TextView title;

        @BindView(R.id.item_selected_content)
        TextView content;

        private Collection collection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void onBindView(final Collection collection) {
            this.collection = collection;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            if (columnCount > 1) {
                int margin = a.getResources().getDimensionPixelSize(R.dimen.normal_margin);
                params.setMargins(0, 0, margin, margin);
                card.setLayoutParams(params);
                card.setRadius(a.getResources().getDimensionPixelSize(R.dimen.material_card_radius));
            } else {
                params.setMargins(0, 0, 0, 0);
                card.setLayoutParams(params);
                card.setRadius(0);
            }

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
                ImageHelper.loadCollectionCover(image.getContext(), image, collection, getAdapterPosition(), this);
            } else {
                ImageHelper.loadResourceImage(image.getContext(), image, R.drawable.default_collection_cover);
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
        }

        @SuppressLint("SetTextI18n")
        void update(int position) {
            Collection newItem = itemList.get(position);
            if ((newItem.cover_photo != null && collection.cover_photo == null)
                    || (newItem.cover_photo == null && collection.cover_photo != null)
                    || (newItem.cover_photo != null && collection.cover_photo != null
                    && !newItem.cover_photo.id.equals(collection.cover_photo.id))) {
                notifyItemChanged(position);
            } else {
                collection = newItem;
                title.setText(collection.title);
            }
        }

        // interface.

        @OnClick(R.id.item_selected) void clickItem() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startCollectionActivity((MysplashActivity) a, collection);
            }
        }

        // on load image listener.

        @SuppressLint("SetTextI18n")
        @Override
        public void onLoadImageSucceed(Photo newT, int index) {
            if (collection.cover_photo.updateLoadInformation(newT)) {
                Collection c = itemList.get(index);
                c.cover_photo.updateLoadInformation(newT);
                itemList.set(index, c);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onLoadImageFailed(Photo originalT, int index) {
            // do nothing.
        }
    }

    public SelectedAdapter(Context a, List<Collection> list) {
        this(a, list, DisplayUtils.getGirdColumnCount(a));
    }

    public SelectedAdapter(Context a, List<Collection> list, int columnCount) {
        this.a = a;
        this.itemList = list;
        this.columnCount = columnCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && position < itemList.size()) {
            ((ViewHolder) holder).onBindView(itemList.get(position));
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected boolean hasFooter() {
        return !DisplayUtils.isLandscape(a)
                && DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void insertItem(Collection c, int position) {
        if (position <= itemList.size()) {
            itemList.add(position, c);
            notifyItemInserted(position);
        }
    }

    public void removeItem(Collection c) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == c.id) {
                itemList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public List<Collection> getItemList() {
        return itemList;
    }

    public void updateCollection(RecyclerView recyclerView,
                                 Collection c, boolean refreshView, boolean probablyRepeat) {
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
                    updateItemView(recyclerView, i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }

    private void updateItemView(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            notifyItemChanged(position);
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] firstVisiblePositions = manager.findFirstVisibleItemPositions(null);
            int[] lastVisiblePositions = manager.findLastVisibleItemPositions(null);
            if (firstVisiblePositions[0] <= position
                    && position <= lastVisiblePositions[lastVisiblePositions.length - 1]) {
                // is a visible item.
                ((ViewHolder) recyclerView.findViewHolderForAdapterPosition(position)).update(position);
            } else {
                notifyItemChanged(position);
            }
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (firstVisiblePosition <= position && position <= lastVisiblePosition) {
                // is a visible item.
                ((ViewHolder) recyclerView.findViewHolderForAdapterPosition(position)).update(position);
            } else {
                notifyItemChanged(position);
            }
        }
    }

    public void setCollectionData(List<Collection> list) {
        itemList.clear();
        itemList.addAll(list);
        notifyDataSetChanged();
    }

    public List<Collection> getCollectionData() {
        List<Collection> list = new ArrayList<>();
        list.addAll(itemList);
        return list;
    }
}