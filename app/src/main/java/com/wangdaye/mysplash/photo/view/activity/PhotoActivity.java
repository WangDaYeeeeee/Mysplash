package com.wangdaye.mysplash.photo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash._common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash._common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.FileUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash._common.i.view.PhotoInfoView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash._common.ui.dialog.StatsDialog;
import com.wangdaye.mysplash._common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.ThreadManager;
import com.wangdaye.mysplash._common._basic.FlagRunnable;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.photo.model.BorwsableObject;
import com.wangdaye.mysplash.photo.model.DownloadObject;
import com.wangdaye.mysplash.photo.model.PhotoInfoObject;
import com.wangdaye.mysplash.photo.presenter.BrowsableImplementor;
import com.wangdaye.mysplash.photo.presenter.DownloadImplementor;
import com.wangdaye.mysplash.photo.presenter.MessageManageImplementor;
import com.wangdaye.mysplash.photo.presenter.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo.presenter.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo.view.holder.BaseHolder;
import com.wangdaye.mysplash.photo.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo.view.holder.ProgressHolder;

/**
 * Photo activity.
 * */

public class PhotoActivity extends MysplashActivity
        implements PhotoInfoView, PopupManageView, BrowsableView, MessageManageView,
        DownloadRepeatDialog.OnCheckOrDownloadListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SafeHandler.HandlerContainer {
    // model.
    private PhotoInfoModel photoInfoModel;
    private DownloadModel downloadModel;
    private BrowsableModel browsableModel;

    // view.
    private RequestBrowsableDataDialog requestDialog;

    private CoordinatorLayout container;
    private FreedomImageView photoImage;

    private RecyclerView recyclerView;

    private StatusBarView statusBar;
    private StatusBarView flowStatusBar;

    private SafeHandler<PhotoActivity> handler;

    // presenter.
    private PhotoInfoPresenter photoInfoPresenter;
    private DownloadPresenter downloadPresenter;
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
        DisplayUtils.setStatusBarStyle(this, true);
        setContentView(R.layout.activity_photo);
        initModel();
        initPresenter();
        runnable.setRunning(false);
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
        browsablePresenter.cancelRequest();
        photoInfoPresenter.cancelRequest();
        runnable.setRunning(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // do nothing.
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
        // do nothing.
    }

    @Override
    protected boolean isFullScreen() {
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
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.messageManagePresenter = new MessageManageImplementor(this);
    }

    /** <br> view. */

    // init.

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView(boolean init) {
        this.handler = new SafeHandler<>(this);

        if (init && browsablePresenter.isBrowsable() && photoInfoPresenter.getPhoto() == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            this.container = (CoordinatorLayout) findViewById(R.id.activity_photo_container);

            SwipeBackCoordinatorLayout swipeBackView
                    = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_photo_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            this.photoImage = (FreedomImageView) findViewById(R.id.activity_photo_image);
            photoImage.setSize(photoInfoPresenter.getPhoto().width, photoInfoPresenter.getPhoto().height);
            ImageHelper.loadRegularPhoto(
                    this, photoImage, photoInfoPresenter.getPhoto(),
                    new ImageHelper.OnLoadImageListener() {
                @Override
                public void onLoadSucceed() {
                    photoInfoPresenter.getPhoto().loadPhotoSuccess = true;
                    if (!photoInfoPresenter.getPhoto().hasFadedIn) {
                        photoInfoPresenter.getPhoto().hasFadedIn = true;
                        ImageHelper.startSaturationAnimation(PhotoActivity.this, photoImage);
                    }
                }

                @Override
                public void onLoadFailed() {
                    // do nothing.
                }
            });

            this.recyclerView = (RecyclerView) findViewById(R.id.activity_photo_recyclerView);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                recyclerView.addOnScrollListener(new OnScrollListener((photoInfoPresenter.getPhoto())));
            } else {
                recyclerView.addOnScrollListener(new MScrollListener((photoInfoPresenter.getPhoto())));
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(photoInfoPresenter.getAdapter());

            this.statusBar = (StatusBarView) findViewById(R.id.activity_photo_statusBar);
            statusBar.setAlpha(0.1f);

            this.flowStatusBar = (StatusBarView) findViewById(R.id.activity_photo_flowStatusBar);
            if (DisplayUtils.isNeedSetStatusBarMask()) {
                flowStatusBar.setBackgroundResource(R.color.colorPrimary_light);
                flowStatusBar.setMask(true);
            }
            flowStatusBar.setAlpha(0f);

            if (!photoInfoPresenter.getPhoto().complete) {
                initRefresh();
            }
        }
    }

    // interface.

    public void initRefresh() {
        photoInfoPresenter.requestPhoto(this);
    }

    public void visitParentActivity() {
        browsablePresenter.visitParentView();
    }

    public void showPopup(Context c, View anchor, String value, int position) {
        popupManagePresenter.showPopup(c, anchor, value, position);
    }

    @Nullable
    private PhotoDownloadView getPhotoDownloadView() {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition <= 1 && 1 <= lastVisibleItemPosition) {
            return ((BaseHolder) recyclerView.findViewHolderForAdapterPosition(1)).getDownloadView();
        } else {
            return null;
        }
    }

    @Nullable
    private ImageView getMoreImage() {
        if (!photoInfoPresenter.getAdapter().isComplete()) {
            return null;
        } else {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            if (lastVisibleItemPosition == photoInfoPresenter.getAdapter().getItemCount() - 1) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
                if (holder instanceof MoreHolder) {
                    return ((MoreHolder) holder).getImageView();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.photoInfoModel = new PhotoInfoObject(
                this, (Photo) getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO));
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.browsableModel = new BorwsableObject(getIntent());
    }

    // interface.

    public void readyToDownload(int type) {
        if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(photoInfoPresenter.getPhoto().id) > 0) {
            NotificationHelper.showSnackbar(
                    getString(R.string.feedback_download_repeat),
                    Snackbar.LENGTH_SHORT);
        } else if (FileUtils.isPhotoExists(this, photoInfoPresenter.getPhoto().id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(type);
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
                downloadPresenter.download(this);
                break;

            case DownloadHelper.SHARE_TYPE:
                downloadPresenter.share(this);
                break;

            case DownloadHelper.WALLPAPER_TYPE:
                downloadPresenter.setWallpaper(this);
                break;
        }
        PhotoDownloadView downloadView = getPhotoDownloadView();
        if (downloadView != null) {
            downloadView.setProgressState();
        }
        startCheckDownloadProgressThread();
    }

    public boolean isBrowsable() {
        return browsablePresenter.isBrowsable();
    }

    public void startCheckDownloadProgressThread() {
        if (!runnable.isRunning()) {
            runnable.setRunning(true);
            ThreadManager.getInstance().execute(runnable);
        }
    }

    public Photo getPhoto() {
        return photoInfoPresenter.getPhoto();
    }

    public boolean isLoadFailed() {
        return photoInfoPresenter.isFailed();
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
                        NotificationHelper.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on check or download swipeListener.

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

    // on scroll changed swipeListener.

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        // data.
        int scrollY;

        float statusBarHeight;
        float screenHeight;
        float footerHeight;
        float showFlowStatusBarTrigger;

        // life cycle.

        OnScrollListener(Photo photo) {
            DisplayUtils utils = new DisplayUtils(PhotoActivity.this);

            statusBarHeight = DisplayUtils.getStatusBarHeight(getResources());
            screenHeight = getResources().getDisplayMetrics().heightPixels;
            footerHeight = utils.dpToPx(72);

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float limitHeight = screenHeight - utils.dpToPx(300);

            if (1.0 * photo.height / photo.width * screenWidth <= limitHeight) {
                showFlowStatusBarTrigger = limitHeight - statusBarHeight;
            } else {
                showFlowStatusBarTrigger = screenWidth * photo.height / photo.width - statusBarHeight;
            }
        }

        // interface.

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollY += dy;

            // image.
            if (scrollY + statusBarHeight < photoImage.getMeasuredHeight()) {
                photoImage.setTranslationY((float) (-scrollY * 0.5));
            }

            // status bar & toolbar.
            if (scrollY - dy < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                AnimUtils.animHide(statusBar, 150, statusBar.getAlpha(), 0, false);
                AnimUtils.animShow(flowStatusBar, 150, flowStatusBar.getAlpha(), 1);
            } else if (scrollY - dy >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                AnimUtils.animHide(flowStatusBar, 150, flowStatusBar.getAlpha(), 0, false);
                AnimUtils.animShow(statusBar, 150, statusBar.getAlpha(), 0.1f);
            }

            // more.
            ImageView moreImage = getMoreImage();
            if (moreImage != null) {
                View moreButton = (View) moreImage.getParent();
                moreImage.setTranslationY(
                        (float) (0.5 * (recyclerView.getBottom() - moreButton.getTop() - footerHeight)));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private class MScrollListener extends OnScrollListener {

        // life cycle.

        MScrollListener(Photo photo) {
            super(photo);
        }

        // interface.

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (scrollY - dy < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                DisplayUtils.setStatusBarStyle(PhotoActivity.this, false);
            } else if (scrollY - dy >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                DisplayUtils.setStatusBarStyle(PhotoActivity.this, true);
            }
        }
    }

    // on swipe swipeListener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(recyclerView, dir);
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

            case PhotoMenuPopupWindow.ITEM_STORY_PAGE:
                if (photoInfoPresenter.getPhoto() != null
                        && photoInfoPresenter.getPhoto().story != null
                        && !TextUtils.isEmpty(photoInfoPresenter.getPhoto().story.image_url)) {
                    IntentHelper.startWebActivity(this, photoInfoPresenter.getPhoto().story.image_url);
                } else {
                    NotificationHelper.showSnackbar(
                            getString(R.string.feedback_story_is_null),
                            Snackbar.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    public void requestPhotoSuccess(Photo photo) {
        getIntent().putExtra(KEY_PHOTO_ACTIVITY_PHOTO, photo);

        int oldCount = photoInfoPresenter.getAdapter().getItemCount() - 1;

        photoInfoPresenter.getAdapter().notifyItemRemoved(oldCount);

        photoInfoPresenter.getAdapter().updatePhoto(photo);
        photoInfoPresenter.getAdapter().notifyItemRangeInserted(
                oldCount, photoInfoPresenter.getAdapter().getItemCount());
    }

    @Override
    public void requestPhotoFailed() {
        if (((LinearLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPosition() == 2) {
            ProgressHolder holder = (ProgressHolder) recyclerView.findViewHolderForAdapterPosition(2);
            holder.setFailedState();
        }
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
    public void drawBrowsableView(Object result) {
        getIntent().putExtra(KEY_PHOTO_ACTIVITY_PHOTO, (Photo) result);
        initModel();
        initPresenter();
        initView(false);
    }

    @Override
    public void visitParentView() {
        IntentHelper.startMainActivity(this);
    }

    // message manage view.

    @Override
    public void sendMessage(int what, Object o) {
        handler.obtainMessage(what, o).sendToTarget();
    }

    @Override
    public void responseMessage(int what, Object o) {
        PhotoDownloadView downloadView = getPhotoDownloadView();
        if (downloadView != null) {
            if (0 <= what && what <= 100) {
                downloadView.setProcess(what);
            } else {
                runnable.setRunning(false);
                downloadView.setButtonState();
            }
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
                        && entity.result == DownloadHelper.RESULT_DOWNLOADING) {
                    DownloadMission mission = DownloadHelper.getInstance(PhotoActivity.this)
                            .getDownloadMission(PhotoActivity.this, entity.missionId);
                    if (mission == null || mission.entity.result != DownloadHelper.RESULT_DOWNLOADING) {
                        messageManagePresenter.sendMessage(-1, null);
                    } else {
                        messageManagePresenter.sendMessage((int) mission.process, null);
                    }
                } else {
                    messageManagePresenter.sendMessage(-1, null);
                }
                SystemClock.sleep(200);
            }
        }
    };
}