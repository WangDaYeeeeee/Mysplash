package com.wangdaye.mysplash.photo3.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
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
import com.wangdaye.mysplash.common.basic.activity.ReadWriteActivity;
import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.DownloadTypeDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.horizontalScrollView.ScalableImageView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.horizontalScrollView.SwipeSwitchLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.db.DatabaseHelper;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.mysplash.common.utils.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.mysplash.photo3.PhotoActivityModel;
import com.wangdaye.mysplash.photo3.PhotoListManagePresenter;
import com.wangdaye.mysplash.photo3.ui.holder.MoreHolder;
import com.wangdaye.mysplash.photo3.ui.holder.ProgressHolder;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Photo activity.
 *
 * This activity is used to show details of a photo.
 */

public class PhotoActivity3 extends ReadWriteActivity
        implements Toolbar.OnMenuItemClickListener, PhotoMenuPopupWindow.OnSelectItemListener,
        PhotoButtonBar.OnClickButtonListener, DownloadRepeatDialog.OnCheckOrDownloadListener,
        DownloadTypeDialog.OnSelectTypeListener, SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_photo_3_container) CoordinatorLayout container;
    @BindView(R.id.activity_photo_3_shadow) View shadow;

    @BindView(R.id.activity_photo_3_swipeSwitchView) SwipeSwitchLayout swipeSwitchView;
    @BindView(R.id.activity_photo_3_switchBackground) AppCompatImageView switchBackground;

    @BindView(R.id.activity_photo_3_regularImage) ScalableImageView regularImage;
    @BindView(R.id.activity_photo_3_scrollView) NestedScrollView scrollView;

    @BindView(R.id.container_photo_3_base_titleShadow) LinearLayout titleShadow;
    @BindView(R.id.container_photo_3_base_controlBar) LinearLayout controlBar;
    @BindView(R.id.container_photo_3_base_avatar) CircleImageView avatar;
    @OnClick(R.id.container_photo_3_base_avatar)
    void clickAvatar() {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            IntentHelper.startUserActivity(
                    this, avatar, controlBar,
                    activityModel.getResource().getValue().data.user, UserActivity.PAGE_PHOTO);
        }
    }
    @BindView(R.id.container_photo_3_base_titleBar) RelativeLayout titleBar;
    @BindView(R.id.container_photo_3_base_title) TextView title;
    @BindView(R.id.container_photo_3_base_subtitle) TextView subtitle;
    @BindView(R.id.container_photo_3_base_buttonBar) PhotoButtonBar buttonBar;

    @OnClick(R.id.container_photo_3_base_touch)
    void clickTouchView() {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            IntentHelper.startPreviewActivity(
                    this, activityModel.getResource().getValue().data, true);
        }
    }

    @BindView(R.id.activity_photo_3_card) CardView cardBackground;
    @BindView(R.id.activity_photo_3_recyclerView) RecyclerView recyclerView;
    @Nullable private PhotoInfoAdapter3 photoInfoAdapter;

    @BindView(R.id.activity_photo_3_appBar) LinearLayout appBar;
    @BindView(R.id.activity_photo_3_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_photo_3_statusBar) StatusBarView statusBar;

    private PhotoListManagePresenter photoListManagePresenter;
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;

    private PhotoActivityModel activityModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_LIST = "photo_activity_2_photo_list";
    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX = "photo_activity_2_photo_current_index";
    public static final String KEY_PHOTO_ACTIVITY_2_PHOTO_HEAD_INDEX = "photo_activity_2_photo_head_index";
    public static final String KEY_PHOTO_ACTIVITY_2_ID = "photo_activity_2_id";

    private static final int LANDSCAPE_MAX_WIDTH_DP = 580;

    @Nullable private String imagePhotoId;
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
        imagePhotoId = null;

        DisplayUtils.setStatusBarStyle(this, true);
        setContentView(R.layout.activity_photo_3);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageHelper.releaseImageView(regularImage);
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

        int currentIndex = photoListManagePresenter.getCurrentIndex();
        int intentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX, -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && currentIndex >= 0
                && currentIndex == intentIndex) {
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

    // init.

    private void initModel() {
        List<Photo> photoList = getIntent().getParcelableArrayListExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_LIST);
        int currentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_CURRENT_INDEX, -1);
        int headIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_2_PHOTO_HEAD_INDEX, -1);
        if (photoList == null) {
            photoList = new ArrayList<>();
            currentIndex = -1;
            headIndex = -1;
        }

        String photoId = getIntent().getStringExtra(KEY_PHOTO_ACTIVITY_2_ID);

        photoListManagePresenter = new PhotoListManagePresenter(photoList, currentIndex, headIndex);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoActivityModel.class);
        if (photoListManagePresenter.getPhoto() != null) {
            activityModel.init(
                    Resource.success(photoListManagePresenter.getPhoto()),
                    photoListManagePresenter.getPhoto().id);
        } else if (!TextUtils.isEmpty(photoId)) {
            activityModel.init(Resource.loading(null), photoId);
        } else {
            activityModel.init(Resource.loading(null), "0TFBD0R75n4");
        }
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ThemeManager.getInstance(this).isLightTheme()) {
            statusBar.setDarkerAlpha(StatusBarView.LIGHT_INIT_MASK_ALPHA);
        }

        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_photo_3_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        if (photoListManagePresenter.getCurrentIndex() > -1) {
            swipeSwitchView.setOnSwitchListener(
                    new OnSwitchListener(photoListManagePresenter.getCurrentIndex()));
        }
        swipeSwitchView.setScalableView(regularImage);

        if (DatabaseHelper.getInstance(this).readDownloadingEntityCount(activityModel.getPhotoId()) > 0) {
            this.setOnDownloadListener();
        }
        buttonBar.setOnClickButtonListener(this);

        OnScrollListener listener = new OnScrollListener();
        scrollView.setOnScrollChangeListener(listener);
        listener.onScrollChange(scrollView, 0, 0, 0, 0);

        findViewById(R.id.activity_photo_3_cardContainer)
                .setBackgroundColor(Color.argb((int) (0.25 * 255), 0, 0, 0));

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

        toolbar.setTitle("");
        if (isTheLowestLevel()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_photo_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                IntentHelper.startMainActivity(this);
            }
            finishSelf(true);
        });
        toolbar.setOnMenuItemClickListener(this);

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };

        // observe.

        activityModel.getResource().observe(this, resource -> {
            if (resource.data == null) {
                if (resource.status == Resource.Status.LOADING) {
                    browsableDialogMangePresenter.load(this);
                } else {
                    browsableDialogMangePresenter.error(this, () ->
                            activityModel.checkToRequestPhoto());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            for (int i = 0; i < photoListManagePresenter.getSize(); i++) {
                if (photoListManagePresenter.getPhotoList().get(i).id.equals(resource.data.id)) {
                    photoListManagePresenter.getPhotoList().set(i, resource.data);
                }
            }

            resetPhotoImage(resource.data);

            title.setText(resource.data.user.name);
            subtitle.setText(DisplayUtils.getDate(this, resource.data.created_at));
            ImageHelper.loadAvatar(this, avatar, resource.data.user, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(resource.data.user.username + "-1");
            }

            buttonBar.setState(resource.data);

            if (photoInfoAdapter != null
                    && !photoInfoAdapter.getPhoto().id.equals(resource.data.id)) {
                // switch photo.
                DisplayUtils.setStatusBarStyle(PhotoActivity3.this, true);
                DisplayUtils.setNavigationBarStyle(PhotoActivity3.this, true, true);
                statusBar.animToInitAlpha();

                scrollView.scrollTo(0, 0);
                scrollView.setOnScrollChangeListener(new OnScrollListener());
            }

            if (updateAdapter(resource.data) && photoInfoAdapter != null) {
                GridLayoutManager layoutManager = new GridLayoutManager(
                        this, photoInfoAdapter.getColumnCount());
                layoutManager.setSpanSizeLookup(
                        new PhotoInfoAdapter3.SpanSizeLookup(
                                photoInfoAdapter,
                                photoInfoAdapter.getColumnCount(),
                                DisplayUtils.isLandscape(this)));
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(photoInfoAdapter);
            }
        });

        // init animation.

        titleShadow.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);

        appBar.setAlpha(0F);
        AnimUtils.alphaInitShow(appBar, 350);

        AnimUtils.alphaInitShow(titleShadow, 350);
        AnimUtils.alphaInitShow(titleBar, 350);

        avatar.setScaleX(0);
        avatar.setScaleY(0);
        AnimUtils.animScale(avatar, 300, 350, 1);
    }

    private void resetPhotoImage(@NonNull Photo photo) {
        if (imagePhotoId == null || !imagePhotoId.equals(photo.id)) {
            imagePhotoId = photo.id;
            ImageHelper.loadRegularPhoto(this, regularImage, photo, null);
        }
    }

    /**
     * @return true : need to be set as the adapter for recycler view.
     * */
    private boolean updateAdapter(@Nullable Photo photo) {
        if (photo == null) {
            return false;
        }

        if (photoInfoAdapter == null) {
            int columnCount = DisplayUtils.isLandscape(this)
                    || DisplayUtils.isTabletDevice(this)
                    ? PhotoInfoAdapter3.COLUMN_COUNT_HORIZONTAL
                    : PhotoInfoAdapter3.COLUMN_COUNT_VERTICAL;
            photoInfoAdapter = new PhotoInfoAdapter3(this, photo, columnCount);
            return true;
        } else if (photoInfoAdapter.getPhoto().id.equals(photo.id)) {
            // update photo.
            if (!photoInfoAdapter.isComplete() && photo.complete) {
                // make photo complete.
                int oldCount = photoInfoAdapter.getItemCount() - 1;
                photoInfoAdapter.notifyItemRemoved(oldCount);
                photoInfoAdapter.updatePhoto(photo);
                photoInfoAdapter.notifyItemRangeInserted(oldCount, photoInfoAdapter.getItemCount());
            } else if (!photo.complete
                    && recyclerView.getLayoutManager() != null
                    && recyclerView.getLayoutManager() instanceof GridLayoutManager
                    && ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == 2) {
                ProgressHolder holder = (ProgressHolder) recyclerView.findViewHolderForAdapterPosition(2);
                if (holder != null) {
                    holder.setFailedState();
                }
            }
            return false;
        } else {
            // reset.
            photoInfoAdapter.reset(photo);
            return true;
        }
    }

    @Nullable
    private MoreHolder getMoreHolder() {
        if (photoInfoAdapter != null && photoInfoAdapter.isComplete()
                && recyclerView.getLayoutManager() != null) {
            int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (lastVisibleItemPosition == photoInfoAdapter.getRealItemCount() - 1) {
                RecyclerView.ViewHolder holder
                        = recyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
                if (holder instanceof MoreHolder) {
                    return (MoreHolder) holder;
                }
            }
        }
        return null;
    }

    public void initRefresh() {
        activityModel.checkToRequestPhoto();
    }

    // download.

    public void readyToDownload(int type) {
        readyToDownload(type, false);
    }

    public void readyToDownload(int type, boolean showTypeDialog) {
        Photo photo = Objects.requireNonNull(activityModel.getResource().getValue()).data;
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
                    downloadByType(photo, type);
                } else {
                    requestReadWritePermission(photo, type);
                }
            }
        }
    }

    public void downloadByType(Photo photo, int type) {
        DownloadHelper.getInstance(this).addMission(this, photo, type);
        buttonBar.setDownloadState(true, -1);
        setOnDownloadListener();
    }

    public void setOnDownloadListener() {
        if (listener == null) {
            Photo photo = Objects.requireNonNull(activityModel.getResource().getValue()).data;
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
    protected void requestReadWritePermissionSucceed(Downloadable downloadable, int requestCode) {
        downloadByType((Photo) downloadable, requestCode);
    }

    // interface.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (activityModel.getResource().getValue() != null
                        && activityModel.getResource().getValue().data != null) {
                    ShareUtils.sharePhoto(activityModel.getResource().getValue().data);
                }
                break;

            case R.id.action_menu:
                PhotoMenuPopupWindow popup = new PhotoMenuPopupWindow(this, toolbar);
                popup.setOnSelectItemListener(this);
                break;
        }
        return true;
    }

    // on select item listener.

    @Override
    public void onSelectItem(int id) {
        switch (id) {
            case PhotoMenuPopupWindow.ITEM_DOWNLOAD_PAGE: {
                Photo photo = Objects.requireNonNull(activityModel.getResource().getValue()).data;
                if (photo != null) {
                    IntentHelper.startWebActivity(this, photo.links.download);
                }
                break;
            }
            case PhotoMenuPopupWindow.ITEM_STORY_PAGE:
                Photo photo = Objects.requireNonNull(activityModel.getResource().getValue()).data;
                if (photo == null) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 1");
                } else if (photo.story == null) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 2");
                } else if (TextUtils.isEmpty(photo.story.image_url)) {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_story_is_null) + " - 3");
                } else {
                    IntentHelper.startWebActivity(this, photo.story.image_url);
                }
                break;
        }
    }

    // on click button listener.

    @Override
    public void onLikeButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            if (activityModel.getResource().getValue() != null
                    && activityModel.getResource().getValue().data != null) {
                activityModel.likeOrDislikePhoto(
                        !activityModel.getResource().getValue().data.liked_by_user);
            }
        } else {
            IntentHelper.startLoginActivity(this);
        }
    }

    @Override
    public void onCollectButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            if (activityModel.getResource().getValue() != null
                    && activityModel.getResource().getValue().data != null) {
                SelectCollectionDialog dialog = new SelectCollectionDialog();
                dialog.setPhotoAndListener(
                        activityModel.getResource().getValue().data,
                        new DispatchCollectionsChangedPresenter());
                dialog.show((this).getSupportFragmentManager(), null);
            }
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
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            IntentHelper.startCheckPhotoActivity(
                    this,
                    activityModel.getResource().getValue().data.id);
        }
    }

    @Override
    public void onDownload(Object obj) {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                downloadByType(activityModel.getResource().getValue().data, (Integer) obj);
            } else {
                requestReadWritePermission(activityModel.getResource().getValue().data, (Integer) obj);
            }
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
     */
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
            swipeSwitchView.setNestedScrollEnable(scrollY == 0);

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
                        DisplayUtils.setNavigationBarStyle(
                                PhotoActivity3.this, true, true);
                    }
                } else if (onlyDark) {
                    onlyDark = false;
                    DisplayUtils.setNavigationBarStyle(
                            PhotoActivity3.this, false, true);
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
            if (photoListManagePresenter.getPhoto() != null) {
                activityModel.setPhoto(photoListManagePresenter.getPhoto());
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
                                        true));
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
                                        false));
            }
        }
    }
}