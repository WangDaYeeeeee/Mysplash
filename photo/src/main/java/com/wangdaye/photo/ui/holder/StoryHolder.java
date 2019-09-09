package com.wangdaye.photo.ui.holder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.image.ImageHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** Story holder. */

public class StoryHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_story_container) RelativeLayout container;
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

    public static final int TYPE_STORY = 2;

    public StoryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        if (photo.story != null && !TextUtils.isEmpty(photo.story.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(photo.story.description);
        } else if (!TextUtils.isEmpty(photo.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(capEveryWord(photo.description));
        } else {
            content.setVisibility(View.GONE);
        }

        if (photo.story != null && !TextUtils.isEmpty(photo.story.title)) {
            title.setText(photo.story.title);
        } else {
            title.setText(photo.user.name);
        }

        if (photo.location != null && !TextUtils.isEmpty(photo.location.title)) {
            subtitle.setText(photo.location.title);
        } else {
            subtitle.setText(DisplayUtils.getDate(a, photo.created_at));
        }

        ImageHelper.loadAvatar(a, avatar, photo.user, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-3");
        }

        this.photo = photo;
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    private String capEveryWord(String str){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile(
                "([a-z])([a-z]*)",
                Pattern.CASE_INSENSITIVE
        ).matcher(str);
        while (capMatcher.find()){
            capMatcher.appendReplacement(
                    capBuffer,
                    capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
            );
        }

        return capMatcher.appendTail(capBuffer).toString();
    }
}
