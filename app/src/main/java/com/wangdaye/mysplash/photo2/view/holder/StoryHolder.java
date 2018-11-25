package com.wangdaye.mysplash.photo2.view.holder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** <br> Story holder. */

public class StoryHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_story_container)
    RelativeLayout container;

    @BindView(R.id.item_photo_2_story_title)
    TextView title;

    @BindView(R.id.item_photo_2_story_subtitle)
    TextView subtitle;

    @BindView(R.id.item_photo_2_story_content)
    TextView content;

    @BindView(R.id.item_photo_2_story_avatar)
    CircleImageView avatar;

    private Photo photo;

    public static final int TYPE_STORY = 2;

    public StoryHolder(View itemView, int marginHorizontal, int columnCount) {
        super(itemView, marginHorizontal, columnCount);
        ButterKnife.bind(this, itemView);

        if (marginHorizontal > 0 && columnCount == PhotoInfoAdapter2.COLUMN_COUNT_HORIZONTAL) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
            params.setMarginStart(marginHorizontal);
            params.setMarginEnd(marginHorizontal);
            container.setLayoutParams(params);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        if (!TextUtils.isEmpty(photo.story.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(photo.story.description);
        } else if (!TextUtils.isEmpty(photo.description)) {
            content.setVisibility(View.VISIBLE);
            content.setText(capEveryWord(photo.description));
        } else {
            content.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(photo.story.title)) {
            title.setText(photo.story.title);
        } else {
            title.setText(photo.user.name);
        }

        if (photo.location != null && !TextUtils.isEmpty(photo.location.title)) {
            subtitle.setText(photo.location.title);
        } else {
            subtitle.setText(DisplayUtils.getDate(a, photo.created_at));
        }

        ImageHelper.loadAvatar(a, avatar, photo.user);
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
                Pattern.CASE_INSENSITIVE).matcher(str);
        while (capMatcher.find()){
            capMatcher.appendReplacement(
                    capBuffer,
                    capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @OnClick(R.id.item_photo_2_story_avatar) void checkAuthor() {
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                itemView,
                photo.user,
                UserActivity.PAGE_PHOTO);
    }

    @OnClick(R.id.item_photo_2_story_subtitle) void checkSubtitle() {
        if (photo != null &&
                photo.location != null
                && !TextUtils.isEmpty(photo.location.title))
        IntentHelper.startSearchActivity(
                Mysplash.getInstance().getTopActivity(),
                photo.location.title);
    }
}
