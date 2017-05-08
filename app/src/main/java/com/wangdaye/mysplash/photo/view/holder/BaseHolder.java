package com.wangdaye.mysplash.photo.view.holder;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Base holder.
 *
 * This view holder is used to show the basic part of the photo information.
 *
 * */

public class BaseHolder extends PhotoInfoAdapter.ViewHolder
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private PhotoActivity activity;

    @BindView(R.id.item_photo_base_displayContainer)
    RelativeLayout displayContainer;

    @BindView(R.id.item_photo_base_toolbar)
    Toolbar toolbar;

    @BindView(R.id.item_photo_base_title)
    TextView title;

    @BindView(R.id.item_photo_base_subtitle)
    TextView subtitle;

    @BindView(R.id.item_photo_base_avatar)
    CircleImageView avatar;

    @BindView(R.id.item_photo_base_btnBar)
    PhotoDownloadView downloadView;

    private Photo photo;

    public static final int TYPE_BASE = 2;

    public BaseHolder(PhotoActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.activity = a;

        toolbar.setTitle("");
        if (activity.isBrowsable()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        ThemeManager.inflateMenu(
                toolbar, R.menu.activity_photo_toolbar_light, R.menu.activity_photo_toolbar_dark);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        DisplayUtils.setTypeface(activity, subtitle);
    }

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        title.setText(a.getString(R.string.by) + " " + photo.user.name);
        subtitle.setText(a.getString(R.string.on) + " " + photo.created_at.split("T")[0]);

        ImageHelper.loadAvatar(a, avatar, photo.user, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-1");
        }

        if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
            downloadView.setProgressState();
            ((PhotoActivity) a).startCheckDownloadProgressThread();
        } else {
            downloadView.setButtonState();
        }

        this.photo = photo;
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    public void showInitAnim() {
        displayContainer.setVisibility(View.GONE);
        downloadView.setVisibility(View.GONE);
        AnimUtils.animInitShow(displayContainer, 200);
        AnimUtils.animInitShow(downloadView, 350);
    }

    public PhotoDownloadView getDownloadView() {
        return downloadView;
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (activity.isBrowsable()) {
                    activity.visitParentActivity();
                }
                activity.finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    @OnClick(R.id.item_photo_base_avatar) void clickAvatar() {
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                photo.user,
                UserActivity.PAGE_PHOTO);
    }

    @OnClick(R.id.container_download_downloadBtn) void download() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
    }

    @OnClick(R.id.container_download_shareBtn) void share() {
        activity.readyToDownload(DownloadHelper.SHARE_TYPE);
    }

    @OnClick(R.id.container_download_wallBtn) void setWallpaper() {
        activity.readyToDownload(DownloadHelper.WALLPAPER_TYPE);
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                ShareUtils.sharePhoto(photo);
                break;

            case R.id.action_menu:
                activity.showPopup(activity, toolbar, null, 0);
                break;
        }
        return true;
    }
}
