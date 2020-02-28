package com.wangdaye.photo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.i.Downloadable;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.common.presenter.LikePhotoPresenter;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.ShareUtils;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.di.component.DaggerApplicationComponent;
import com.wangdaye.photo.ui.PhotoButtonBar;
import com.wangdaye.photo.ui.PhotoSwipeBackCoordinatorLayout;
import com.wangdaye.photo.ui.adapter.pager.PagerAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.photo.ui.behavior.BottomSheetBehavior;
import com.wangdaye.photo.ui.dialog.DownloadTypeDialog;
import com.wangdaye.photo.vm.PhotoActivityModel;
import com.wangdaye.photo.R;

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

@Route(path = PhotoActivity.PHOTO_ACTIVITY)
public class PhotoActivity extends ReadWriteActivity
        implements DownloadRepeatDialog.OnCheckOrDownloadListener, DownloadTypeDialog.OnSelectTypeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, PhotoButtonBar.OnClickButtonListener {

    @BindView(R2.id.activity_photo_3_swipeBackView) PhotoSwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_photo_3_container) CoordinatorLayout container;
    @BindView(R2.id.activity_photo_3_shadow) View shadow;

    @BindView(R2.id.activity_photo_3_viewPager) ViewPager2 viewPager;
    private PagerAdapter pagerAdapter;

    @BindView(R2.id.activity_photo_3_appBar) LinearLayout topBar;
    @BindView(R2.id.activity_photo_3_toolbar) Toolbar toolbar;

    @BindView(R2.id.activity_photo_3_bottomBar) LinearLayout bottomBar;

    @BindView(R2.id.activity_photo_3_actor_controlBar) LinearLayout actorControlBar;
    @BindView(R2.id.activity_photo_3_actor_avatar) CircularImageView avatar;
    @OnClick(R2.id.activity_photo_3_actor_avatar)
    void clickAvatar() {
        readCurrentPhoto(photo -> ComponentFactory.getUserModule().startUserActivity(
                this,
                avatar,
                actorControlBar,
                photo.user,
                ProfilePager.PAGE_PHOTO
        ));
    }
    @BindView(R2.id.activity_photo_3_actor_title) TextView title;
    @BindView(R2.id.activity_photo_3_actor_subtitle) TextView subtitle;
    @BindView(R2.id.activity_photo_3_actor_buttonBar) PhotoButtonBar buttonBar;

    @BindView(R2.id.activity_photo_3_previewTop) View previewTop;
    @BindView(R2.id.activity_photo_3_previewBottom) View previewBottom;

    @BindView(R2.id.activity_photo_3_bottomSheet_background) View bottomSheetBackground;
    @OnClick(R2.id.activity_photo_3_bottomSheet_background)
    void clickBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @BindView(R2.id.activity_photo_3_bottomSheet) RecyclerView bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private PhotoInfoAdapter3 photoInfoAdapter;

    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    private @Nullable AnimatorSet componentsVisibilityAnimator;
    private @Nullable AnimatorSet previewVisibilityAnimator;

    private PhotoActivityModel activityModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    public static final String PHOTO_ACTIVITY = "/photo/PhotoActivity";
    public static final String KEY_PHOTO_ACTIVITY_PHOTO = "photo_activity_2_photo";
    public static final String KEY_PHOTO_ACTIVITY_CURRENT_INDEX = "photo_activity_2_current_index";
    public static final String KEY_PHOTO_ACTIVITY_ID = "photo_activity_2_id";

    private static final int LANDSCAPE_MAX_WIDTH_DP = 580;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        setContentView(R.layout.activity_photo_3);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initSystemBar() {
        DisplayUtils.setSystemBarStyle(this, true,
                true, false, true, false);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            finishSelf(true);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && activityModel.isMultiPage()
                && activityModel.getInitPage() == viewPager.getCurrentItem()
                && pagerAdapter.getCurrentScale() == 1) {
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
        Photo photo = getIntent().getParcelableExtra(KEY_PHOTO_ACTIVITY_PHOTO);
        int currentIndex = getIntent().getIntExtra(KEY_PHOTO_ACTIVITY_CURRENT_INDEX, -1);
        String photoId = getIntent().getStringExtra(KEY_PHOTO_ACTIVITY_ID);
        List<Photo> photoList = MysplashApplication.getInstance().loadMorePhotos(this, 0);
        if (photoList == null) {
            photoList = new ArrayList<>();
        }

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoActivityModel.class);
        if (photoList.size() != 0) {
            if (currentIndex < 0 || currentIndex >= photoList.size()) {
                currentIndex = 0;
            }
            activityModel.init(photoList, currentIndex);
        } else if (photo != null) {
            activityModel.init(photo);
        } else if (!TextUtils.isEmpty(photoId)) {
            activityModel.init(photoId);
        } else {
            activityModel.init("0TFBD0R75n4");
        }
    }

    private void initView() {
        swipeBackView.setTarget(viewPager);
        swipeBackView.setHorizontalConsumer(viewPager);
        swipeBackView.setBottomSheetRecyclerView(bottomSheet);
        swipeBackView.setOnSwipeListener(this);

        viewPager.setUserInputEnabled(false);
        viewPager.setPageTransformer(
                new MarginPageTransformer(
                        getResources().getDimensionPixelSize(R.dimen.normal_margin)
                )
        );

        toolbar.setTitle("");
        if (isTheLowestLevel()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        DisplayUtils.inflateToolbarMenu(toolbar, R.menu.activity_photo_toolbar, item -> {
            int i = item.getItemId();
            if (i == R.id.action_preview) {
                activityModel.getComponentsVisibility().setValue(false);
                activityModel.getPreviewVisibility().setValue(true);
            } else {
                readCurrentPhoto(ShareUtils::sharePhoto);
            }
            return true;
        });
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });

        int marginHorizontal = 0;
        if (DisplayUtils.isLandscape(this)) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            float density = getResources().getDisplayMetrics().density;
            int widthDp = (int) (screenWidth / density);
            if (widthDp > LANDSCAPE_MAX_WIDTH_DP) {
                marginHorizontal = (int) new DisplayUtils(this).dpToPx(
                        (int) ((widthDp - LANDSCAPE_MAX_WIDTH_DP) * 0.5)
                );
            }
        }
        if (marginHorizontal > 0) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bottomSheet.getLayoutParams();
            params.setMarginStart(marginHorizontal);
            params.setMarginEnd(marginHorizontal);
            bottomSheet.setLayoutParams(params);
        }

        bottomSheet.setClipToPadding(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            bottomSheet.setOnApplyWindowInsetsListener((v, insets) -> {
                bottomSheet.setPadding(0, 0, 0,
                        (int) (new DisplayUtils(this).dpToPx(12)
                                + insets.getSystemWindowInsetTop()
                                + insets.getSystemWindowInsetBottom())
                );
                return insets;
            });
        } else {
            bottomSheet.setPadding(0, 0, 0,
                    (int) (new DisplayUtils(this).dpToPx(12)
                            + DisplayUtils.getStatusBarHeight(getResources())
                            + DisplayUtils.getNavigationBarHeight(this))
            );
        }

        this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            float lastOffset = -1;
            int visibility = View.GONE;

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    PhotoActivity.this.bottomSheet.scrollToPosition(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (lastOffset >= 0 || slideOffset >= 0) {
                    float r = (float) Math.min(0.66, Math.max(0, slideOffset));
                    bottomSheetBackground.setAlpha(r);
                    visibility = r > 0 ? View.VISIBLE : View.GONE;
                } else {
                    visibility = View.GONE;
                }

                lastOffset = slideOffset;

                if (bottomSheetBackground.getVisibility() != visibility) {
                    bottomSheetBackground.setVisibility(visibility);
                }
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setExpandedOffset(DisplayUtils.getStatusBarHeight(getResources()));
        bottomSheet.post(() ->
                bottomSheetBehavior.setHalfExpandedRatio(
                        1.f * bottomBar.getMeasuredHeight() / container.getMeasuredHeight())
        );

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };

        // observe.

        activityModel.getListResource().observe(this, resource -> {
            if (resource.dataList.size() == 0) {
                return;
            }
            if (pagerAdapter == null) {
                pagerAdapter = new PagerAdapter(this, resource.dataList);
                viewPager.setAdapter(pagerAdapter);
                viewPager.registerOnPageChangeCallback(onPageChangeCallback);
                viewPager.setCurrentItem(activityModel.getInitPage(), false);
            } else {
                pagerAdapter.update(resource.dataList);
            }
        });
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

            title.setText(resource.data.user.name);
            subtitle.setText(DisplayUtils.getDate(PhotoActivity.this, resource.data.created_at));
            LoadImagePresenter.loadUserAvatar(PhotoActivity.this, avatar, resource.data.user, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(resource.data.user.username + "_" + resource.data.user.username);
            }

            buttonBar.setState(resource.data);
            buttonBar.setOnClickButtonListener(this);

            if (updateAdapter(resource.data, resource.status == Resource.Status.LOADING)
                    && photoInfoAdapter != null) {
                GridLayoutManager layoutManager = new GridLayoutManager(
                        this, photoInfoAdapter.getColumnCount());
                layoutManager.setSpanSizeLookup(
                        photoInfoAdapter.getSpanSizeLookup(DisplayUtils.isLandscape(this))
                );
                bottomSheet.setLayoutManager(layoutManager);
                bottomSheet.setAdapter(photoInfoAdapter);
            }
        });
        activityModel.getComponentsVisibility().observe(this, visibility -> {
            if (componentsVisibilityAnimator != null) {
                componentsVisibilityAnimator.cancel();
                componentsVisibilityAnimator = null;
            }

            componentsVisibilityAnimator = new AnimatorSet();
            componentsVisibilityAnimator.playTogether(
                    ObjectAnimator.ofFloat(
                            topBar, "alpha", topBar.getAlpha(), visibility ? 1 : 0),
                    ObjectAnimator.ofFloat(
                            topBar, "translationY", topBar.getTranslationY(),
                            visibility ? 0 : -topBar.getMeasuredHeight()),
                    ObjectAnimator.ofFloat(
                            bottomBar, "alpha", bottomBar.getAlpha(), visibility ? 1 : 0),
                    ObjectAnimator.ofFloat(
                            bottomBar, "translationY", bottomBar.getTranslationY(),
                            visibility ? 0 : bottomBar.getMeasuredHeight())
            );
            componentsVisibilityAnimator.setDuration(350);
            componentsVisibilityAnimator.setInterpolator(new FastOutSlowInInterpolator());
            componentsVisibilityAnimator.start();
        });
        activityModel.getPreviewVisibility().observe(this, visibility -> {
            if (previewVisibilityAnimator != null) {
                previewVisibilityAnimator.cancel();
                previewVisibilityAnimator = null;
            }

            previewVisibilityAnimator = new AnimatorSet();
            previewVisibilityAnimator.playTogether(
                    ObjectAnimator.ofFloat(
                            previewTop, "alpha", previewTop.getAlpha(), visibility ? 1 : 0),
                    ObjectAnimator.ofFloat(
                            previewTop, "translationY", previewTop.getTranslationY(),
                            visibility ? 0 : -previewTop.getMeasuredHeight()),
                    ObjectAnimator.ofFloat(
                            previewBottom, "alpha", previewBottom.getAlpha(), visibility ? 1 : 0),
                    ObjectAnimator.ofFloat(
                            previewBottom, "translationY", previewBottom.getTranslationY(),
                            visibility ? 0 : previewBottom.getMeasuredHeight())
            );
            previewVisibilityAnimator.setDuration(350);
            previewVisibilityAnimator.setInterpolator(new FastOutSlowInInterpolator());
            previewVisibilityAnimator.start();
        });

        topBar.setAlpha(0F);
        bottomBar.setAlpha(0F);

        avatar.setVisibility(View.GONE);
        avatar.setScaleX(0);
        avatar.setScaleY(0);
        AnimUtils.animScale(avatar, 300, 350, 1);
    }

    /**
     * @return true : need to be set as the adapter for recycler view.
     * */
    private boolean updateAdapter(@Nullable Photo photo, boolean progressing) {
        if (photo == null) {
            return false;
        }

        if (photoInfoAdapter == null) {
            int columnCount = DisplayUtils.isLandscape(this) || DisplayUtils.isTabletDevice(this)
                    ? PhotoInfoAdapter3.COLUMN_COUNT_HORIZONTAL
                    : PhotoInfoAdapter3.COLUMN_COUNT_VERTICAL;
            photoInfoAdapter = new PhotoInfoAdapter3(this, photo, progressing, columnCount);
            return true;
        } else {
            // update photo.
            photoInfoAdapter.update(photo, progressing);
            return false;
        }
    }

    public void switchComponentsVisibility() {
        assert activityModel.getComponentsVisibility().getValue() != null;
        assert activityModel.getPreviewVisibility().getValue() != null;

        boolean componentsVisibility = activityModel.getComponentsVisibility().getValue();
        boolean previewVisibility = activityModel.getPreviewVisibility().getValue();

        if (componentsVisibility && !previewVisibility) {
            activityModel.getComponentsVisibility().setValue(false);
            activityModel.getPreviewVisibility().setValue(false);
        } else if (!componentsVisibility && previewVisibility) {
            activityModel.getComponentsVisibility().setValue(true);
            activityModel.getPreviewVisibility().setValue(false);
        } else {
            activityModel.getComponentsVisibility().setValue(true);
            activityModel.getPreviewVisibility().setValue(false);
        }
    }

    private void readCurrentPhoto(PhotoReader r) {
        if (activityModel.getResource().getValue() != null
                && activityModel.getResource().getValue().data != null) {
            r.read(activityModel.getResource().getValue().data);
        }
    }

    private interface PhotoReader {
        void read(Photo photo);
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
                        if (!ComponentFactory.getDownloaderService().isDownloading(
                                PhotoActivity.this, photo.id)) {
                            String scale = ComponentFactory.getSettingsService().getDownloadScale();
                            ComponentFactory.getDownloaderService().addTask(
                                    PhotoActivity.this, photo, type, scale);

                            // handel update result by message bus.
                            // viewModel.setPhoto(Resource.loading(photo), false);
                        }
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
            Photo photo = activityModel.getResource().getValue().data;
            requestReadWritePermission(activityModel.getResource().getValue().data,
                    new RequestPermissionCallback() {
                @Override
                public void onGranted(Downloadable downloadable) {
                    if (!ComponentFactory.getDownloaderService().isDownloading(
                            PhotoActivity.this, photo.id)) {
                        String scale = ComponentFactory.getSettingsService().getDownloadScale();
                        ComponentFactory.getDownloaderService().addTask(
                                PhotoActivity.this, photo, (Integer) obj, scale);

                        // handel update result by message bus.
                        // viewModel.setPhoto(Resource.loading(photo), false);
                    }
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

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(@SwipeBackCoordinatorLayout.DirectionRule int dir, float percent) {
        if (dir == SwipeBackCoordinatorLayout.DOWN_DIR) {
            shadow.setAlpha(1 - percent);
            topBar.setAlpha(1 - percent);
            bottomBar.setAlpha(1 - percent);
        }
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.DOWN_DIR) {
            finishSelf(false);
        }
    }

    // on click button listener.

    @Override
    public void onLikeButtonClicked() {
        readCurrentPhoto(photo -> {
            if (AuthManager.getInstance().isAuthorized()) {
                if (photo != null && !LikePhotoPresenter.getInstance().isInProgress(photo)) {
                    if (!photo.liked_by_user) {
                        LikePhotoPresenter.getInstance().like(photo);
                    } else {
                        LikePhotoPresenter.getInstance().unlike(photo);
                    }

                    // handel update result by message bus.
                    // viewModel.setPhoto(Resource.loading(photo), false);
                }
            } else {
                ComponentFactory.getMeModule().startLoginActivity(this);
            }
        });
    }

    @Override
    public void onCollectButtonClicked() {
        readCurrentPhoto(photo -> {
            if (AuthManager.getInstance().isAuthorized()) {
                SelectCollectionDialog dialog = new SelectCollectionDialog();
                dialog.setPhotoAndListener(photo, new DispatchCollectionsChangedPresenter());
                dialog.show(getSupportFragmentManager(), null);
            } else {
                ComponentFactory.getMeModule().startLoginActivity(this);
            }
        });
    }

    @Override
    public void onDownloadButtonClicked() {
        readyToDownload(DownloadTask.DOWNLOAD_TYPE, true);
    }

    @Override
    public void onDownloadButtonLongClicked() {
        readyToDownload(DownloadTask.DOWNLOAD_TYPE);
    }

    private ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {

            ListResource<Photo> resource = activityModel.getListResource().getValue();
            assert resource != null;
            if (position < 0 || position >= resource.dataList.size()) {
                return;
            }

            List<Photo> photoList = resource.dataList;
            activityModel.setPhoto(photoList.get(position));

            if (position > photoList.size() - ListPager.DEFAULT_PER_PAGE) {
                List<Photo> append = MysplashApplication.getInstance().loadMorePhotos(
                        PhotoActivity.this, photoList.size());
                if (append != null && append.size() != 0) {
                    activityModel.getListResource().setValue(ListResource.loadSuccess(resource, append));
                }
            }
        }
    };
}