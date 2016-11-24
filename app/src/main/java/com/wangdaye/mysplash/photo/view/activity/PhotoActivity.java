package com.wangdaye.mysplash.photo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash._common.i.view.PhotoInfoView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash._common.ui.dialog.StatsDialog;
import com.wangdaye.mysplash._common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomTouchView;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.photo.model.activity.BorwsableObject;
import com.wangdaye.mysplash.photo.model.activity.DownloadObject;
import com.wangdaye.mysplash.photo.model.activity.PhotoInfoObject;
import com.wangdaye.mysplash.photo.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.ScrollImplementor;
import com.wangdaye.mysplash.photo.view.widget.PhotoDetailsView;
import com.wangdaye.mysplash.user.model.widget.ScrollObject;

/**
 * Photo activity.
 * */

public class PhotoActivity extends MysplashActivity
        implements PhotoInfoView, ScrollView, PopupManageView, BrowsableView,
        View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        NotificationUtils.SnackbarContainer {
    // model.
    private PhotoInfoModel photoInfoModel;
    private DownloadModel downloadModel;
    private ScrollModel scrollModel;
    private BrowsableModel browsableModel;

    // view.
    private RequestBrowsableDataDialog requestDialog;

    private CoordinatorLayout container;
    private NestedScrollView scrollView;
    private Toolbar toolbar;
    private CircleImageView avatarImage;
    private PhotoDetailsView detailsView;

    // presenter.
    private PhotoInfoPresenter photoInfoPresenter;
    private DownloadPresenter downloadPresenter;
    private ScrollPresenter scrollPresenter;
    private PopupManagePresenter popupManagePresenter;
    private BrowsablePresenter browsablePresenter;

    // data
    public static final String KEY_PHOTO_ACTIVITY_PHOTO = "photo_activity_photo";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView(true);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
        browsablePresenter.cancelRequest();
        if (detailsView != null) {
            detailsView.cancelRequest();
        }
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Photo);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Photo);
        }
    }

    @Override
    protected void backToTop() {
        scrollPresenter.scrollToTop();
    }

    @Override
    protected boolean needSetStatusBarTextDark() {
        return false;
    }

    @Override
    public void finishActivity(int dir) {
        SwipeBackCoordinatorLayout.hideBackgroundShadow(container);
        if (!browsablePresenter.isBrowsable()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
            switch (dir) {
                case SwipeBackCoordinatorLayout.UP_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_top);
                    break;

                case SwipeBackCoordinatorLayout.DOWN_DIR:
                    overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                    break;
            }
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photoInfoPresenter = new PhotoInfoImplementor(photoInfoModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
    }

    /** <br> view. */

    // init.

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView(boolean init) {
        if (init && browsablePresenter.isBrowsable()) {
            browsablePresenter.requestBrowsableData();
        } else {
            this.container = (CoordinatorLayout) findViewById(R.id.activity_photo_container);

            SwipeBackCoordinatorLayout swipeBackView
                    = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_photo_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            FreedomImageView photoImage = (FreedomImageView) findViewById(R.id.activity_photo_image);
            photoImage.setSize(photoInfoPresenter.getPhoto().width, photoInfoPresenter.getPhoto().height);
            Glide.with(this)
                    .load(photoInfoPresenter.getPhoto().urls.regular)
                    .override(
                            photoInfoPresenter.getPhoto().getRegularWidth(),
                            photoInfoPresenter.getPhoto().getRegularHeight())
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(photoImage);

            StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_photo_statusBar);
            if (DisplayUtils.isNeedSetStatusBarMask()) {
                statusBar.setBackgroundResource(R.color.colorPrimary_light);
                statusBar.setMask(true);
            }

            this.scrollView = (NestedScrollView) findViewById(R.id.activity_photo_scrollView);
            scrollView.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);

            FreedomTouchView touchView = (FreedomTouchView) findViewById(R.id.activity_photo_touchView);
            touchView.setSize(photoInfoPresenter.getPhoto().width, photoInfoPresenter.getPhoto().height);
            touchView.setOnClickListener(this);

            RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.activity_photo_titleBar);

            this.toolbar = (Toolbar) findViewById(R.id.activity_photo_toolbar);
            toolbar.setTitle("");
            if (Mysplash.getInstance().isLightTheme()) {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_light);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
                }
                toolbar.inflateMenu(R.menu.activity_photo_toolbar_light);
            } else {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
                }
                toolbar.inflateMenu(R.menu.activity_photo_toolbar_dark);
            }
            toolbar.setNavigationOnClickListener(this);
            toolbar.setOnMenuItemClickListener(scrollToolbarMenuListener);

            this.avatarImage = (CircleImageView) findViewById(R.id.activity_photo_avatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatarImage.setTransitionName(photoInfoPresenter.getPhoto().user.username);
            }
            avatarImage.setOnClickListener(this);
            Glide.with(this)
                    .load(photoInfoPresenter.getPhoto().user.profile_image.large)
                    .priority(Priority.NORMAL)
                    .crossFade(300)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(128, 128)
                    .into(avatarImage);

            TextView title = (TextView) findViewById(R.id.activity_photo_title);
            title.setText(getString(R.string.by) + " " + photoInfoPresenter.getPhoto().user.name);

            TextView subtitle = (TextView) findViewById(R.id.activity_photo_subtitle);
            subtitle.setText(getString(R.string.on) + " " + photoInfoPresenter.getPhoto().created_at.split("T")[0]);
            DisplayUtils.setTypeface(this, subtitle);

            LinearLayout buttonBar = (LinearLayout) findViewById(R.id.activity_photo_btnBar);

            ImageButton[] optionButtons = new ImageButton[] {
                    (ImageButton) findViewById(R.id.activity_photo_downloadBtn),
                    (ImageButton) findViewById(R.id.activity_photo_shareBtn),
                    (ImageButton) findViewById(R.id.activity_photo_wallBtn)};
            for (ImageButton optionButton : optionButtons) {
                optionButton.setOnClickListener(this);
            }

            TextView[] optionTexts = new TextView[] {
                    (TextView) findViewById(R.id.activity_photo_downloadTxt),
                    (TextView) findViewById(R.id.activity_photo_shareTxt),
                    (TextView) findViewById(R.id.activity_photo_wallTxt)};
            String[] downloadOptions = getResources().getStringArray(R.array.download_options);
            for (int i = 0; i < optionTexts.length; i ++) {
                optionTexts[i].setText(downloadOptions[i]);
            }

            this.detailsView = (PhotoDetailsView) findViewById(R.id.activity_photo_detailsView);
            detailsView.initMP(photoInfoPresenter.getPhoto());
            detailsView.requestPhotoDetails();

            if (Mysplash.getInstance().isLightTheme()) {
                optionButtons[0].setImageResource(R.drawable.ic_download_light);
                optionButtons[1].setImageResource(R.drawable.ic_send_light);
                optionButtons[2].setImageResource(R.drawable.ic_mountain_light);
            } else {
                optionButtons[0].setImageResource(R.drawable.ic_download_dark);
                optionButtons[1].setImageResource(R.drawable.ic_send_dark);
                optionButtons[2].setImageResource(R.drawable.ic_mountain_dark);
            }

            AnimUtils.animInitShow(titleBar, 200);
            AnimUtils.animInitShow(buttonBar, 300);
            AnimUtils.animInitShow(detailsView, 400);
        }
    }

    /** <br> model. */

    private void initModel() {
        this.photoInfoModel = new PhotoInfoObject((Photo) getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO));
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.scrollModel = new ScrollObject();
        this.browsableModel = new BorwsableObject(getIntent());
    }

    /** <br> permission. */

    private void requestPermission(int permissionCode, int type) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    downloadByType(type);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadByType(requestCode);
                    } else {
                        Toast.makeText(
                                this,
                                getString(R.string.feedback_need_permission),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public void downloadByType(int type) {
        switch (type) {
            case DownloadHelper.DOWNLOAD_TYPE:
                downloadPresenter.download();
                break;

            case DownloadHelper.SHARE_TYPE:
                downloadPresenter.share();
                break;

            case DownloadHelper.WALLPAPER_TYPE:
                downloadPresenter.setWallpaper();
                break;
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (browsablePresenter.isBrowsable()) {
                    browsablePresenter.visitParentView();
                }
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;

            case R.id.activity_photo_touchView:
                IntentHelper.startPreviewPhotoActivity(this, photoInfoPresenter.getPhoto());
                break;

            case R.id.activity_photo_avatar:
                photoInfoPresenter.touchAuthorAvatar();
                break;

            case R.id.activity_photo_downloadBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.download();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_TYPE);
                }
                break;

            case R.id.activity_photo_shareBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.share();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.SHARE_TYPE);
                }
                break;

            case R.id.activity_photo_wallBtn:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadPresenter.setWallpaper();
                } else {
                    requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.WALLPAPER_TYPE);
                }
                break;
        }
    }

    // on menu item click listener.

    private Toolbar.OnMenuItemClickListener scrollToolbarMenuListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:
                    ShareUtils.sharePhoto(photoInfoPresenter.getPhoto());
                    break;

                case R.id.action_menu:
                    popupManagePresenter.showPopup(PhotoActivity.this, toolbar, null, 0);
                    break;
            }
            return true;
        }
    };

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(scrollView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishActivity(dir);
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

    // photo info view.

    @Override
    public void touchAuthorAvatar() {
        IntentHelper.startUserActivity(
                this,
                avatarImage,
                photoInfoPresenter.getPhoto().user);
    }

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case PhotoMenuPopupWindow.ITEM_STATS:
                StatsDialog dialog = new StatsDialog();
                dialog.setPhoto(photoInfoPresenter.getPhoto());
                dialog.show(getFragmentManager(), null);
                break;

            case PhotoMenuPopupWindow.ITEM_DOWNLOAD_PAGE:
                Intent d = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(photoInfoPresenter.getPhoto().links.download));
                startActivity(d);
                break;
        }
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        ((NestedScrollView) findViewById(R.id.activity_photo_scrollView)).smoothScrollTo(0, 0);
    }

    @Override
    public void autoLoad(int dy) {
        // do nothing.
    }

    @Override
    public boolean needBackToTop() {
        return false;
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        photoInfoPresenter.touchMenuItem(position);
    }

    // browsable view.

    @Override
    public void showRequestDialog() {
        requestDialog = new RequestBrowsableDataDialog();
        requestDialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissRequestDialog() {
        requestDialog.dismiss();
        requestDialog = null;
    }

    @Override
    public void drawBrowsableView() {
        initModel();
        initPresenter();
        initView(false);
    }

    @Override
    public void visitParentView() {
        startActivity(new Intent(this, MainActivity.class));
    }
}