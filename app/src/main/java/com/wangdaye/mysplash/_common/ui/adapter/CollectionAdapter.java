package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.widget.glide.ColorAnimRequestListener;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

/**
 * Collection adapter. (Recycler view)
 * */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    // widget
    private Context a;
    private List<Collection> itemList;

    /** <br> life cycle. */

    public CollectionAdapter(Context a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new ViewHolder(v, viewType);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText("");
        holder.subtitle.setText("");
        holder.image.setShowShadow(false);
        if (itemList.get(position).cover_photo != null
                && itemList.get(position).cover_photo.width != 0
                && itemList.get(position).cover_photo.height != 0) {
            Glide.with(a)
                    .load(itemList.get(position).cover_photo.urls.regular)
                    .listener(new ColorAnimRequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            if (!itemList.get(position).cover_photo.hasFadedIn) {
                                itemList.get(position).cover_photo.hasFadedIn = true;
                                startColorAnimation(a, holder.image);
                            }
                            holder.title.setText(itemList.get(position).title.toUpperCase());
                            int photoNum = itemList.get(position).total_photos;
                            holder.subtitle.setText(photoNum + " " + a.getResources().getStringArray(R.array.user_tabs)[0]);
                            holder.image.setShowShadow(true);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.image);
            holder.background.setBackgroundColor(
                    DisplayUtils.calcCardBackgroundColor(
                            itemList.get(position).cover_photo.color));
        } else {
            holder.image.setImageResource(R.color.colorTextContent_light);
            holder.title.setText(itemList.get(position).title.toUpperCase());
            int photoNum = itemList.get(position).total_photos;
            holder.subtitle.setText(photoNum + (photoNum > 1 ? " photos" : " photo"));
        }

        DisplayUtils.loadAvatar(a, holder.avatar, itemList.get(position).user.profile_image);
        holder.name.setText(itemList.get(position).user.name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.background.setTransitionName(itemList.get(position).id + "-background");
            holder.avatar.setTransitionName(itemList.get(position).user.username + "-avatar");
        }
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
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

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.image);
        Glide.clear(holder.avatar);
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

    public int getRealItemCount() {
        return itemList.size();
    }

    public List<Collection> getItemList() {
        return itemList;
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