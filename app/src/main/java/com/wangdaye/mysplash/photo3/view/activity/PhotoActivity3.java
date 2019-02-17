package com.wangdaye.mysplash.photo3.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.RequestLoadActivity;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.downloader.DownloaderService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel3;
import com.wangdaye.mysplash.common.i.model.PhotoListManageModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PhotoInfoPresenter3;
import com.wangdaye.mysplash.common.i.presenter.PhotoListManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;
import com.wangdaye.mysplash.common.i.view.PhotoInfoView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.DownloadTypeDialog;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.fullScreenView.FullScreenImageView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo3.model.BorwsableObject;
import com.wangdaye.mysplash.photo3.model.DownloadObject;
import com.wangdaye.mysplash.photo3.model.PhotoInfoObject;
import com.wangdaye.mysplash.photo3.model.PhotoListManageObject;
import com.wangdaye.mysplash.photo3.presenter.BrowsableImplementor;
import com.wangdaye.mysplash.photo3.presenter.DownloadImplementor;
import com.wangdaye.mysplash.photo3.presenter.PhotoActivityPopupManageImplementor;
import com.wangdaye.mysplash.photo3.presenter.PhotoInfoImplementor;
import com.wangdaye.mysplash.photo3.presenter.PhotoListManageImplementor;
import com.wangdaye.mysplash.photo3.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo3.view.holder.ProgressHolder;
import com.wangdaye.mysplash.photo3.view.widget.PhotoButtonBar;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Photo activity.
 *
 * This activity is used to show details of a photo.
 *
 * */

