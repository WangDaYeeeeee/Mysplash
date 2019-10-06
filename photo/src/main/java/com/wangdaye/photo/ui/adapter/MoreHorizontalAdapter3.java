package com.wangdaye.photo.ui.adapter;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection landscape adapter.
 *
 * This adapter is used to show {@link Collection}
 * for {@link RecyclerView}.
 *
 * */

public class MoreHorizontalAdapter3 extends RecyclerView.Adapter<MoreHorizontalAdapter3.ViewHolder> {

    private List<Collection> itemList;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.item_photo_3_more_page_horizontal) CardView card;
        @BindView(R2.id.item_photo_3_more_page_horizontal_cover) CoverImageView image;
        @BindView(R2.id.item_photo_3_more_page_horizontal_title) TextView title;
        @BindView(R2.id.item_photo_3_more_page_horizontal_subtitle) TextView subtitle;
        @BindView(R2.id.item_photo_3_more_page_horizontal_avatar) CircularImageView avatar;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindView(Collection collection) {

            title.setText("");
            subtitle.setText("");
            image.setShowShadow(false);

            if (collection.cover_photo != null) {
                ImageHelper.loadCollectionCover(image.getContext(), image, collection, true, () -> {
                    title.setText(collection.title.toUpperCase());
                    subtitle.setText(collection.user.name);
                    image.setShowShadow(true);
                });
                card.setCardBackgroundColor(
                        ImageHelper.computeCardBackgroundColor(
                                card.getContext(),
                                collection.cover_photo.color)
                );
            } else {
                image.setImageResource(R.color.colorDarkCardBackground);
            }

            ImageHelper.loadAvatar(avatar.getContext(), avatar, collection.user, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                card.setTransitionName(collection.id + "-background");
                avatar.setTransitionName(collection.user.username + "-avatar");
            }

            card.setOnClickListener(v -> {
                MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
                if (activity != null) {
                    ComponentFactory.getCollectionModule()
                            .startCollectionActivity(activity, avatar, card, collection);
                }
            });

            avatar.setOnClickListener(v -> {
                MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
                if (activity != null) {
                    ComponentFactory.getUserModule().startUserActivity(
                            activity,
                            avatar,
                            card,
                            collection.user,
                            ProfilePager.PAGE_PHOTO
                    );
                }
            });
        }

        public void onRecycled() {
            ImageHelper.releaseImageView(image);
            ImageHelper.releaseImageView(avatar);
        }
    }

    public MoreHorizontalAdapter3(List<Collection> list) {
        this.itemList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_3_more_page_horizontal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }
}
