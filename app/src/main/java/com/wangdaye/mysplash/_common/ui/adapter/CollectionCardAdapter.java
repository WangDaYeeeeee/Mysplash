package com.wangdaye.mysplash._common.ui.adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

/**
 * Collection card adapter. (Recycler view)
 * */

public class CollectionCardAdapter extends RecyclerView.Adapter<CollectionCardAdapter.ViewHolder> {
    // widget
    private MysplashActivity a;
    private List<Collection> itemList;

    /** <br> life cycle. */

    public CollectionCardAdapter(MysplashActivity a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
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
        holder.onRecycled();
    }

    public int getRealItemCount() {
        return itemList.size();
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

        ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.item_collection_card).setOnClickListener(this);

            this.background = (RelativeLayout) itemView.findViewById(R.id.item_collection_card_background);

            this.image = (FreedomImageView) itemView.findViewById(R.id.item_collection_card_cover);

            this.title = (TextView) itemView.findViewById(R.id.item_collection_card_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_card_subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);

            this.avatar = (CircleImageView) itemView.findViewById(R.id.item_collection_card_avatar);
            avatar.setOnClickListener(this);

            this.name = (TextView) itemView.findViewById(R.id.item_collection_card_name);
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
                case R.id.item_collection_card:
                    IntentHelper.startCollectionActivity(
                            a,
                            avatar,
                            background,
                            itemList.get(getAdapterPosition()));
                    break;

                case R.id.item_collection_card_avatar:
                    IntentHelper.startUserActivity(
                            a,
                            avatar,
                            itemList.get(getAdapterPosition()).user,
                            UserActivity.PAGE_PHOTO);
                    break;
            }
        }
    }
}