package com.wangdaye.mysplash._common.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.FooterAdapter;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection adapter. (Recycler view)
 * */

public class CollectionAdapter extends FooterAdapter<RecyclerView.ViewHolder> {
    // widget
    private Context a;
    private List<Collection> itemList;

    /** <br> life cycle. */

    public CollectionAdapter(Context a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    @Override
    protected boolean hasFooter() {
        return DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> UI. */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
            return new ViewHolder(v, position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onBindView(position);
        }
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    /** <br> data. */

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onRecycled();
        }
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

    public void changeItem(Collection c) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id == c.id) {
                itemList.remove(i);
                itemList.add(i, c);
                notifyItemChanged(i);
                return;
            }
        }
        insertItem(c, 0);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public List<Collection> getItemList() {
        return itemList;
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

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public RelativeLayout background;
        public FreedomImageView image;
        public TextView title;
        public TextView subtitle;
        CircleImageView avatar;
        TextView name;

        // life cycle.

        ViewHolder(View itemView, int position) {
            super(itemView);

            this.background = (RelativeLayout) itemView.findViewById(R.id.item_collection_background);
            background.setOnClickListener(this);

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_collection_cover);
            if (itemList.get(position).cover_photo != null
                    && itemList.get(position).cover_photo.width != 0
                    && itemList.get(position).cover_photo.height != 0) {
                image.setSize(itemList.get(position).cover_photo.width, itemList.get(position).cover_photo.height);
            }

            this.title = (TextView) itemView.findViewById(R.id.item_collection_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);

            this.avatar = (CircleImageView) itemView.findViewById(R.id.item_collection_avatar);
            avatar.setOnClickListener(this);

            this.name = (TextView) itemView.findViewById(R.id.item_collection_name);
            DisplayUtils.setTypeface(itemView.getContext(), name);
        }

        // UI.

        void onBindView(final int position) {
            title.setText("");
            subtitle.setText("");
            image.setShowShadow(false);

            if (itemList.get(position).cover_photo != null) {
                ImageHelper.loadCollectionCover(a, image, itemList.get(position), new ImageHelper.OnLoadImageListener() {
                    @Override
                    public void onLoadSucceed() {
                        if (!itemList.get(position).cover_photo.hasFadedIn) {
                            itemList.get(position).cover_photo.hasFadedIn = true;
                            ImageHelper.startSaturationAnimation(a, image);
                        }
                        title.setText(itemList.get(position).title.toUpperCase());
                        int photoNum = itemList.get(position).total_photos;
                        subtitle.setText(photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                        image.setShowShadow(true);
                    }

                    @Override
                    public void onLoadFailed() {
                        title.setText(itemList.get(position).title.toUpperCase());
                        int photoNum = itemList.get(position).total_photos;
                        subtitle.setText(photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                        image.setShowShadow(true);
                    }
                });
                background.setBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                itemList.get(position).cover_photo.color));
            } else {
                image.setImageResource(R.color.colorTextContent_light);
            }

            ImageHelper.loadAvatar(a, avatar, itemList.get(position).user, null);
            name.setText(itemList.get(position).user.name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background.setTransitionName(itemList.get(position).id + "-background");
                avatar.setTransitionName(itemList.get(position).user.username + "-avatar");
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
            ImageHelper.releaseImageView(avatar);
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_background:
                    if (a instanceof MysplashActivity) {
                        IntentHelper.startCollectionActivity(
                                (MysplashActivity) a,
                                avatar,
                                background,
                                itemList.get(getAdapterPosition()));
                    }
                    break;

                case R.id.item_collection_avatar:
                    if (a instanceof MysplashActivity) {
                        IntentHelper.startUserActivity(
                                (MysplashActivity) a,
                                avatar,
                                itemList.get(getAdapterPosition()).user,
                                UserActivity.PAGE_PHOTO);
                    }
                    break;
            }
        }
    }
}