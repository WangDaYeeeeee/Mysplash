package com.wangdaye.photo.ui.adapter.photo.holder;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.CoverImageView;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragCardView;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.photo.ui.adapter.photo.model.MoreModel;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * More holder.
 *
 * This view holder is used to show related photos and collections information.
 *
 * */

public class MoreHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_more) LongPressDragCardView card;
    @BindView(R2.id.item_photo_3_more_cover) CoverImageView image;

    @BindView(R2.id.item_photo_3_more_title) TextView title;
    @BindView(R2.id.item_photo_3_more_subtitle) TextView subtitle;
    @BindView(R2.id.item_photo_3_more_avatar) CircularImageView avatar;
    @BindView(R2.id.item_photo_3_more_name) TextView name;

    private MoreModel model;
    private int cancelFlagMarginTop;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new MoreHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof MoreModel;
        }
    }

    public MoreHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_more);
        ButterKnife.bind(this, itemView);
        cancelFlagMarginTop = (int) new DisplayUtils(itemView.getContext()).dpToPx(98);
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        Context context = itemView.getContext();
        model = (MoreModel) viewModel;

        card.setCoverImage(image);
        card.setLongPressDragChildList(Collections.singletonList(avatar));
        card.setCancelFlagMarginTop(cancelFlagMarginTop);
        card.setOnClickListener(v ->
                ComponentFactory.getCollectionModule().startCollectionActivity(
                        a, avatar, card, model.collection)
        );

        title.setText(model.collection.title.toUpperCase());
        subtitle.setText(model.collection.total_photos + " "
                + context.getResources().getStringArray(R.array.user_tabs)[0]);
        name.setText(model.collection.user.name);

        if (model.collection.cover_photo == null) {
            image.setShowShadow(false);
        } else {
            image.setShowShadow(true);
            int[] size = model.collection.cover_photo.getRegularSize();
            image.setSize(size[0], size[1]);

            LoadImagePresenter.loadCollectionCover(context, image, model.collection, null);
            card.setCardBackgroundColor(
                    ImageHelper.computeCardBackgroundColor(context, model.collection.cover_photo.color));
        }

        LoadImagePresenter.loadUserAvatar(context, avatar, model.collection.user, null);
        avatar.setOnClickListener(v ->
                ComponentFactory.getUserModule().startUserActivity(
                        a, avatar, card, model.collection.user, ProfilePager.PAGE_PHOTO)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setTransitionName(model.collection.id + "-background");
            avatar.setTransitionName(model.collection.user.username + "-avatar");
        }
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(image);
        ImageHelper.releaseImageView(avatar);
    }
}
