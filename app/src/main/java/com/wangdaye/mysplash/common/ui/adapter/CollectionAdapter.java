package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.FooterAdapter;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Collection adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link Collection}.
 *
 * */

public class CollectionAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private Context a;
    private List<Collection> itemList;

    private int columnCount;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_collection)
        CardView card;

        @BindView(R.id.item_collection_cover)
        FreedomImageView image;

        @BindView(R.id.item_collection_title)
        TextView title;

        @BindView(R.id.item_collection_subtitle)
        TextView subtitle;

        @BindView(R.id.item_collection_avatar)
        CircleImageView avatar;

        @BindView(R.id.item_collection_name)
        TextView name;

        private Collection collection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), name);
        }

        public void onBindView(final Collection collection) {
            this.collection = collection;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            if (columnCount > 1) {
                int margin = a.getResources().getDimensionPixelSize(R.dimen.little_margin);
                params.setMargins(0, 0, margin, margin);
                card.setLayoutParams(params);
                card.setRadius(new DisplayUtils(a).dpToPx(2));
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

            title.setText("");
            subtitle.setText("");
            name.setText("");
            image.setShowShadow(false);

            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(
                        a, image, collection,
                        new ImageHelper.OnLoadImageListener() {
                            @Override
                            public void onLoadSucceed() {
                                if (!collection.cover_photo.hasFadedIn) {
                                    collection.cover_photo.hasFadedIn = true;
                                    ImageHelper.startSaturationAnimation(a, image);
                                }
                                title.setText(collection.title.toUpperCase());
                                int photoNum = collection.total_photos;
                                subtitle.setText(
                                        photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                                name.setText(collection.user.name);
                                image.setShowShadow(true);
                            }

                            @Override
                            public void onLoadFailed() {
                                title.setText(collection.title.toUpperCase());
                                int photoNum = collection.total_photos;
                                subtitle.setText(
                                        photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                                name.setText(collection.user.name);
                                image.setShowShadow(true);
                            }
                        });
                card.setCardBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                a,
                                collection.cover_photo.color));
            } else {
                ImageHelper.loadResourceImage(a, image, R.drawable.default_collection_cover);
            }

            ImageHelper.loadAvatar(a, avatar, collection.user, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                card.setTransitionName(collection.id + "-background");
                avatar.setTransitionName(collection.user.username + "-avatar");
            }
        }

        public void onRecycled() {
            ImageHelper.releaseImageView(image);
            ImageHelper.releaseImageView(avatar);
        }

        // interface.

        @OnClick(R.id.item_collection) void clickItem() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startCollectionActivity(
                        (MysplashActivity) a,
                        avatar,
                        card,
                        collection);
            }
        }

        @OnClick(R.id.item_collection_avatar) void checkAuthor() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startUserActivity(
                        (MysplashActivity) a,
                        avatar,
                        collection.user,
                        UserActivity.PAGE_PHOTO);
            }
        }
    }

    public CollectionAdapter(Context a, List<Collection> list) {
        this(a, list, DisplayUtils.getGirdColumnCount(a));
    }

    public CollectionAdapter(Context a, List<Collection> list, int columnCount) {
        this.a = a;
        this.itemList = list;
        this.columnCount = columnCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && position < itemList.size()) {
            ((ViewHolder) holder).onBindView(itemList.get(position));
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
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

    public void updateCollection(Collection c, boolean probablyRepeat) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id == c.id) {
                boolean refreshView = false;
                try {
                    if ((itemList.get(i).cover_photo != null && c.cover_photo != null
                            && !itemList.get(i).cover_photo.id.equals(c.cover_photo.id))
                            || (itemList.get(i).cover_photo == null && c.cover_photo != null)
                            || (itemList.get(i).cover_photo != null && c.cover_photo == null)) {
                        refreshView = true;
                    }
                    if (!refreshView && !itemList.get(i).title.equals(c.title)) {
                        refreshView = true;
                    }
                    if (!refreshView && itemList.get(i).total_photos != c.total_photos) {
                        refreshView = true;
                    }
                    if (!refreshView) {
                        if ((itemList.get(i).user.profile_image != null && c.user.portfolio_url != null
                                && !itemList.get(i).user.profile_image.large.equals(c.user.profile_image.large))
                                || (itemList.get(i).user.profile_image == null && c.user.portfolio_url != null)
                                || (itemList.get(i).user.profile_image != null && c.user.portfolio_url == null)) {
                            refreshView = true;
                        }
                    }
                    if (!refreshView && !itemList.get(i).user.name.equals(c.user.name)) {
                        refreshView = true;
                    }
                } catch (Exception ignored) {

                }

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