public class PhotoActivity3 extends RequestLoadActivity<Photo>
        implements PhotoInfoView, PopupManageView, BrowsableView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, PhotoButtonBar.OnClickButtonListener,
        DownloadRepeatDialog.OnCheckOrDownloadListener, DownloadTypeDialog.OnSelectTypeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.activity_photo_3_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_photo_3_shadow)
    View shadow;

    @BindView(R.id.activity_photo_3_swipeSwitchView)
    SwipeSwitchLayout swipeSwitchView;

    @BindView(R.id.activity_photo_3_switchBackground)
    AppCompatImageView switchBackground;

    @BindView(R.id.activity_photo_3_regularImage)
    FullScreenImageView regularImage;

    @BindView(R.id.activity_photo_3_scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.container_photo_3_base_titleShadow)
    LinearLayout titleShadow;

    @BindView(R.id.container_photo_3_base_controlBar)
    LinearLayout controlBar;

    @BindView(R.id.container_photo_3_base_avatar)
    CircleImageView avatar;

    @BindView(R.id.container_photo_3_base_titleBar)
    RelativeLayout titleBar;

    @BindView(R.id.container_photo_3_base_title)
    TextView title;

    @BindView(R.id.container_photo_3_base_subtitle)
    TextView subtitle;

    @BindView(R.id.container_photo_3_base_buttonBar)
    PhotoButtonBar buttonBar;

    @BindView(R.id.activity_photo_3_card)
    CardView cardBackground;

    @BindView(R.id.activity_photo_3_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activity_photo_3_appBar)
    LinearLayout appBar;

    @BindView(R.id.activity_photo_3_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_photo_3_statusBar)
    StatusBarView statusBar;

    private RequestBrowsableDataDialog requestDialog;

    private PhotoListManageModel photoListManageModel;
    private PhotoListManagePresenter photoListManagePresenter;

    private PhotoInfoModel3 photoInfoModel;
    private PhotoInfoPresenter3 photoInfoPresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    private PopupManagePresenter popupManagePresenter;

    private BrowsableModel browsableModel;
    private BrowsablePresenter browsablePresenter;

    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_LIST = "photo_activity_2_photo_list";
    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX = "photo_activity_2_photo_current_index";
    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_HEAD_INDEX = "photo_activity_2_photo_head_index";
    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_BUNDLE = "photo_activity_2_photo_bundle";
    public static final String KEY_PHOTO_ACTIVITY_2_ID = "photo_activity_2_id";

    private static final int LANDSCAPE_MAX_WIDTH_DP = 580;

    private OnDownloadListener listener;

    private class OnDownloadListener extends DownloaderService.OnDownloadListener {

        OnDownloadListener(DownloadMissionEntity entity) {
            super(entity.missionId, entity.getNotificationTitle(), entity.result);
        }

        @Override
        public void onProcess(float process) {
            buttonBar.setDownloadState(
                    true,
                    (int) Math.max(0, Math.min(process, 100)));
        }

        @Override
        public void onComplete(int result) {
            listener = null;
            buttonBar.setDownloadState(false, -1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }

        DisplayUtils.setStatusBarStyle(this, true);
        setContentView(R.layout.activity_photo_3);
        initModel(null);
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
        ImageHelper.releaseImageView(regularImage);
        browsablePresenter.cancelRequest();
        photoInfoPresenter.cancelRequest();
        if (listener != null) {
            DownloadHelper.getInstance(this).removeOnDownloadListener(listener);
            listener = null;
        }
    }

    @Override
    protected void setTheme() {
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
        DisplayUtils.setNavigationBarStyle(this, true, true);
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        // do nothing.
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        recyclerView.setAlpha(0f);
        if (!browsablePresenter.isBrowsable()
                && photoListManagePresenter.getCurrentIndex()
                == getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX, -1)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
            if (backPressed) {
                overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
            } else {
                overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
            }
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public void updateData(Photo photo) {
        for (int i = 0; i < photoListManagePresenter.getPhotoList().size(); i ++) {
            if (photoListManagePresenter.getPhotoList().get(i).id.equals(photo.id)) {
                photoListManagePresenter.getPhotoList().set(i, photo);
                if (i == photoListManagePresenter.getCurrentIndex() - photoListManagePresenter.getHeadIndex()) {
                    photoInfoPresenter.setPhoto(photoListManagePresenter.getPhoto(), false);
                    buttonBar.setState(photo);
                }
            }
        }
    }

    // init.

    private void initModel(@Nullable Photo photo) {
        List<Photo> photoList = getIntent().getParcelableArrayListExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_LIST);
        int currentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX, -1);
        int headIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_HEAD_INDEX, -1);
        String id = null;
        if (photoList == null) {
            photoList = new ArrayList<>();
            currentIndex = -1;
            headIndex = -1;
        } else {
            id = getIntent().getStringExtra(KEY_PHOTO_ACTIVITY_2_ID);
            if (!TextUtils.isEmpty(id)) {
                getIntent().putExtra(KEY_PHOTO_ACTIVITY_2_ID, "");
            }
        }

        int columnCount = DisplayUtils.isLandscape(this) || DisplayUtils.isTabletDevice(this)
                ? PhotoInfoAdapter3.COLUMN_COUNT_HORIZONTAL : PhotoInfoAdapter3.COLUMN_COUNT_VERTICAL;

        this.photoListManageModel = new PhotoListManageObject(photoList, currentIndex, headIndex);
        this.photoInfoModel = new PhotoInfoObject(
                this, photo == null ? photoListManageModel.getPhoto() : photo, columnCount);
        this.downloadModel = new DownloadObject(photoInfoModel.getPhoto());
        this.browsableModel = new BorwsableObject(getIntent());
        if (!TextUtils.isEmpty(id)) {
            getIntent().putExtra(KEY_PHOTO_ACTIVITY_2_ID, id);
        }
    }

    private void initPresenter() {
        this.photoListManagePresenter = new PhotoListManageImplementor(photoListManageModel);
        this.photoInfoPresenter = new PhotoInfoImplementor(photoInfoModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.popupManagePresenter = new PhotoActivityPopupManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView(boolean init) {
        if (init && /*browsablePresenter.isBrowsable() &&*/ photoInfoPresenter.getPhoto() == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ThemeManager.getInstance(this).isLightTheme()) {
                statusBar.setDarkerAlpha(StatusBarView.LIGHT_INIT_MASK_ALPHA);
            }

            SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_photo_3_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            if (photoListManagePresenter.getCurrentIndex() > -1) {
                swipeSwitchView.setOnSwitchListener(new OnSwitchListener(photoListManagePresenter.getCurrentIndex()));
            }

            resetPhotoImage(true);
            
            if (photoInfoPresenter.getPhoto() != null) {
                initBaseView(photoInfoPresenter.getPhoto());
            }

            OnScrollListener listener = new OnScrollListener();
            scrollView.setOnScrollChangeListener(listener);
            listener.onScrollChange(scrollView, 0, 0, 0, 0);

            findViewById(R.id.activity_photo_3_cardContainer)
                    .setBackgroundColor(Color.argb((int) (0.25 * 255), 0, 0, 0));

            PhotoInfoAdapter3 adapter = photoInfoPresenter.getAdapter();

            int marginHorizontal = 0;
            if (DisplayUtils.isLandscape(this)) {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                float density = getResources().getDisplayMetrics().density;
                int widthDp = (int) (screenWidth / density);
                if (widthDp > LANDSCAPE_MAX_WIDTH_DP) {
                    marginHorizontal = (int) new DisplayUtils(this)
                            .dpToPx((int) ((widthDp - LANDSCAPE_MAX_WIDTH_DP) * 0.5));
                }
            }
            if (marginHorizontal > 0) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardBackground.getLayoutParams();
                params.setMarginStart(marginHorizontal);
                params.setMarginEnd(marginHorizontal);
                cardBackground.setLayoutParams(params);
            }

            GridLayoutManager layoutManager = new GridLayoutManager(this, adapter.getColumnCount());
            layoutManager.setSpanSizeLookup(
                    new PhotoInfoAdapter3.SpanSizeLookup(
                            adapter,
                            adapter.getColumnCount(),
                            DisplayUtils.isLandscape(this)));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            toolbar.setTitle("");
            if (isTheLowestLevel()) {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
            }
            toolbar.inflateMenu(R.menu.activity_photo_toolbar);
            toolbar.setNavigationOnClickListener(this);
            toolbar.setOnMenuItemClickListener(this);

            if (photoInfoPresenter.getPhoto() != null
                    && !photoInfoPresenter.getPhoto().complete) {
                initRefresh();
            }
        }
    }
    
    private void initBaseView(Photo photo) {
        title.setText(photo.user.name);
        subtitle.setText(DisplayUtils.getDate(this, photo.created_at));

        ImageHelper.loadAvatar(this, avatar, photo.user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(photo.user.username + "-1");
        }

        buttonBar.setState(photo);
        if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(photo.id) > 0) {
            this.setOnDownloadListener();
        }
        buttonBar.setOnClickButtonListener(this);

        // execute init animation.

        titleShadow.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);

        AnimUtils.alphaInitShow(titleShadow, 350);
        AnimUtils.alphaInitShow(titleBar, 350);

        avatar.setScaleX(0);
        avatar.setScaleY(0);
        AnimUtils.animScale(avatar, 300, 350, 1);
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

    public void likePhoto() {
        photoInfoPresenter.setLikeForAPhoto(this);
        buttonBar.setLikeState(photoInfoPresenter.getPhoto());
    }

    public void collectPhoto() {
        SelectCollectionDialog dialog = new SelectCollectionDialog();
        dialog.setPhotoAndListener(getPhoto(), this);
        dialog.show((this).getSupportFragmentManager(), null);
    }

    // HTTP request.

    public void initRefresh() {
        photoInfoPresenter.requestPhoto(this);
    }

    public boolean isLoadFailed() {
        return photoInfoPresenter.isFailed();
    }

    // UI.

    private void resetPhotoImage(boolean init) {
        ImageHelper.releaseImageView(regularImage);
        ImageHelper.loadRegularPhoto(
                this, regularImage, photoInfoPresenter.getPhoto(), 0,
                new ImageHelper.OnLoadImageListener<Photo>() {
                    @Override
                    public void onLoadImageSucceed(Photo newT, int index) {
                        if (photoInfoPresenter.getPhoto() != null) {
                            photoInfoPresenter.getPhoto().updateLoadInformation(newT);
                        }
                    }

                    @Override
                    public void onLoadImageFailed(Photo originalT, int index) {
                        // do nothing.
                    }
                });

        if (init) {
            appBar.setAlpha(0F);
            AnimUtils.alphaInitShow(appBar, 350);
        }
    }

    @Nullable
    private MoreHolder getMoreHolder() {
        if (photoInfoPresenter.getAdapter().isComplete() && recyclerView.getLayoutManager() != null) {
            int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (lastVisibleItemPosition == photoInfoPresenter.getAdapter().getRealItemCount() - 1) {
                RecyclerView.ViewHolder holder
                        = recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
                if (holder instanceof MoreHolder) {
                    return (MoreHolder) holder;
                }
            }
        }
        return null;
    }

    public void showPopup(Context c, View anchor, String value, int position) {
        popupManagePresenter.showPopup(c, anchor, value, position);
    }

    // download.

    public void readyToDownload(int type) {
        readyToDownload(type, false);
    }

    public void readyToDownload(int type, boolean showTypeDialog) {
        Photo photo = photoInfoPresenter.getPhoto();
        if (photo != null) {
            if (showTypeDialog) {
                DownloadTypeDialog dialog = new DownloadTypeDialog();
                dialog.setOnSelectTypeListener(this);
                dialog.show(getSupportFragmentManager(), null);
            } else if (DatabaseHelper.getInstance(this)
                    .readDownloadingEntityCount(photo.id) > 0) {
                NotificationHelper.showSnackbar(getString(R.string.feedback_download_repeat));
            } else if (FileUtils.isPhotoExists(this, photo.id)) {
                DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                dialog.setDownloadKey(type);
                dialog.setOnCheckOrDownloadListener(this);
                dialog.show(getSupportFragmentManager(), null);
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    downloadByType(type);
                } else {
                    requestReadWritePermission(type);
                }
            }
        }
    }

    public void downloadByType(int type) {
        switch (type) {
            case DownloaderService.DOWNLOAD_TYPE:
                downloadPresenter.download(this);
                break;

            case DownloaderService.SHARE_TYPE:
                downloadPresenter.share(this);
                break;

            case DownloaderService.WALLPAPER_TYPE:
                downloadPresenter.setWallpaper(this);
                break;
        }
        buttonBar.setDownloadState(true, -1);
        setOnDownloadListener();
    }

    public void setOnDownloadListener() {
        if (listener == null) {
            Photo photo = photoInfoPresenter.getPhoto();
            if (photo != null) {
                DownloadMissionEntity entity = DatabaseHelper.getInstance(PhotoActivity3.this)
                        .readDownloadingEntity(photo.id);
                if (entity != null) {
                    listener = new OnDownloadListener(entity);
                    DownloadHelper.getInstance(this).addOnDownloadListener(listener);
                }
            }
        }
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        downloadByType(requestCode);
    }

    // interface.

    // on click listener.

    @OnClick(R.id.container_photo_3_base_touch) void clickTouchView() {
        if (photoInfoPresenter.getPhoto() != null) {
            IntentHelper.startPreviewActivity(
                    Mysplash.getInstance().getTopActivity(), photoInfoPresenter.getPhoto(), true);
        }
    }

    @OnClick(R.id.container_photo_3_base_avatar) void clickAvatar() {
        if (photoInfoPresenter.getPhoto() != null) {
            IntentHelper.startUserActivity(
                    Mysplash.getInstance().getTopActivity(),
                    avatar,
                    controlBar,
                    photoInfoPresenter.getPhoto().user,
                    UserActivity.PAGE_PHOTO);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                if (isTheLowestLevel()) {
                    visitParentActivity();
                }
                finishSelf(true);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                ShareUtils.sharePhoto(getPhoto());
                break;

            case R.id.action_menu:
                showPopup(this, toolbar, null, 0);
                break;
        }
        return true;
    }

    // on click button listener.

    @Override
    public void onLikeButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            likePhoto();
        } else {
            IntentHelper.startLoginActivity(this);
        }
    }

    @Override
    public void onCollectButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            collectPhoto();
        } else {
            IntentHelper.startLoginActivity(this);
        }
    }

    @Override
    public void onDownloadButtonClicked() {
        readyToDownload(DownloaderService.DOWNLOAD_TYPE, true);
    }

    @Override
    public void onDownloadButtonLongClicked() {
        readyToDownload(DownloaderService.DOWNLOAD_TYPE);
    }

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

    // on select type listener.

    @Override
    public void onSelectType(int type) {
        readyToDownload(type);
    }

    // on scroll changed listener.

    /**
     * This listener is used to set footer image position and control the style of status bars.
     * */
    private class OnScrollListener implements NestedScrollView.OnScrollChangeListener {
        
        boolean landscape;
        private int navigationBarHeight;
        
        float verticalFooterHeight;
        float showFlowStatusBarTrigger;
        float toolbarTranslationTrigger;

        boolean onlyDark;

        // life cycle.

        OnScrollListener() {
            landscape = DisplayUtils.isLandscape(PhotoActivity3.this);
            navigationBarHeight = DisplayUtils.getNavigationBarHeight(PhotoActivity3.this.getResources());
            
            verticalFooterHeight = getResources().getDimensionPixelSize(R.dimen.item_photo_3_more_vertical_height)
                    + DisplayUtils.getNavigationBarHeight(getResources());
            showFlowStatusBarTrigger = DisplayUtils.getScreenSize(PhotoActivity3.this)[1]
                    - DisplayUtils.getStatusBarHeight(getResources());
            toolbarTranslationTrigger = DisplayUtils.getScreenSize(PhotoActivity3.this)[1]
                    - DisplayUtils.getStatusBarHeight(getResources())
                    - new DisplayUtils(PhotoActivity3.this).dpToPx(56)
                    - getResources().getDimensionPixelSize(R.dimen.little_icon_size)
                    - 2 * getResources().getDimensionPixelSize(R.dimen.normal_margin);
            onlyDark = true;
        }

        // interface.

        @Override
        public void onScrollChange(NestedScrollView nestedScrollView, 
                                   int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            // base holder.
            if (!landscape && scrollY < navigationBarHeight) {
                titleShadow.setTranslationY(scrollY);
                controlBar.setTranslationY(scrollY - navigationBarHeight);
            } else {
                if (controlBar.getTranslationY() != 0) {
                    controlBar.setTranslationY(0);
                }
                if (titleShadow.getTranslationY() != navigationBarHeight) {
                    titleShadow.setTranslationY(navigationBarHeight);
                }
            }

            // toolbar.
            if (scrollY > toolbarTranslationTrigger) {
                appBar.setTranslationY(toolbarTranslationTrigger - scrollY);
            } else if (appBar.getTranslationY() != 0) {
                appBar.setTranslationY(0);
            }

            // status bar & navigation bar.
            if (oldScrollY < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                statusBar.animToDarkerAlpha();
            } else if (oldScrollY >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                statusBar.animToInitAlpha();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (oldScrollY < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                    DisplayUtils.setStatusBarStyle(PhotoActivity3.this, false);
                } else if (oldScrollY >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                    DisplayUtils.setStatusBarStyle(PhotoActivity3.this, true);
                }
                if (!recyclerView.canScrollVertically(-1) || (getMoreHolder() != null)) {
                    if (!onlyDark) {
                        onlyDark = true;
                        DisplayUtils.setNavigationBarStyle(PhotoActivity3.this, true, true);
                    }
                } else if (onlyDark) {
                    onlyDark = false;
                    DisplayUtils.setNavigationBarStyle(PhotoActivity3.this, false, true);
                }
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
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }

    // on switch listener (swipe switch layout).

    private class OnSwitchListener implements SwipeSwitchLayout.OnSwitchListener {

        private int currentIndex;
        private int targetIndex;

        OnSwitchListener(int index) {
            this.currentIndex = index;
            this.targetIndex = index;
        }

        @Override
        public void onSwipe(int direction, float progress) {
            if (targetIndex != currentIndex + direction) {
                targetIndex = currentIndex + direction;
                if (canSwitch(direction)) {
                    ImageHelper.loadBackgroundPhoto(
                            PhotoActivity3.this,
                            switchBackground,
                            photoListManagePresenter
                                    .getPhotoList()
                                    .get(targetIndex - photoListManagePresenter.getHeadIndex()));
                    switchBackground.setBackgroundColor(
                            ImageHelper.computeCardBackgroundColor(
                                    PhotoActivity3.this,
                                    photoListManagePresenter
                                            .getPhotoList()
                                            .get(targetIndex - photoListManagePresenter.getHeadIndex())
                                            .color));
                } else {
                    ImageHelper.releaseImageView(switchBackground);
                    switchBackground.setBackgroundColor(Color.BLACK);
                }
            }
            switchBackground.setAlpha((float) (progress * 0.5));
        }

        @Override
        public boolean canSwitch(int direction) {
            int newIndex = photoListManagePresenter.getCurrentIndex() - photoListManagePresenter.getHeadIndex() + direction;
            return 0 <= newIndex && newIndex < photoListManagePresenter.getPhotoList().size();
        }

        @Override
        public void onSwitch(int direction) {
            this.currentIndex += direction;
            this.targetIndex = currentIndex;

            photoListManagePresenter.setCurrentIndex(currentIndex);
            photoInfoPresenter.setPhoto(photoListManagePresenter.getPhoto(), true);

            DisplayUtils.setStatusBarStyle(PhotoActivity3.this, true);
            DisplayUtils.setNavigationBarStyle(PhotoActivity3.this, true, true);
            statusBar.animToInitAlpha();

            Photo photo = photoInfoPresenter.getPhoto();
            resetPhotoImage(false);
            if (photo != null) {
                initBaseView(photo);
            }

            photoInfoPresenter.cancelRequest();

            scrollView.scrollTo(0, 0);
            scrollView.setOnScrollChangeListener(new OnScrollListener());
            photoInfoPresenter.getAdapter().reset(photoInfoPresenter.getPhoto());
            recyclerView.setAdapter(photoInfoPresenter.getAdapter());

            if (photo != null && !photo.complete) {
                initRefresh();
            }

            if ((direction == SwipeSwitchLayout.DIRECTION_LEFT
                    && currentIndex - photoListManagePresenter.getHeadIndex() <= 10)) {
                int oldSize = photoListManagePresenter.getPhotoList().size();
                photoListManagePresenter.getPhotoList().addAll(
                        0,
                        Mysplash.getInstance()
                                .loadMorePhotos(
                                        PhotoActivity3.this,
                                        photoListManagePresenter.getPhotoList(),
                                        photoListManagePresenter.getHeadIndex(),
                                        true,
                                        getIntent().getBundleExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_BUNDLE)));
                photoListManagePresenter.setHeadIndex(
                        photoListManagePresenter.getHeadIndex()
                                - (photoListManagePresenter.getPhotoList().size() - oldSize));
            } else if (direction == SwipeSwitchLayout.DIRECTION_RIGHT
                    && photoListManagePresenter.getTailIndex() - currentIndex <= 10) {
                photoListManagePresenter.getPhotoList().addAll(
                        Mysplash.getInstance()
                                .loadMorePhotos(
                                        PhotoActivity3.this,
                                        photoListManagePresenter.getPhotoList(),
                                        photoListManagePresenter.getHeadIndex(),
                                        false,
                                        getIntent().getBundleExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_BUNDLE)));
            }
        }
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
        photoInfoPresenter.setPhoto(photo, false);

        buttonBar.setCollectState(photo);

        Mysplash.getInstance().dispatchPhotoUpdate(this, photo);
    }

    // view.

    // photo info view.

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case PhotoMenuPopupWindow.ITEM_DOWNLOAD_PAGE:
                Photo photo = photoInfoPresenter.getPhoto();
                if (photo != null) {
                    IntentHelper.startWebActivity(this, photo.links.download);
                }
                break;

            case PhotoMenuPopupWindow.ITEM_STORY_PAGE:
                if (photoInfoPresenter.getPhoto() == null) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 1");
                } else if (photoInfoPresenter.getPhoto().story == null) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 2");
                } else if (TextUtils.isEmpty(photoInfoPresenter.getPhoto().story.image_url)) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 3");
                } else {
                    IntentHelper.startWebActivity(this, photoInfoPresenter.getPhoto().story.image_url);
                }
                break;
        }
    }

    @Override
    public void requestPhotoSuccess(Photo photo) {
        Photo old = photoInfoPresenter.getPhoto();
        if (old != null && photo != null
                && photo.id.equals(old.id)) {
            int oldCount = photoInfoPresenter.getAdapter().getItemCount() - 1;

            photoInfoPresenter.getAdapter().notifyItemRemoved(oldCount);

            photoInfoPresenter.getAdapter().updatePhoto(photo);
            photoInfoPresenter.getAdapter().notifyItemRangeInserted(
                    oldCount, photoInfoPresenter.getAdapter().getItemCount());

            buttonBar.setState(photo);

            Mysplash.getInstance().dispatchPhotoUpdate(this, photo);
        }
    }

    @Override
    public void requestPhotoFailed() {
        if (recyclerView.getLayoutManager() != null
                && recyclerView.getLayoutManager() instanceof GridLayoutManager
                && ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == 2) {
            ProgressHolder holder = (ProgressHolder) recyclerView.findViewHolderForAdapterPosition(2);
            if (holder != null) {
                holder.setFailedState();
            }
        }
    }

    @Override
    public void setLikeForAPhotoCompleted(Photo photo, boolean succeed) {
        /*
        List<MysplashPopupWindow> popupList = getPopupList();
        for (int i = 0; i < popupList.size(); i ++) {
            if (popupList.get(i) instanceof PhotoMenuPopupWindow) {
                ((PhotoMenuPopupWindow) popupList.get(i)).setLikeResult(this, getPhoto());
                return;
            }
        }
        */
        buttonBar.setLikeState(photo);
        if (succeed) {
            Mysplash.getInstance().dispatchPhotoUpdate(this, photo);
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
        requestDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void dismissRequestDialog() {
        if (requestDialog != null) {
            requestDialog.dismiss();
            requestDialog = null;
        }
    }

    @Override
    public void drawBrowsableView(Object result) {
        initModel((Photo) result);
        initPresenter();
        initView(false);
    }

    @Override
    public void visitPreviousPage() {
        IntentHelper.startMainActivity(this);
    }
}