package com.wangdaye.mysplash.photo.view.holder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** <br> Story holder. */

public class StoryHolder extends PhotoInfoAdapter.ViewHolder {

    @BindView(R.id.item_photo_story_title)
    TextView title;

    @BindView(R.id.item_photo_story_subtitle)
    TextView subtitle;

    @BindView(R.id.item_photo_story_content)
    TextView content;

    @BindView(R.id.item_photo_story_avatar)
    CircleImageView avatar;

    private Photo photo;

    public static final int TYPE_STORY = 4;

    public StoryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), subtitle);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), content);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        if (TextUtils.isEmpty(photo.story.description)) {
            content.setVisibility(View.GONE);
        } else {
            content.setVisibility(View.VISIBLE);
            content.setText(photo.story.description);
        }

        if (TextUtils.isEmpty(photo.story.title)) {
            title.setText("A Story");
        } else {
            title.setText(photo.story.title);
        }

        subtitle.setText(a.getString(R.string.by) + " " + photo.user.name);

        ImageHelper.loadAvatar(a, avatar, photo.user, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username);
        }

        this.photo = photo;
    }

    @OnClick(R.id.item_photo_story_avatar) void checkAuthor() {
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                photo.user,
                UserActivity.PAGE_PHOTO);
    }
}
