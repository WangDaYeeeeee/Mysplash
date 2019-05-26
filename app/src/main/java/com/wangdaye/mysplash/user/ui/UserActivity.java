package com.wangdaye.mysplash.user.ui;

import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoItemEventHelper;
import com.wangdaye.mysplash.common.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.mysplash.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash.common.ui.popup.PhotoOrderPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.CircularImageView;
import com.wangdaye.mysplash.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.adapter.PagerAdapter;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.user.vm.AbstractUserViewModel;
import com.wangdaye.mysplash.user.vm.UserActivityModel;
import com.wangdaye.mysplash.user.vm.UserCollectionsViewModel;
import com.wangdaye.mysplash.user.vm.UserLikesViewModel;
import com.wangdaye.mysplash.user.vm.UserPhotosViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User activity.
 *
 * This activity is used to show the information of a user.
 *
 * */

public class UserActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_user_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_user_container) CoordinatorLayout container;
    @BindView(R.id.activity_user_shadow) View shadow;

    @BindView(R.id.activity_user_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.activity_user_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_user_avatar) CircularImageView avatar;
    @BindView(R.id.activity_user_title) TextView title;
    @OnClick(R.id.activity_user_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()) {
            User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
            ProfileDialog dialog = new ProfileDialog();
            dialog.setUsername(user.username);
            dialog.show(getSupportFragmentManager(), null);
        }
    }
    @BindView(R.id.activity_user_profileView) UserProfileView userProfileView;

    @BindView(R.id.activity_user_viewPager) ViewPager viewPager;
    @BindView(R.id.activity_user_indicator) AutoHideInkPageIndicator indicator;
    private PagerAdapter adapter;

    private PagerView[] pagers = new PagerView[pageCount()];
    private FooterAdapter[] adapters = new FooterAdapter[pageCount()];

    private UserActivityModel activityModel;
    private PagerManageViewModel pagerManageModel;
    private AbstractUserViewModel<?, ?>[] pagerModels = new AbstractUserViewModel<?, ?>[pageCount()];
    private UserPhotosViewModel photoPagerModel;
    private UserLikesViewModel likesPagerModel;
    private UserCollectionsViewModel collectionsPagerModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    private PagerLoadablePresenter loadablePresenter;
    private Handler handler;

    public static final String KEY_USER_ACTIVITY_USER = "user_activity_user";
    public static final String KEY_USER_ACTIVITY_USERNAME = "user_activity_username";
    public static final String KEY_USER_ACTIVITY_PAGE_POSITION = "user_activity_page_position";

    public static final int PAGE_PHOTO = 0;
    public static final int PAGE_LIKE = 1;
    public static final int PAGE_COLLECTION = 2;
    @IntDef({PAGE_PHOTO, PAGE_LIKE, PAGE_COLLECTION})
    public @interface UserPageRule {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }

        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void setTheme() {
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @Override
    public void handleBackPressed() {
        if (pagers[getCurrentPagerPosition()].checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            if (isTheLowestLevel()) {
                IntentHelper.startMainActivity(this);
            }
            finishSelf(true);
        }
    }

    @Override
    protected void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        if (getCurrentPagerPosition() == PAGE_COLLECTION) {
            return new ArrayList<>();
        }
        return loadablePresenter.loadMore(
                list, headIndex, headDirection,
                pagers[getCurrentPagerPosition()],
                pagers[getCurrentPagerPosition()].getRecyclerView(),
                adapters[getCurrentPagerPosition()],
                this,
                getCurrentPagerPosition()
        );
    }

    // init.

    private void initModel() {
        User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
        String username = getIntent().getStringExtra(KEY_USER_ACTIVITY_USERNAME);
        int page = getIntent().getIntExtra(KEY_USER_ACTIVITY_PAGE_POSITION, PAGE_PHOTO);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(UserActivityModel.class);
        if (user != null) {
            activityModel.init(Resource.success(user), user.username);
        } else if (!TextUtils.isEmpty(username)) {
            activityModel.init(Resource.error(null), username);
        } else {
            activityModel.init(Resource.error(null), "unsplash");
        }

        pagerManageModel = ViewModelProviders.of(this, viewModelFactory).get(PagerManageViewModel.class);
        pagerManageModel.init(page);

        photoPagerModel = ViewModelProviders.of(this, viewModelFactory).get(UserPhotosViewModel.class);
        photoPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder(),
                activityModel.getUsername()
        );
        pagerModels[PAGE_PHOTO] = photoPagerModel;

        likesPagerModel = ViewModelProviders.of(this, viewModelFactory).get(UserLikesViewModel.class);
        likesPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder(),
                activityModel.getUsername()
        );
        pagerModels[PAGE_LIKE] = likesPagerModel;

        collectionsPagerModel = ViewModelProviders.of(this, viewModelFactory).get(UserCollectionsViewModel.class);
        collectionsPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                activityModel.getUsername()
        );
        pagerModels[PAGE_COLLECTION] = collectionsPagerModel;
    }

    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_user_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_user_toolbar);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                IntentHelper.startMainActivity(this);
            }
            finishSelf(true);
        });

        initPages();

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                if (getCurrentPagerPosition() == PAGE_PHOTO) {
                    return Objects.requireNonNull(photoPagerModel.getListResource().getValue())
                            .dataList.subList(fromIndex, toIndex);
                } else if (getCurrentPagerPosition() == PAGE_LIKE) {
                    return Objects.requireNonNull(likesPagerModel.getListResource().getValue())
                            .dataList.subList(fromIndex, toIndex);
                } else {
                    return new ArrayList<>();
                }
            }
        };

        userProfileView.setOnRippleButtonSwitchedListener(switchTo ->
                activityModel.followOrCancelFollowUser(switchTo));
        userProfileView.setAdapter(adapter);

        handler = new Handler();
        handler.postDelayed(() ->
                activityModel.getResource().observe(
                        this, userResource -> drawProfile(userResource.data)
                ), 1000
        );
        activityModel.getResource().observe(this, resource -> {
            if (resource.data == null) {
                if (resource.status == Resource.Status.LOADING) {
                    browsableDialogMangePresenter.load(this);
                } else {
                    browsableDialogMangePresenter.error(this, () -> activityModel.requestUser());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            if (TextUtils.isEmpty(photoPagerModel.getUsername())) {
                photoPagerModel.setUsername(resource.data.username);
                photoPagerModel.refresh();
            }
            if (TextUtils.isEmpty(likesPagerModel.getUsername())) {
                likesPagerModel.setUsername(resource.data.username);
                likesPagerModel.refresh();
            }
            if (TextUtils.isEmpty(collectionsPagerModel.getUsername())) {
                collectionsPagerModel.setUsername(resource.data.username);
                collectionsPagerModel.refresh();
            }

            if (TextUtils.isEmpty(resource.data.portfolio_url)) {
                toolbar.getMenu().getItem(0).setVisible(false);
            } else {
                toolbar.getMenu().getItem(0).setVisible(true);
            }

            avatar.setOnClickListener(v ->
                    IntentHelper.startPreviewActivity(
                            UserActivity.this,
                            resource.data,
                            false
                    )
            );
            ImageHelper.loadAvatar(this, avatar, resource.data, null);

            title.setText(resource.data.name);
        });
    }

    private void initPages() {
        adapters[PAGE_PHOTO] = new PhotoAdapter(
                this,
                Objects.requireNonNull(photoPagerModel.getListResource().getValue()).dataList
        ).setItemEventCallback(new PhotoItemEventHelper(
                this,
                photoPagerModel.getListResource().getValue().dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                UserActivity.this.downloadPhoto(photo);
            }
        });

        adapters[PAGE_LIKE] = new PhotoAdapter(
                this,
                Objects.requireNonNull(likesPagerModel.getListResource().getValue()).dataList
        ).setItemEventCallback(new PhotoItemEventHelper(
                this,
                likesPagerModel.getListResource().getValue().dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                UserActivity.this.downloadPhoto(photo);
            }
        });

        adapters[PAGE_COLLECTION] = new CollectionAdapter(
                this,
                Objects.requireNonNull(collectionsPagerModel.getListResource().getValue()).dataList
        ).setItemEventCallback(new CollectionItemEventHelper(this));

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new UserPhotosView(
                                this,
                                R.id.activity_user_page_photo,
                                (PhotoAdapter) adapters[PAGE_PHOTO],
                                getCurrentPagerPosition() == PAGE_PHOTO,
                                PAGE_PHOTO,
                                this
                        ), new UserPhotosView(
                                this,
                                R.id.activity_user_page_like,
                                (PhotoAdapter) adapters[PAGE_LIKE],
                                getCurrentPagerPosition() == PAGE_LIKE,
                                PAGE_LIKE,
                                this
                        ), new UserCollectionsView(
                                this,
                                R.id.activity_user_page_collection,
                                (CollectionAdapter) adapters[PAGE_COLLECTION],
                                getCurrentPagerPosition() == PAGE_COLLECTION,
                                PAGE_COLLECTION,
                                this
                        )
                )
        );
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new PagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_user_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = PAGE_PHOTO; i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            DisplayUtils.setNavigationBarStyle(
                    this,
                    pagers[position].getState() == PagerView.State.NORMAL,
                    true
            );

            ListResource resource = pagerModels[getCurrentPagerPosition()].getListResource().getValue();
            if (resource != null
                    && resource.dataList.size() == 0
                    && resource.state != ListResource.State.REFRESHING
                    && resource.state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(
                        pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = PAGE_PHOTO; i < pageCount(); i ++) {
            int finalI = i;
            pagerModels[i].getListResource().observe(this, resource ->
                    PagerViewManagePresenter.responsePagerListResourceChanged(
                            resource, pagers[finalI], adapters[finalI]
                    )
            );
        }

        photoPagerModel.getPhotosOrder().observe(this, s -> {
            String original = photoPagerModel.getPhotosOrder().getValue();
            if (original != null && !original.equals(s)) {
                PagerViewManagePresenter.initRefresh(photoPagerModel, adapters[PAGE_PHOTO]);
            }
        });
        likesPagerModel.getPhotosOrder().observe(this, s -> {
            String original = likesPagerModel.getPhotosOrder().getValue();
            if (original != null && !original.equals(s)) {
                PagerViewManagePresenter.initRefresh(likesPagerModel, adapters[PAGE_LIKE]);
            }
        });

        AnimUtils.translationYInitShow(viewPager, 400);
    }

    private void drawProfile(@Nullable User user) {
        if (user == null) {
            return;
        }
        if (user.complete && userProfileView.getState() == UserProfileView.STATE_LOADING) {
            TransitionManager.beginDelayedTransition(container);
            userProfileView.drawUserInfo(this, user);
        } else if (userProfileView.getState() == UserProfileView.STATE_NORMAL) {
            userProfileView.setRippleButtonState(user);
        }
    }

    // control.

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return PAGE_PHOTO;
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int pageCount() {
        return 3;
    }

    private void downloadPhoto(Photo photo) {
        requestReadWritePermission(photo, downloadable ->
                DownloadHelper.getInstance(this).addMission(
                        this,
                        (Photo) downloadable,
                        DownloadMissionEntity.DOWNLOAD_TYPE
                )
        );
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        pagerModels[index].refresh();
    }

    @Override
    public void onLoad(int index) {
        pagerModels[index].load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return pagerModels[index].getListResource().getValue() != null

                && Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()
        ).state != ListResource.State.REFRESHING

                && Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()
        ).state != ListResource.State.LOADING

                && Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()
        ).state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return pagerModels[index].getListResource().getValue() != null
                && Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()
        ).state == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_portfolio:
                if (activityModel.getResource().getValue() != null
                        && activityModel.getResource().getValue().data != null) {
                    String url = activityModel.getResource().getValue().data.portfolio_url;
                    if (!TextUtils.isEmpty(url)) {
                        IntentHelper.startWebActivity(this, url);
                    } else {
                        NotificationHelper.showSnackbar(getString(R.string.feedback_portfolio_is_null));
                    }
                }
                break;

            case R.id.action_share:
                if (activityModel.getResource().getValue() != null
                        && activityModel.getResource().getValue().data != null) {
                    ShareUtils.shareUser(activityModel.getResource().getValue().data);
                }
                break;

            case R.id.action_filter:
                if (getCurrentPagerPosition() == PAGE_PHOTO) {
                    new PhotoOrderPopupWindow(
                            this,
                            toolbar,
                            photoPagerModel.getPhotosOrder().getValue(),
                            PhotoOrderPopupWindow.NO_RANDOM_TYPE
                    ).setOnPhotoOrderChangedListener(orderValue ->
                            photoPagerModel.setPhotosOrder(orderValue)
                    );
                } else if (getCurrentPagerPosition() == PAGE_LIKE) {
                    new PhotoOrderPopupWindow(
                            this,
                            toolbar,
                            likesPagerModel.getPhotosOrder().getValue(),
                            PhotoOrderPopupWindow.NO_RANDOM_TYPE
                    ).setOnPhotoOrderChangedListener(orderValue ->
                            likesPagerModel.setPhotosOrder(orderValue)
                    );
                } else {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_no_filter));
                }
                break;
        }
        return true;
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManageModel.setPagerPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (appBar.getY() <= -appBar.getMeasuredHeight()) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    indicator.setDisplayState(true);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    indicator.setDisplayState(false);
                    break;
            }
        }
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (appBar.getY() > -appBar.getMeasuredHeight()) {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                DisplayUtils.setStatusBarStyle(this, true);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on swipe listener. (swipe back listener)

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight()
                    + getResources().getDimensionPixelSize(R.dimen.tab_layout_height);
        } else {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
