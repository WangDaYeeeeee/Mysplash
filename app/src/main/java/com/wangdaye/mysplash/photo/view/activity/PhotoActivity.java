package com.wangdaye.mysplash.photo.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.item.DownloadMission;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;
import com.wangdaye.mysplash.common.i.view.PhotoInfoView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.ui.dialog.StatsDialog;
import com.wangdaye.mysplash.common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.common._basic.FlagRunnable;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.photo.model.BorwsableObject;
import com.wangdaye.mysplash.photo.model.DownloadObject;
import com.wangdaye.mysplash.photo.model.PhotoInfoObject;
import com.wangdaye.mysplash.photo.presenter.BrowsableImplementor;
import com.wangdaye.mysplash.photo.presenter.DownloadImplementor;
import com.wangdaye.mysplash.photo.presenter.MessageManageImplementor;
import com.wangdaye.mysplash.photo.presenter.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo.presenter.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo.view.holder.BaseHolder;
import com.wangdaye.mysplash.photo.view.holder.BaseLandscapeHolder;
import com.wangdaye.mysplash.photo.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo.view.holder.ProgressHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Photo activity.
 *
 * This activity is used to show details of a photo.
 *
 * */

public class PhotoActivity extends ReadWriteActivity
        implements PhotoInfoView, PopupManageView, BrowsableView, MessageManageView,
        DownloadRepeatDialog.OnCheckOrDownloadListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SelectCollectionDialog.OnCollectionsChangedListener, SafeHandler.HandlerContainer {

    @BindView(R.id.activity_photo_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_photo_image)
    FreedomImageView photoImage;

    @BindView(R.id.activity_photo_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activity_photo_translucentStatusBar)
    StatusBarView translucentStatusBar;

    @BindView(R.id.activity_photo_statusBar)
    StatusBarView statusBar;

    private RequestBrowsableDataDialog requestDialog;
    private SafeHandler<PhotoActivity> handler;

    private PhotoInfoModel photoInfoModel;
    private PhotoInfoPresenter photoInfoPresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    private PopupManagePresenter popupManagePresenter;

    private BrowsableModel browsableModel;
    private BrowsablePresenter browsablePresenter;

    private MessageManagePresenter messageManagePresenter;

    public static final String KEY_PHOTO_ACTIVITY_PHOTO = "photo_activity_photo";
    public static final String KEY_PHOTO_ACTIVITY_ID = "photo_activity_id";

    /**
     * This runnable is used to poll download progress.
     * */
    private FlagRunnable progressRunnable = new FlagRunnable(false) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.setStatusBarStyle(this, true);
        setContentView(R.layout.activity_photo);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initView(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browsablePresenter.cancelRequest();
        photoInfoPresenter.cancelRequest();
        progressRunnable.setRunning(false);
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Photo);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Photo);
        }
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // do nothing.
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishActivity(int dir) {
        Intent result = new Intent();
        result.putExtra(
                KEY_PHOTO_ACTIVITY_PHOTO,
                getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO));
        setResult(RESULT_OK, result);

        recyclerView.setAlpha(0f);
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        this.photoInfoModel = new PhotoInfoObject(
                this, (Photo) getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO));
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.browsableModel = new BorwsableObject(getIntent());
    }

    private void initPresenter() {
        this.photoInfoPresenter = new PhotoInfoImplementor(photoInfoModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.messageManagePresenter = new MessageManageImplementor(this);
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView(boolean init) {
        this.handler = new SafeHandler<>(this);
        if (init && browsablePresenter.isBrowsable() && photoInfoPresenter.getPhoto() == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                    this, R.id.activity_photo_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            photoImage.setSize(
                    photoInfoPresenter.getPhoto().width, photoInfoPresenter.getPhoto().height);
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

            recyclerView.setAdapter(photoInfoPresenter.getAdapter());
            int columnCount;
            if (DisplayUtils.isLandscape(this)) {
                columnCount = 2;
            } else {
                columnCount = 1;
            }
            GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
            layoutManager.setSpanSizeLookup(
                    new PhotoInfoAdapter.SpanSizeLookup(
                            photoInfoPresenter.getAdapter(), columnCount));
            recyclerView.setLayoutManager(layoutManager);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                recyclerView.addOnScrollListener(new OnScrollListener((photoInfoPresenter.getPhoto())));
            } else {
                recyclerView.addOnScrollListener(new MScrollListener((photoInfoPresenter.getPhoto())));
            }

            statusBar.setAlpha(0f);

            if (!photoInfoPresenter.getPhoto().complete) {
                initRefresh();
            }
        }
    }

    // control.

    public void visitParentActivity() {
        browsablePresenter.visitPreviousPage();
    }

    public boolean isBrowsable() {
        return browsablePresenter.isBrowsable();
    }

    public Photo getPhoto() {
        return photoInfoPresenter.getPhoto();
    }

    // HTTP request.

    public void initRefresh() {
        photoInfoPresenter.requestPhoto(this);
    }

    public boolean isLoadFailed() {
        return photoInfoPresenter.isFailed();
    }

    // UI.

    @Nullable
    private PhotoDownloadView getPhotoDownloadView() {
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition <= 1 && 1 <= lastVisibleItemPosition) {
            PhotoInfoAdapter.ViewHolder holder
                    = (PhotoInfoAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(1);
            if (holder instanceof BaseHolder) {
                return ((BaseHolder) holder).getDownloadView();
            } else if (holder instanceof BaseLandscapeHolder) {
                return ((BaseLandscapeHolder) holder).getDownloadView();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    private ViewPager getMoreImageContainer() {
        if (!photoInfoPresenter.getAdapter().isComplete()) {
            return null;
        } else {
            int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (lastVisibleItemPosition == photoInfoPresenter.getAdapter().getItemCount() - 1) {
                RecyclerView.ViewHolder holder
                        = recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
                if (holder instanceof MoreHolder) {
                    return ((MoreHolder) holder).getViewPager();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public void showPopup(Context c, View anchor, String value, int position) {
        popupManagePresenter.showPopup(c, anchor, value, position);
    }

    // download.

    public void readyToDownload(int type) {
        if (DatabaseHelper.getInstance(this)
                .readDownloadingEntityCount(photoInfoPresenter.getPhoto().id) > 0) {
            NotificationHelper.showSnackbar(getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isPhotoExists(this, photoInfoPresenter.getPhoto().id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(type);
            dialog.setOnCheckOrDownloadListener(this);
            dialog.show(getFragmentManager(), null);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                downloadByType(type);
            } else {
                requestReadWritePermission(type);
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

    public void startCheckDownloadProgressThread() {
        if (!progressRunnable.isRunning()) {
            progressRunnable.setRunning(true);
            ThreadManager.getInstance().execute(progressRunnable);
        }
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        downloadByType(requestCode);
    }

    // interface.

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
            requestReadWritePermission((Integer) obj);
        }
    }

    // on scroll changed listener.

    /**
     * This listener is used to set footer image position and control the style of status bars.
     * */
    private class OnScrollListener extends RecyclerView.OnScrollListener {

        int scrollY;

        float statusBarHeight;
        float screenHeight;
        float footerHeight;
        float showFlowStatusBarTrigger;

        // life cycle.

        OnScrollListener(Photo photo) {
            statusBarHeight = DisplayUtils.getStatusBarHeight(getResources());
            screenHeight = getResources().getDisplayMetrics().heightPixels;
            footerHeight = getResources().getDimensionPixelSize(R.dimen.item_photo_more_vertical_height);

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float limitHeight = screenHeight
                    - getResources().getDimensionPixelSize(R.dimen.photo_info_base_view_height);

            if (DisplayUtils.isLandscape(PhotoActivity.this)) {
                showFlowStatusBarTrigger = getResources().getDisplayMetrics().heightPixels - statusBarHeight;
            } else {
                if (1.0 * photo.height / photo.width * screenWidth <= limitHeight) {
                    showFlowStatusBarTrigger = limitHeight - statusBarHeight;
                } else {
                    showFlowStatusBarTrigger = screenWidth * photo.height / photo.width - statusBarHeight;
                }
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
                translucentStatusBar.animToDarkerAlpha();
                AnimUtils.animShow(statusBar, 150, statusBar.getAlpha(), 1);
            } else if (scrollY - dy >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                translucentStatusBar.animToInitAlpha();
                AnimUtils.animHide(statusBar, 150, statusBar.getAlpha(), 0, false);
            }

            // more.
            ViewPager moreContainer = getMoreImageContainer();
            if (moreContainer != null) {
                moreContainer.setTranslationY(
                        (float) (0.5 * (recyclerView.getBottom()
                                - ((View) moreContainer.getParent()).getTop()
                                - footerHeight)));
            }
        }
    }

    /**
     * This listener is used to control the text color for status bar. Only for Android M.
     * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private class MScrollListener extends OnScrollListener {

        MScrollListener(Photo photo) {
            super(photo);
        }

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

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishActivity(dir);
    }

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        Photo photo = getPhoto();
        photo.current_user_collections.clear();
        photo.current_user_collections.addAll(p.current_user_collections);
        photoInfoPresenter.setPhoto(photo);
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(message.what, message.obj);
    }

    // view.

    // photo info view.

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case PhotoMenuPopupWindow.ITEM_LIKE:
                if (AuthManager.getInstance().isAuthorized()) {
                    photoInfoPresenter.setLikeForAPhoto(this);
                } else {
                    IntentHelper.startLoginActivity(this);
                }
                break;

            case PhotoMenuPopupWindow.ITEM_COLLECT: {
                if (!AuthManager.getInstance().isAuthorized()) {
                    IntentHelper.startLoginActivity(this);
                } else {
                    SelectCollectionDialog dialog = new SelectCollectionDialog();
                    dialog.setPhotoAndListener(getPhoto(), this);
                    dialog.show((this).getFragmentManager(), null);
                }
                break;
            }
            case PhotoMenuPopupWindow.ITEM_STATS: {
                StatsDialog dialog = new StatsDialog();
                dialog.setPhoto(photoInfoPresenter.getPhoto());
                dialog.show(getFragmentManager(), null);
                break;
            }
            case PhotoMenuPopupWindow.ITEM_DOWNLOAD_PAGE:
                IntentHelper.startWebActivity(this, photoInfoPresenter.getPhoto().links.download);
                break;

            case PhotoMenuPopupWindow.ITEM_STORY_PAGE:
                if (photoInfoPresenter.getPhoto() != null
                        && photoInfoPresenter.getPhoto().story != null
                        && !TextUtils.isEmpty(photoInfoPresenter.getPhoto().story.image_url)) {
                    IntentHelper.startWebActivity(this, photoInfoPresenter.getPhoto().story.image_url);
                } else {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null));
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
        if (((GridLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPosition() == 2) {
            ProgressHolder holder = (ProgressHolder) recyclerView.findViewHolderForAdapterPosition(2);
            holder.setFailedState();
        }
    }

    @Override
    public void setLikeForAPhotoCompleted() {
        List<MysplashPopupWindow> popupList = getPopupList();
        for (int i = 0; i < popupList.size(); i ++) {
            if (popupList.get(i) instanceof PhotoMenuPopupWindow) {
                ((PhotoMenuPopupWindow) popupList.get(i)).setLikeResult(this, getPhoto());
                return;
            }
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
    public void visitPreviousPage() {
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
                progressRunnable.setRunning(false);
                downloadView.setButtonState();
            }
        }
    }
}