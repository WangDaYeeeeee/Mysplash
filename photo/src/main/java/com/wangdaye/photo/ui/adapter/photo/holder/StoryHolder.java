package com.wangdaye.photo.ui.adapter.photo.holder;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.photo.ui.adapter.photo.model.StoryModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** Story holder. */

public class StoryHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_story_title) TextView title;
    @BindView(R2.id.item_photo_3_story_subtitle) TextView subtitle;

    @OnClick(R2.id.item_photo_3_story_subtitle) void checkSubtitle() {
        if (photo != null &&
                photo.location != null
                && !TextUtils.isEmpty(photo.location.title))
            ComponentFactory.getSearchModule().startSearchActivity(
                    MysplashApplication.getInstance().getTopActivity(),
                    photo.location.title
            );
    }
    @BindView(R2.id.item_photo_3_story_content) TextView content;
    @BindView(R2.id.item_photo_3_story_avatar) CircularImageView avatar;
    @OnClick(R2.id.item_photo_3_story_avatar) void checkAuthor() {
        ComponentFactory.getUserModule().startUserActivity(
                MysplashApplication.getInstance().getTopActivity(),
                avatar,
                itemView,
                photo.user,
                ProfilePager.PAGE_PHOTO
        );
    }

    private Photo photo;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new StoryHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof StoryModel;
        }
    }

    public StoryHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_story);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        StoryModel model = (StoryModel) viewModel;
        this.photo = model.photo;

        if (!TextUtils.isEmpty(model.content)) {
            content.setVisibility(View.VISIBLE);
            content.setText(model.content);
        } else {
            content.setVisibility(View.GONE);
        }

        title.setText(model.title);
        subtitle.setText(model.subtitle);

        LoadImagePresenter.loadUserAvatar(a, avatar, model.author, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-3");
        }
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }
}
