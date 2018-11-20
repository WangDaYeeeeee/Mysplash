package com.wangdaye.mysplash.photo2.view.holder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.fullScreenView.FullScreenTouchView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;
import com.wangdaye.mysplash.photo2.view.widget.PhotoButtonBar;
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

public class BaseHolder extends PhotoInfoAdapter2.ViewHolder
        implements PhotoButtonBar.OnClickButtonListener {

    private PhotoActivity2 activity;

    @BindView(R.id.item_photo_2_base_touch)
    FullScreenTouchView touchView;

    @BindView(R.id.item_photo_2_base_titleShadow)
    LinearLayout shadow;

    @BindView(R.id.item_photo_2_base_controlBar)
    LinearLayout controlBar;

    @BindView(R.id.item_photo_2_base_avatar)
    CircleImageView avatar;

    @BindView(R.id.item_photo_2_base_titleBar)
    RelativeLayout titleBar;

    @BindView(R.id.item_photo_2_base_title)
    TextView title;

    @BindView(R.id.item_photo_2_base_subtitle)
    TextView subtitle;

    @BindView(R.id.item_photo_2_base_buttonBar)
    PhotoButtonBar buttonBar;

    private Photo photo;
    private boolean landscape;
    private int navigationBarHeight;

    public static final int TYPE_BASE = 0;

    public BaseHolder(PhotoActivity2 a, View itemView, int marginHorizontal, int columnCount) {
        super(itemView, marginHorizontal, columnCount);
        ButterKnife.bind(this, itemView);

        this.activity = a;

        landscape = DisplayUtils.isLandscape(a);
        navigationBarHeight = DisplayUtils.getNavigationBarHeight(a.getResources());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        title.setText(photo.user.name);
        subtitle.setText(DisplayUtils.getDate(a, photo.created_at));

        ImageHelper.loadAvatar(a, avatar, photo.user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-1");
        }

        buttonBar.setState(photo);
        if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
            a.startCheckDownloadProgressThread();
        }
        buttonBar.setOnClickButtonListener(this);

        this.photo = photo;
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    public void showInitAnim() {
        shadow.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);

        AnimUtils.alphaInitShow(shadow, 350);
        AnimUtils.alphaInitShow(titleBar, 350);

        avatar.setScaleX(0);
        avatar.setScaleY(0);
        AnimUtils.animScale(avatar, 300, 350, 1);
    }

    public void onScrolling(int scrollY) {
        if (!landscape && scrollY < navigationBarHeight) {
            shadow.setTranslationY(scrollY);
            controlBar.setTranslationY(scrollY - navigationBarHeight);
        } else {
            if (controlBar.getTranslationY() != 0) {
                controlBar.setTranslationY(0);
            }
            if (shadow.getTranslationY() != navigationBarHeight) {
                shadow.setTranslationY(navigationBarHeight);
            }
        }
    }

    public PhotoButtonBar getButtonBar() {
        return buttonBar;
    }

    // interface.

    // on click listener.

    @OnClick(R.id.item_photo_2_base_touch) void clickTouchView() {
        IntentHelper.startPreviewActivity(
                Mysplash.getInstance().getTopActivity(), photo, true);
    }

    @OnClick(R.id.item_photo_2_base_avatar) void clickAvatar() {
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                controlBar,
                photo.user,
                UserActivity.PAGE_PHOTO);
    }

    // on click button listener.

    @Override
    public void onLikeButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            activity.likePhoto();
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @Override
    public void onCollectButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            activity.collectPhoto();
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @Override
    public void onDownloadButtonClicked() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE, true);
    }

    @Override
    public void onDownloadButtonLongClicked() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
    }
}
