package com.wangdaye.mysplash.photo.view.holder;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Base holder.
 * */

public class BaseHolder extends PhotoInfoAdapter.ViewHolder
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    // widget
    private RelativeLayout displayContainer;
    private Toolbar toolbar;
    private TextView title;
    private TextView subtitle;
    private CircleImageView avatar;
    private PhotoDownloadView downloadView;

    // data
    private Photo photo;

    public static final int TYPE_BASE = 2;

    /** <br> life cycle. */

    public BaseHolder(View itemView) {
        super(itemView);

        MysplashActivity activity = Mysplash.getInstance().getTopActivity();

        this.displayContainer = (RelativeLayout) itemView.findViewById(R.id.item_photo_base_displayContainer);

        this.toolbar = (Toolbar) itemView.findViewById(R.id.item_photo_base_toolbar);
        toolbar.setTitle("");
        if (Mysplash.getInstance().isLightTheme()) {
            if (activity instanceof PhotoActivity && ((PhotoActivity) activity).isBrowsable()) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_light);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
            }
            toolbar.inflateMenu(R.menu.activity_photo_toolbar_light);
        } else {
            if (activity instanceof PhotoActivity && ((PhotoActivity) activity).isBrowsable()) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
            }
            toolbar.inflateMenu(R.menu.activity_photo_toolbar_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        this.title = (TextView) itemView.findViewById(R.id.item_photo_base_title);

        this.subtitle = (TextView) itemView.findViewById(R.id.item_photo_base_subtitle);
        DisplayUtils.setTypeface(activity, subtitle);

        this.avatar = (CircleImageView) itemView.findViewById(R.id.item_photo_base_avatar);
        avatar.setOnClickListener(this);

        this.downloadView = (PhotoDownloadView) itemView.findViewById(R.id.item_photo_base_btnBar);
        downloadView.setOnClickListener(this);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        title.setText(a.getString(R.string.by) + " " + photo.user.name);
        subtitle.setText(a.getString(R.string.on) + " " + photo.created_at.split("T")[0]);

        DisplayUtils.loadAvatar(a, avatar, photo.user.profile_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username);
        }

        if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
            downloadView.setProgressState();
            ((PhotoActivity) a).startCheckDownloadProgressThread();
        } else {
            downloadView.setButtonState();
        }

        this.photo = photo;
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

    /** <br> interface. */

    @Override
    public void onClick(View view) {
        if (Mysplash.getInstance().getTopActivity() instanceof PhotoActivity) {
            PhotoActivity activity = (PhotoActivity) Mysplash.getInstance().getTopActivity();
            switch (view.getId()) {
                case -1:
                    if (activity.isBrowsable()) {
                        activity.visitParentActivity();
                    }
                    activity.finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                    break;

                case R.id.item_photo_base_avatar:
                    IntentHelper.startUserActivity(
                            Mysplash.getInstance().getTopActivity(),
                            avatar,
                            photo.user,
                            UserActivity.PAGE_PHOTO);
                    break;

                case R.id.container_download_downloadBtn:
                    activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
                    break;

                case R.id.container_download_shareBtn:
                    activity.readyToDownload(DownloadHelper.SHARE_TYPE);
                    break;

                case R.id.container_download_wallBtn:
                    activity.readyToDownload(DownloadHelper.WALLPAPER_TYPE);
                    break;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (Mysplash.getInstance().getTopActivity() instanceof PhotoActivity) {
            PhotoActivity activity = (PhotoActivity) Mysplash.getInstance().getTopActivity();
            switch (item.getItemId()) {
                case R.id.action_share:
                    ShareUtils.sharePhoto(photo);
                    break;

                case R.id.action_menu:
                    activity.showPopup(activity, toolbar, null, 0);
                    break;
            }
        }
        return true;
    }
}
