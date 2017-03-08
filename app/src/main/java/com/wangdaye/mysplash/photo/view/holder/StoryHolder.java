package com.wangdaye.mysplash.photo.view.holder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/** <br> Story holder. */

public class StoryHolder extends PhotoInfoAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private TextView title;
    private TextView subtitle;
    private TextView content;
    private CircleImageView avatar;

    // data
    private Photo photo;

    public static final int TYPE_STORY = 4;

    /** <br> life cycle. */

    public StoryHolder(View itemView) {
        super(itemView);

        this.title = (TextView) itemView.findViewById(R.id.item_photo_story_title);

        this.subtitle = (TextView) itemView.findViewById(R.id.item_photo_story_subtitle);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), subtitle);

        this.content = (TextView) itemView.findViewById(R.id.item_photo_story_content);
        DisplayUtils.setTypeface(Mysplash.getInstance().getTopActivity(), content);

        this.avatar = (CircleImageView) itemView.findViewById(R.id.item_photo_story_avatar);
        avatar.setOnClickListener(this);
    }

    /** <br> UI. */

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

    /** <br> interface. */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_photo_story_avatar:
                IntentHelper.startUserActivity(
                        Mysplash.getInstance().getTopActivity(),
                        avatar,
                        photo.user,
                        UserActivity.PAGE_PHOTO);
                break;
        }
    }
}
