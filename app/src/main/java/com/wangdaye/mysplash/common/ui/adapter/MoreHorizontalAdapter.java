package com.wangdaye.mysplash.common.ui.adapter;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Collection landscape adapter.
 *
 * This adapter is used to show {@link com.wangdaye.mysplash.common.data.entity.unsplash.Collection}
 * for {@link android.support.v7.widget.RecyclerView}.
 *
 * */

public class MoreHorizontalAdapter extends RecyclerView.Adapter<MoreHorizontalAdapter.ViewHolder> {

    private MysplashActivity a;
    private List<Collection> itemList;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_photo_more_page_horizontal)
        CardView card;

        @BindView(R.id.item_photo_more_page_horizontal_cover)
        FreedomImageView image;

        @BindView(R.id.item_photo_more_page_horizontal_title)
        TextView title;

        @BindView(R.id.item_photo_more_page_horizontal_subtitle)
        TextView subtitle;

        @BindView(R.id.item_photo_more_page_horizontal_avatar)
        CircleImageView avatar;

        private Collection collection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindView(final Collection collection) {
            this.collection = collection;

            title.setText("");
            subtitle.setText("");
            image.setShowShadow(false);

            DisplayUtils.setTypeface(a, subtitle);

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
                                subtitle.setText(collection.user.name);
                                image.setShowShadow(true);
                            }

                            @Override
                            public void onLoadFailed() {
                                title.setText(collection.title.toUpperCase());
                                subtitle.setText(collection.user.name);
                                image.setShowShadow(true);
                            }
                        });
                card.setCardBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                a,
                                collection.cover_photo.color));
            } else {
                image.setImageResource(R.color.colorTextContent_light);
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

        @OnClick(R.id.item_photo_more_page_horizontal) void clickItem() {
            IntentHelper.startCollectionActivity(
                    a,
                    avatar,
                    card,
                    collection);
        }

        @OnClick(R.id.item_photo_more_page_horizontal_avatar) void checkAuthor() {
            IntentHelper.startUserActivity(
                    a,
                    avatar,
                    collection.user,
                    UserActivity.PAGE_PHOTO);
        }
    }

    public MoreHorizontalAdapter(MysplashActivity a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_more_page_horizontal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }
}
