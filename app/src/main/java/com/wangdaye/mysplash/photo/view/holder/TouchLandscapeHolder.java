package com.wangdaye.mysplash.photo.view.holder;

import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomTouchView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Touch holder.
 * */

public class TouchLandscapeHolder extends PhotoInfoAdapter.ViewHolder {

    private PhotoActivity activity;

    @BindView(R.id.item_photo_touch_landscape_touch)
    FreedomTouchView touchView;

    @BindView(R.id.item_photo_touch_landscape_avatar)
    CircleImageView avatar;

    @BindView(R.id.item_photo_touch_landscape_title)
    TextView title;

    @BindView(R.id.item_photo_touch_landscape_subtitle)
    TextView subtitle;

    @BindView(R.id.item_photo_touch_landscape_menuBtn)
    ImageButton menuBtn;

    private Photo photo;

    public static final int TYPE_TOUCH_LANDSCAPE = 8;

    public TouchLandscapeHolder(PhotoActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.activity = a;
        DisplayUtils.setTypeface(activity, subtitle);
    }

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        this.photo = photo;

        touchView.setSize(photo.width, photo.height);
        touchView.setShowShadow(true);

        ImageHelper.loadAvatar(a, avatar, photo.user, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-2");
        }

        title.setText(a.getString(R.string.by) + " " + photo.user.name);
        subtitle.setText(a.getString(R.string.on) + " " + photo.created_at.split("T")[0]);
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    @OnClick(R.id.item_photo_touch_landscape_touch) void clickItem() {
        IntentHelper.startPreviewActivity(
                Mysplash.getInstance().getTopActivity(), photo, true);
    }

    @OnClick(R.id.item_photo_touch_landscape_avatar) void clickAvatar() {
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                photo.user,
                UserActivity.PAGE_PHOTO);
    }

    @OnClick(R.id.item_photo_touch_landscape_shareBtn) void share() {
        ShareUtils.sharePhoto(photo);
    }

    @OnClick(R.id.item_photo_touch_landscape_menuBtn) void checkMenu() {
        activity.showPopup(activity, menuBtn, null, 0);
    }
}
