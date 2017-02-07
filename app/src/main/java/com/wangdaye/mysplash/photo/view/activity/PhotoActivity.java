package com.wangdaye.mysplash.photo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
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
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.ThreadManager;
import com.wangdaye.mysplash._common.utils.widget.FlagRunnable;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.photo.model.activity.BorwsableObject;
import com.wangdaye.mysplash.photo.model.activity.DownloadObject;
import com.wangdaye.mysplash.photo.model.activity.PhotoInfoObject;
import com.wangdaye.mysplash.photo.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.MessageManageImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo.presenter.activity.ScrollImplementor;
import com.wangdaye.mysplash.photo.view.widget.PhotoDetailsView;
import com.wangdaye.mysplash._common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash.user.model.widget.ScrollObject;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Photo activity.
 * */

public class PhotoActivity extends MysplashActivity
        implements PhotoInfoView, ScrollView, PopupManageView, BrowsableView, MessageManageView,
        View.OnClickListener, DownloadRepeatDialog.OnCheckOrDownloadListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, SafeHandler.HandlerContainer {
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
    private PhotoDownloadView buttonBar;
    private PhotoDetailsView detailsView;

    private SafeHandler<PhotoActivity> handler;

    // presenter.
    private PhotoInfoPresenter photoInfoPresenter;
    private DownloadPresenter downloadPresenter;
    private ScrollPresenter scrollPresenter;
    private PopupManagePresenter popupManagePresenter;
    private BrowsablePresenter browsablePresenter;
    private MessageManagePresenter messageManagePresenter;

    // data
    public static final String KEY_PHOTO_ACTIVITY_PHOTO = "photo_activity_photo";
    public static final String KEY_PHOTO_ACTIVITY_ID = "photo_activity_id";

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
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
        browsablePresenter.cancelRequest();
        runnable.setRunning(false);
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

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photoInfoPresenter = new PhotoInfoImplementor(photoInfoModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.messageManagePresenter = new MessageManageImplementor(this);
    }

    /** <br> view. */

    // init.

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView(boolean init) {
        this.handler = new SafeHandler<>(this);

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
                    .centerCrop()
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

            this.buttonBar = (PhotoDownloadView) findViewById(R.id.activity_photo_btnBar);
            if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(photoInfoPresenter.getPhoto().id) > 0) {
                buttonBar.setProgressState();
                runnable.setRunning(true);
                ThreadManager.getInstance().execute(runnable);
            }
            buttonBar.setOnClickListener(this);

            this.detailsView = (PhotoDetailsView) findViewById(R.id.activity_photo_detailsView);
            detailsView.initMP(photoInfoPresenter.getPhoto());
            detailsView.requestPhotoDetails();

            AnimUtils.animInitShow(titleBar, 200);
            AnimUtils.animInitShow(buttonBar, 300);
            AnimUtils.animInitShow(detailsView, 400);
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.photoInfoModel = new PhotoInfoObject((Photo) getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO));
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.scrollModel = new ScrollObject();
        this.browsableModel = new BorwsableObject(getIntent());
    }

    // interface.

    public void readyToDownload(int type) {
        if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(photoInfoPresenter.getPhoto().id) > 0) {
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_download_repeat),
                    Snackbar.LENGTH_SHORT);
        } else if (FileUtils.isPhotoExists(this, photoInfoPresenter.getPhoto().id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownlaodKey(type);
            dialog.setOnCheckOrDownloadListener(this);
            dialog.show(getFragmentManager(), null);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                downloadByType(type);
            } else {
                requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, type);
            }
        }
    }

    public void downloadByType(int type) {
        switch (type) {
            case DownloadHelper.DOWNLOAD_TYPE:
                buttonBar.setProgressState();
                downloadPresenter.download();
                break;

            case DownloadHelper.SHARE_TYPE:
                buttonBar.setProgressState();
                downloadPresenter.share();
                break;

            case DownloadHelper.WALLPAPER_TYPE:
                buttonBar.setProgressState();
                downloadPresenter.setWallpaper();
                break;
        }
        runnable.setRunning(true);
        ThreadManager.getInstance().execute(runnable);
    }

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
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
                        NotificationUtils.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
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

            case R.id.container_download_downloadBtn:
                readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
                break;

            case R.id.container_download_shareBtn:
                readyToDownload(DownloadHelper.SHARE_TYPE);
                break;

            case R.id.container_download_wallBtn:
                readyToDownload(DownloadHelper.WALLPAPER_TYPE);
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

    // on check or download listener.

    @Override
    public void onCheck(Object obj) {
        IntentHelper.startCheckPhotoActivity(
                this,
                ((Photo) downloadPresenter.getDownloadKey()).id);
    }

    @Override
    public void onDownload(Object obj) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadByType((Integer) obj);
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, (Integer) obj);
        }
    }

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

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(this, message.what, message.obj);
    }

    // view.

    // photo info view.

    @Override
    public void touchAuthorAvatar() {
        IntentHelper.startUserActivity(
                this,
                avatarImage,
                photoInfoPresenter.getPhoto().user,
                UserActivity.PAGE_PHOTO);
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
                IntentHelper.startWebActivity(this, photoInfoPresenter.getPhoto().links.download);
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

    // message manage view.

    @Override
    public void sendMessage(int what, Object o) {
        handler.obtainMessage(what, o).sendToTarget();
    }

    @Override
    public void responseMessage(int what, Object o) {
        if (0 <= what && what <= 100) {
            buttonBar.setProcess(what);
        } else {
            runnable.setRunning(false);
            buttonBar.setButtonState();
        }
    }

    /** <br> inner class. */

    private FlagRunnable runnable = new FlagRunnable(true) {
        @Override
        public void run() {
            while (isRunning()) {
                DownloadMissionEntity entity = DatabaseHelper.getInstance(PhotoActivity.this)
                        .readDownloadingEntity(photoInfoPresenter.getPhoto().id);
                if (entity != null && entity.missionId != -1
                        && entity.result == DownloadMissionEntity.RESULT_DOWNLOADING) {
                    Cursor cursor = DownloadHelper.getInstance(PhotoActivity.this)
                            .getMissionCursor(entity.missionId);
                    if (cursor != null && cursor.getCount() != 0) {
                        if (DownloadHelper.isMissionSuccess(cursor)
                                || DownloadHelper.isMissionFailed(cursor)) {
                            messageManagePresenter.sendMessage(-1, null);
                        } else {
                            messageManagePresenter.sendMessage(
                                    (int) DownloadHelper.getMissionProcess(cursor),
                                    null);
                        }
                    } else {
                        messageManagePresenter.sendMessage(-1, null);
                    }
                } else {
                    messageManagePresenter.sendMessage(-1, null);
                }
                SystemClock.sleep(200);
            }
        }
    };
}