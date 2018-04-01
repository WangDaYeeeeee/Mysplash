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
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
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
 * This adapter is used to show {@link Collection}
 * for {@link RecyclerView}.
 *
 * */

public class MoreHorizontalAdapter2 extends RecyclerView.Adapter<MoreHorizontalAdapter2.ViewHolder> {

    private MysplashActivity a;
    private List<Collection> itemList;

    class ViewHolder extends RecyclerView.ViewHolder
            implements ImageHelper.OnLoadImageListener<Photo> {

        @BindView(R.id.item_photo_2_more_page_horizontal)
        CardView card;

        @BindView(R.id.item_photo_2_more_page_horizontal_cover)
        FreedomImageView image;

        @BindView(R.id.item_photo_2_more_page_horizontal_title)
        TextView title;

        @BindView(R.id.item_photo_2_more_page_horizontal_subtitle)
        TextView subtitle;

        @BindView(R.id.item_photo_2_more_page_horizontal_avatar)
        CircleImageView avatar;

        private Collection collection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindView(Collection collection) {
            this.collection = collection;

            title.setText("");
            subtitle.setText("");
            image.setShowShadow(false);

            DisplayUtils.setTypeface(a, subtitle);

            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(image.getContext(), image, collection, getAdapterPosition(), this);
                card.setCardBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                card.getContext(),
                                collection.cover_photo.color));
            } else {
                image.setImageResource(R.color.colorTextContent_light);
            }

            ImageHelper.loadAvatar(avatar.getContext(), avatar, collection.user, getAdapterPosition(), null);

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

        @OnClick(R.id.item_photo_2_more_page_horizontal) void clickItem() {
            IntentHelper.startCollectionActivity(
                    a,
                    avatar,
                    card,
                    collection);
        }

        @OnClick(R.id.item_photo_2_more_page_horizontal_avatar) void checkAuthor() {
            IntentHelper.startUserActivity(
                    a,
                    avatar,
                    card,
                    collection.user,
                    UserActivity.PAGE_PHOTO);
        }

        // on load image listener.

        @Override
        public void onLoadImageSucceed(Photo newT, int index) {
            if (collection.cover_photo.updateLoadInformation(newT)) {
                Collection c = itemList.get(index);
                c.cover_photo.updateLoadInformation(newT);
                itemList.set(index, c);
            }

            title.setText(collection.title.toUpperCase());
            subtitle.setText(collection.user.name);
            image.setShowShadow(true);
        }

        @Override
        public void onLoadImageFailed(Photo originalT, int index) {
            title.setText(collection.title.toUpperCase());
            subtitle.setText(collection.user.name);
            image.setShowShadow(true);
        }
    }

    public MoreHorizontalAdapter2(MysplashActivity a, List<Collection> list) {
        this.a = a;
        this.itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_2_more_page_horizontal, parent, false);
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
