package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.FooterAdapter;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
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

    private boolean horizontal;

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.item_collection_background)
        RelativeLayout background;

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

        ViewHolder(View itemView, Collection collection) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (horizontal) {
                if (collection.cover_photo != null
                        && collection.cover_photo.width != 0
                        && collection.cover_photo.height != 0) {
                    image.setSize(
                            collection.cover_photo.width,
                            collection.cover_photo.height);
                }
                itemView.findViewById(R.id.item_collection_card).setOnClickListener(this);
            } else {
                background.setOnClickListener(this);
            }

            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), name);
        }

        public void onBindView(final Collection collection) {
            this.collection = collection;

            float[] sizes = image.getSize();
            if (collection.cover_photo != null
                    && (sizes[0] != collection.cover_photo.width || sizes[1] != collection.cover_photo.height)) {
                image.setSize(collection.cover_photo.width, collection.cover_photo.height);
            }

            title.setText("");
            subtitle.setText("");
            name.setText("");
            image.setShowShadow(false);

            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(a, image, collection,
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
                background.setBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                a,
                                collection.cover_photo.color));
            } else {
                image.setImageResource(R.color.colorTextContent_light);
            }

            ImageHelper.loadAvatar(a, avatar, collection.user, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background.setTransitionName(collection.id + "-background");
                avatar.setTransitionName(collection.user.username + "-avatar");
            }
        }

        public void onRecycled() {
            ImageHelper.releaseImageView(image);
            ImageHelper.releaseImageView(avatar);
        }

        // interface.

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_collection_card:
                case R.id.item_collection_background:
                    if (a instanceof MysplashActivity) {
                        IntentHelper.startCollectionActivity(
                                (MysplashActivity) a,
                                avatar,
                                background,
                                collection);
                    }
                    break;
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
        this(a, list, false);
    }

    public CollectionAdapter(Context a, List<Collection> list, boolean horizontal) {
        this.a = a;
        this.itemList = list;
        this.horizontal = horizontal;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            if (horizontal) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_collection_card, parent, false);
                return new ViewHolder(v, itemList.get(position));
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_collection, parent, false);
                return new ViewHolder(v, itemList.get(position));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
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
        return !horizontal && DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void insertItem(Collection c, int position) {
        itemList.add(position, c);
        notifyItemInserted(position);
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

    public void updateCollection(Collection c, boolean probablyRepeat, boolean refreshView) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id == c.id) {
                c.insertingPhoto = itemList.get(i).insertingPhoto;
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