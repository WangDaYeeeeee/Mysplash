package com.wangdaye.photo.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.VerticalNestedScrollView;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.common.ui.widget.longPressDrag.LongPressDragHorizontalScrollableImageView;
import com.wangdaye.common.ui.widget.windowInsets.ApplyWindowInsetsLayout;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.ShareUtils;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.common.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.di.component.DaggerApplicationComponent;
import com.wangdaye.photo.ui.dialog.DownloadTypeDialog;
import com.wangdaye.photo.vm.PhotoActivityModel;
import com.wangdaye.photo.base.PhotoListManagePresenter;
import com.wangdaye.photo.R;
import com.wangdaye.photo.ui.PhotoButtonBar;
import com.wangdaye.photo.ui.PhotoMenuPopupWindow;
import com.wangdaye.photo.ui.TouchEventTransmitterView;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.photo.ui.holder.MoreHolder;
import com.wangdaye.photo.ui.holder.ProgressHolder;

import java.util.ArrayList;
import java.util.Arrays;
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

@Route(path = PhotoActivity.PHOTO_ACTIVITY)
public class PhotoActivity extends ReadWriteActivity
        implements Toolbar.OnMenuItemClickListener, PhotoMenuPopupWindow.OnSelectItemListener,
        PhotoButtonBar.OnClickButtonListener, DownloadRepeatDialog.OnCheckOrDownloadListener,
        DownloadTypeDialog.OnSelectTypeListener, SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_photo_3_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_photo_3_container) CoordinatorLayout container;
    @BindView(R2.id.activity_photo_3_shadow) View shadow;

    @BindView(R2.id.activity_photo_3_swipeSwitchView) SwipeSwitchLayout swipeSwitchView;
    @BindView(R2.id.activity_photo_3_switchBackground) AppCompatImageView switchBackground;

    @BindView(R2.id.activity_photo_3_regularImage) LongPressDragHorizontalScrollableImageView regularImage;
    @OnClick(R2.id.activity_photo_3_regularImage)
    void clickTouchView() {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            ComponentFactory.getPhotoModule().startPreviewActivity(
                    this, activityModel.getResource().getValue().data, true);
        }
    }

    @BindView(R2.id.activity_photo_3_touchEventTransmitter)
    TouchEventTransmitterView touchEventTransmitter;

    @BindView(R2.id.container_photo_3_actor_container) LinearLayout actorContainer;
    @BindView(R2.id.container_photo_3_actor_controlBar) LinearLayout actorControlBar;
    @BindView(R2.id.container_photo_3_actor_avatar) CircularImageView avatar;
    @OnClick(R2.id.container_photo_3_actor_avatar)
    void clickAvatar() {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            ComponentFactory.getUserModule().startUserActivity(
                    this,
                    avatar,
                    actorControlBar,
                    activityModel.getResource().getValue().data.user,
                    ProfilePager.PAGE_PHOTO
            );
        }
    }
    @BindView(R2.id.container_photo_3_actor_title) TextView title;
    @BindView(R2.id.container_photo_3_actor_subtitle) TextView subtitle;
    @BindView(R2.id.container_photo_3_actor_buttonBar) PhotoButtonBar buttonBar;

    @BindView(R2.id.activity_photo_3_scrollView) VerticalNestedScrollView scrollView;
    @BindView(R2.id.activity_photo_3_card) CardView cardBackground;
    @BindView(R2.id.activity_photo_3_recyclerView) RecyclerView recyclerView;
    @Nullable private PhotoInfoAdapter3 photoInfoAdapter;

    @BindView(R2.id.activity_photo_3_appBar) LinearLayout appBar;
    @BindView(R2.id.activity_photo_3_toolbar) Toolbar toolbar;
    @BindView(R2.id.activity_photo_3_statusBar) StatusBarView statusBar;

    private PhotoListManagePresenter photoListManagePresenter;
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;

    private PhotoActivityModel activityModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    private boolean hasCardMargin;
    private int cardRadius;
    private static final int LANDSCAPE_MAX_WIDTH_DP = 580;

    @Nullable private String imagePhotoId;

    public static final String PHOTO_ACTIVITY = "/photo/PhotoActivity";
    public static final String KEY_PHOTO_ACTIVITY_PHOTO_LIST = "photo_activity_2_photo_list";
    public static final String KEY_PHOTO_ACTIVITY_PHOTO_CURRENT_INDEX = "photo_activity_2_photo_current_index";
    public static final String KEY_PHOTO_ACTIVITY_PHOTO_HEAD_INDEX = "photo_activity_2_photo_head_index";
    public static final String KEY_PHOTO_ACTIVITY_ID = "photo_activity_2_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);

        imagePhotoId = null;

        setContentView(R.layout.activity_photo_3);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ApplyWindowInsetsLayout applyWindowInsetsLayout = getApplyWindowInsetsLayout();
        if (applyWindowInsetsLayout != null) {
            applyWindowInsetsLayout.setOnApplyWindowInsetsListener(windowInsets -> {
                MysplashApplication.getInstance().setWindowInsets(
                        windowInsets.left, windowInsets.top, windowInsets.right, windowInsets.bottom);
                setWindowInsets(
                        windowInsets.left, windowInsets.top, windowInsets.right, windowInsets.bottom);

                DisplayUtils.setStatusBarStyle(this, true);
                DisplayUtils.setNavigationBarStyle(
                        this, true, hasTranslucentNavigationBar());
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageHelper.releaseImageView(regularImage);
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
        int intentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_PHOTO_CURRENT_INDEX, -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && regularImage.isAlignCenter()
                && currentIndex >= 0
                && currentIndex == intentIndex) {
            finishAfterTransition();
        } else {
            finish();
            if (backPressed) {
                // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
            } else {
                overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
            }
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        List<Photo> photoList = getIntent().getParcelableArrayListExtra(KEY_PHOTO_ACTIVITY_PHOTO_LIST);
        int currentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_PHOTO_CURRENT_INDEX, -1);
        int headIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_PHOTO_HEAD_INDEX, -1);
        if (photoList == null) {
            photoList = new ArrayList<>();
            currentIndex = -1;
            headIndex = -1;
        }

        String photoId = getIntent().getStringExtra(KEY_PHOTO_ACTIVITY_ID);

        photoListManagePresenter = new PhotoListManagePresenter(photoList, currentIndex, headIndex);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoActivityModel.class);
        if (photoListManagePresenter.getPhoto() != null) {
            activityModel.init(
                    Resource.success(photoListManagePresenter.getPhoto()),
                    photoListManagePresenter.getPhoto().id
            );
        } else if (!TextUtils.isEmpty(photoId)) {
            activityModel.init(Resource.loading(null), photoId);
        } else {
            activityModel.init(Resource.loading(null), "0TFBD0R75n4");
        }
    }

    private void initView() {
        DisplayUtils utils = new DisplayUtils(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ThemeManager.getInstance(this).isLightTheme()) {
            statusBar.setDarkerAlpha(StatusBarView.LIGHT_INIT_MASK_ALPHA);
        }

        swipeBackView.setOnSwipeListener(this);

        if (photoListManagePresenter.getCurrentIndex() > -1) {
            swipeSwitchView.setOnSwitchListener(
                    new OnSwitchListener(photoListManagePresenter.getCurrentIndex())
            );
        }

        regularImage.setLongPressDragChildList(
                Arrays.asList(
                        avatar,
                        buttonBar.getDownloadButton(),
                        buttonBar.getCollectButton(),
                        buttonBar.getLikeButton()
                )
        );
        regularImage.post(() -> regularImage.setCancelFlagMarginTop(
                MysplashApplication.getInstance().getWindowInsets().top
                        + getResources().getDimensionPixelSize(R.dimen.normal_margin)
        ));
        touchEventTransmitter.setTarget(regularImage);

        buttonBar.setOnClickButtonListener(this);

        scrollView.setOnScrollChangeListener(new OnScrollListener());

        int marginHorizontal = 0;
        if (DisplayUtils.isLandscape(this)) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            float density = getResources().getDisplayMetrics().density;
            int widthDp = (int) (screenWidth / density);
            if (widthDp > LANDSCAPE_MAX_WIDTH_DP) {
                marginHorizontal = (int) utils.dpToPx((int) ((widthDp - LANDSCAPE_MAX_WIDTH_DP) * 0.5));
            }
        }
        hasCardMargin = marginHorizontal > 0;
        if (marginHorizontal > 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardBackground.getLayoutParams();
            params.setMarginStart(marginHorizontal);
            params.setMarginEnd(marginHorizontal);
            cardBackground.setLayoutParams(params);
        }

        cardRadius = getResources().getDimensionPixelSize(R.dimen.material_card_radius_large);
        actorContainer.setTranslationY(hasCardMargin ? 0 : cardRadius);

        toolbar.setTitle("");
        if (isTheLowestLevel()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_photo_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
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
                            activityModel.checkToRequestPhoto()
                    );
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
                DisplayUtils.setStatusBarStyle(this, true);
                DisplayUtils.setNavigationBarStyle(
                        this, true, hasTranslucentNavigationBar());
                statusBar.switchToInitAlpha();

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
                                DisplayUtils.isLandscape(this)
                        )
                );
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(photoInfoAdapter);
            }
        });

        // init animation.

        appBar.setAlpha(0F);
        AnimUtils.alphaInitShow(appBar, 350);

        actorContainer.setAlpha(0);
        AnimUtils.alphaInitShow(actorContainer, 350);

        avatar.setVisibility(View.GONE);
        avatar.setScaleX(0);
        avatar.setScaleY(0);
        AnimUtils.animScale(avatar, 300, 350, 1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void resetPhotoImage(@NonNull Photo photo) {
        if (imagePhotoId == null || !imagePhotoId.equals(photo.id)) {
            imagePhotoId = photo.id;
            ImageHelper.loadRegularPhoto(this, regularImage, photo, false, null);
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
            } else if (ComponentFactory.getDownloaderService().isDownloading(this, photo.id)) {
                NotificationHelper.showSnackbar(this, getString(R.string.feedback_download_repeat));
            } else if (FileUtils.isPhotoExists(this, photo.id)) {
                DownloadRepeatDialog dialog = new DownloadRepeatDialog();
                dialog.setDownloadKey(type);
                dialog.setOnCheckOrDownloadListener(this);
                dialog.show(getSupportFragmentManager(), null);
            } else {
                requestReadWritePermission(photo, new RequestPermissionCallback() {
                    @Override
                    public void onGranted(Downloadable downloadable) {
                        downloadByType(photo, type);
                    }

                    @Override
                    public void onDenied(Downloadable downloadable) {
                        NotificationHelper.showSnackbar(
                                PhotoActivity.this, getString(R.string.feedback_need_permission));
                    }
                });
            }
        }
    }

    public void downloadByType(Photo photo, int type) {
        ComponentFactory.getDownloaderService().addTask(
                this, photo, type, ComponentFactory.getSettingsService().getDownloadScale());
    }

    // interface.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            if (activityModel.getResource().getValue() != null
                    && activityModel.getResource().getValue().data != null) {
                ShareUtils.sharePhoto(activityModel.getResource().getValue().data);
            }
        } else if (i == R.id.action_menu) {
            PhotoMenuPopupWindow popup = new PhotoMenuPopupWindow(this, toolbar);
            popup.setOnSelectItemListener(this);
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
                    RoutingHelper.startWebActivity(this, photo.links.download);
                }
                break;
            }
            case PhotoMenuPopupWindow.ITEM_STORY_PAGE:
                Photo photo = Objects.requireNonNull(activityModel.getResource().getValue()).data;
                if (photo == null) {
                    NotificationHelper.showSnackbar(this, getString(R.string.feedback_story_is_null) + " - 1");
                } else if (photo.story == null) {
                    NotificationHelper.showSnackbar(this, getString(R.string.feedback_story_is_null) + " - 2");
                } else if (TextUtils.isEmpty(photo.story.image_url)) {
                    NotificationHelper.showSnackbar(this, getString(R.string.feedback_story_is_null) + " - 3");
                } else {
                    RoutingHelper.startWebActivity(this, photo.story.image_url);
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
            ComponentFactory.getMeModule().startLoginActivity(this);
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
                        new DispatchCollectionsChangedPresenter()
                );
                dialog.show((this).getSupportFragmentManager(), null);
            }
        } else {
            ComponentFactory.getMeModule().startLoginActivity(this);
        }
    }

    @Override
    public void onDownloadButtonClicked() {
        readyToDownload(DownloadTask.DOWNLOAD_TYPE, true);
    }

    @Override
    public void onDownloadButtonLongClicked() {
        readyToDownload(DownloadTask.DOWNLOAD_TYPE);
    }

    // on check or download listener.

    @Override
    public void onCheck(Object obj) {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            RoutingHelper.startCheckPhotoActivity(
                    this,
                    activityModel.getResource().getValue().data.id);
        }
    }

    @Override
    public void onDownload(Object obj) {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            requestReadWritePermission(activityModel.getResource().getValue().data,
                    new RequestPermissionCallback() {
                @Override
                public void onGranted(Downloadable downloadable) {
                    downloadByType((Photo) downloadable, (Integer) obj);
                }

                @Override
                public void onDenied(Downloadable downloadable) {
                    NotificationHelper.showSnackbar(
                            PhotoActivity.this, getString(R.string.feedback_need_permission));
                }
            });
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

        private int navigationBarHeight;

        float verticalFooterHeight;
        float showFlowStatusBarTrigger;
        float toolbarTranslationTrigger;

        boolean onlyDark;

        // life cycle.

        OnScrollListener() {
            Rect windowInsets = MysplashApplication.getInstance().getWindowInsets();

            navigationBarHeight = MysplashApplication.getInstance().getWindowInsets().bottom;

            verticalFooterHeight = getResources().getDimensionPixelSize(R.dimen.item_photo_3_more_vertical_height)
                    + windowInsets.bottom;

            showFlowStatusBarTrigger = DisplayUtils.getScreenSize(PhotoActivity.this)[1]
                    - windowInsets.top;

            toolbarTranslationTrigger = DisplayUtils.getScreenSize(PhotoActivity.this)[1]
                    - windowInsets.top
                    - new DisplayUtils(PhotoActivity.this).dpToPx(56)
                    - getResources().getDimensionPixelSize(R.dimen.little_icon_size)
                    - 2 * getResources().getDimensionPixelSize(R.dimen.normal_margin);

            onlyDark = true;
        }

        // interface.

        @Override
        public void onScrollChange(NestedScrollView nestedScrollView,
                                   int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            // photo.
            // regularImage.setTranslationY(scrollY);

            // base holder.
            if (hasCardMargin) {
                actorContainer.setTranslationY(scrollY);
            } else if (scrollY < navigationBarHeight) {
                actorContainer.setTranslationY(scrollY + cardRadius);
            } else if (actorContainer.getTranslationY() != navigationBarHeight + cardRadius) {
                actorContainer.setTranslationY(navigationBarHeight + cardRadius);
            }

            // toolbar.
            if (scrollY > toolbarTranslationTrigger) {
                appBar.setTranslationY(toolbarTranslationTrigger - scrollY);
            } else if (appBar.getTranslationY() != 0) {
                appBar.setTranslationY(0);
            }

            // status bar & navigation bar.
            if (oldScrollY < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                statusBar.switchToDarkerAlpha();
            } else if (oldScrollY >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                statusBar.switchToInitAlpha();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (oldScrollY < showFlowStatusBarTrigger && scrollY >= showFlowStatusBarTrigger) {
                    DisplayUtils.setStatusBarStyle(PhotoActivity.this, false);
                } else if (oldScrollY >= showFlowStatusBarTrigger && scrollY < showFlowStatusBarTrigger) {
                    DisplayUtils.setStatusBarStyle(PhotoActivity.this, true);
                }
                if (!recyclerView.canScrollVertically(-1) || (getMoreHolder() != null)) {
                    if (!onlyDark) {
                        onlyDark = true;
                        DisplayUtils.setNavigationBarStyle(
                                PhotoActivity.this, true, hasTranslucentNavigationBar());
                    }
                } else if (onlyDark) {
                    onlyDark = false;
                    DisplayUtils.setNavigationBarStyle(
                            PhotoActivity.this, false, hasTranslucentNavigationBar());
                }
            }
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
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
                            PhotoActivity.this,
                            switchBackground,
                            photoListManagePresenter
                                    .getPhotoList()
                                    .get(targetIndex - photoListManagePresenter.getHeadIndex()));
                    switchBackground.setBackgroundColor(
                            ImageHelper.computeCardBackgroundColor(
                                    PhotoActivity.this,
                                    photoListManagePresenter
                                            .getPhotoList()
                                            .get(targetIndex - photoListManagePresenter.getHeadIndex())
                                            .color
                            )
                    );
                } else {
                    ImageHelper.releaseImageView(switchBackground);
                    switchBackground.setBackgroundColor(Color.BLACK);
                }
            }
            switchBackground.setAlpha((float) (progress * 0.5));
        }

        @Override
        public boolean canSwitch(int direction) {
            int newIndex = photoListManagePresenter.getCurrentIndex()
                    - photoListManagePresenter.getHeadIndex()
                    + direction;
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
                        0, MysplashApplication.getInstance().loadMorePhotos(
                                PhotoActivity.this,
                                photoListManagePresenter.getPhotoList(),
                                photoListManagePresenter.getHeadIndex(),
                                true
                        )
                );
                photoListManagePresenter.setHeadIndex(
                        photoListManagePresenter.getHeadIndex()
                                - (photoListManagePresenter.getPhotoList().size() - oldSize));
            } else if (direction == SwipeSwitchLayout.DIRECTION_RIGHT
                    && photoListManagePresenter.getTailIndex() - currentIndex <= 10) {
                photoListManagePresenter.getPhotoList().addAll(
                        MysplashApplication.getInstance().loadMorePhotos(
                                PhotoActivity.this,
                                photoListManagePresenter.getPhotoList(),
                                photoListManagePresenter.getHeadIndex(),
                                false
                        )
                );
            }
        }
    }
}