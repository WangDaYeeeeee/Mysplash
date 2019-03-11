package com.wangdaye.mysplash.me.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash.common.ui.popup.PhotoOrderPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.me.vm.MeActivityModel;
import com.wangdaye.mysplash.me.vm.MeCollectionsViewModel;
import com.wangdaye.mysplash.me.vm.MeLikesViewModel;
import com.wangdaye.mysplash.me.vm.MePhotosViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Me activity.
 *
 * This activity is used to show information of the application user.
 *
 * */

public class MeActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener,
        MeMenuPopupWindow.OnSelectItemListener, PhotoAdapter.ItemEventCallback,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_me_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_me_container) CoordinatorLayout container;
    @BindView(R.id.activity_me_shadow) View shadow;

    @BindView(R.id.activity_me_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.activity_me_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_me_avatar) CircleImageView avatar;
    @BindView(R.id.activity_me_title) TextView title;
    @OnClick(R.id.activity_me_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()
                && !TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            ProfileDialog dialog = new ProfileDialog();
            dialog.setUsername(AuthManager.getInstance().getUsername());
            dialog.show(getSupportFragmentManager(), null);
        }
    }
    @BindView(R.id.activity_me_profileView) MeProfileView profile;

    @BindView(R.id.activity_me_viewPager) ViewPager viewPager;
    @BindView(R.id.activity_me_indicator) AutoHideInkPageIndicator indicator;
    private MyPagerAdapter adapter;

    private PagerView[] pagers = new PagerView[pageCount()];
    private PhotoAdapter photoAdapter;
    private PhotoAdapter likeAdapter;
    private CollectionAdapter collectionAdapter;

    private MeActivityModel activityModel;
    private PagerManageViewModel pagerManageViewModel;
    private MePhotosViewModel photoPagerModel;
    private MeLikesViewModel likesPagerModel;
    private MeCollectionsViewModel collectionsPagerModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;
    private PagerLoadablePresenter loadablePresenter;
    private Handler handler;

    public static final String EXTRA_BROWSABLE = "browsable";
    public static final String KEY_ME_ACTIVITY_PAGE_POSITION = "me_activity_page_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }

        setContentView(R.layout.activity_me);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getState() == AuthManager.State.FREE
                && AuthManager.getInstance().getUser() != null) {
            AuthManager.getInstance().requestPersonalProfile();
        }
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
        setResult(RESULT_OK);
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
        if (getCurrentPagerPosition() == collectionsPage()) {
            return new ArrayList<>();
        }
        return loadablePresenter.loadMore(
                list, headIndex, headDirection,
                pagers[getCurrentPagerPosition()],
                pagers[getCurrentPagerPosition()].getRecyclerView(),
                getCurrentPagerPosition() == photosPage() ? photoAdapter : likeAdapter,
                this, getCurrentPagerPosition());
    }

    // init.

    private void initModel() {
        activityModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MeActivityModel.class);
        activityModel.init();

        pagerManageViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PagerManageViewModel.class);
        pagerManageViewModel.init(photosPage());

        photoPagerModel = ViewModelProviders.of(this, viewModelFactory).get(MePhotosViewModel.class);
        photoPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder());

        likesPagerModel = ViewModelProviders.of(this, viewModelFactory).get(MeLikesViewModel.class);
        likesPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder());

        collectionsPagerModel = ViewModelProviders.of(this, viewModelFactory).get(MeCollectionsViewModel.class);
        collectionsPagerModel.init(ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE));
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_me_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_me_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                IntentHelper.startMainActivity(MeActivity.this);
            }
            finishSelf(true);
        });
        toolbar.setOnMenuItemClickListener(this);

        avatar.setOnClickListener(v -> {
            if (AuthManager.getInstance().getUser() != null) {
                IntentHelper.startPreviewActivity(
                        MeActivity.this, AuthManager.getInstance().getUser(), false);
            }
        });

        initPages();
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                if (getCurrentPagerPosition() == photosPage()) {
                    return Objects.requireNonNull(photoPagerModel.getListResource().getValue())
                            .dataList.subList(fromIndex, toIndex);
                } else if (getCurrentPagerPosition() == likesPage()) {
                    return Objects.requireNonNull(likesPagerModel.getListResource().getValue())
                            .dataList.subList(fromIndex, toIndex);
                } else {
                    return new ArrayList<>();
                }
            }
        };

        handler = new Handler();
        handler.postDelayed(
                () -> activityModel.getResource().observe(this, userResource -> drawProfile()),
                800);
        activityModel.getResource().observe(this, user -> {

            if (AuthManager.getInstance().getUser() != null) {
                title.setText(AuthManager.getInstance().getUser().name);
                ImageHelper.loadAvatar(
                        MeActivity.this, avatar, AuthManager.getInstance().getUser(), null);
            } else {
                title.setText("...");
                ImageHelper.loadAvatar(MeActivity.this, avatar, new User(), null);
            }

            if (TextUtils.isEmpty(photoPagerModel.getUsername())) {
                PagerViewManagePresenter.initRefresh(photoPagerModel, photoAdapter);
            }
            if (TextUtils.isEmpty(likesPagerModel.getUsername())) {
                PagerViewManagePresenter.initRefresh(likesPagerModel, likeAdapter);
            }
            if (TextUtils.isEmpty(collectionsPagerModel.getUsername())) {
                PagerViewManagePresenter.initRefresh(collectionsPagerModel, collectionAdapter);
            }
        });
    }

    private void initPages() {
        photoAdapter = new PhotoAdapter(
                this,
                Objects.requireNonNull(photoPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this));
        photoAdapter.setItemEventCallback(this);

        likeAdapter = new PhotoAdapter(
                this,
                Objects.requireNonNull(likesPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this));
        likeAdapter.setItemEventCallback(this);

        collectionAdapter = new CollectionAdapter(
                this,
                Objects.requireNonNull(collectionsPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this));

        List<View> pageList = new ArrayList<>();
        pageList.add(
                new MePhotosView(
                        this, R.id.activity_me_page_photo,
                        photoAdapter,
                        getCurrentPagerPosition() == photosPage(),
                        photosPage(),
                        this));
        pageList.add(
                new MePhotosView(
                        this, R.id.activity_me_page_like,
                        likeAdapter,
                        getCurrentPagerPosition() == likesPage(),
                        likesPage(),
                        this));
        pageList.add(
                new MeCollectionsView(
                        this, R.id.activity_me_page_collection,
                        collectionAdapter,
                        getCurrentPagerPosition() == collectionsPage(),
                        collectionsPage(), this));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_me_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageViewModel.getPagerPosition().observe(this, position -> {
            for (int i = photosPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            DisplayUtils.setNavigationBarStyle(
                    this,
                    pagers[position].getState() == PagerView.State.NORMAL,
                    true);
            if (position == photosPage()
                    && photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().dataList.size() == 0
                    && photoPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && photoPagerModel.getListResource().getValue().state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(photoPagerModel, photoAdapter);
            } else if (position == likesPage()
                    && likesPagerModel.getListResource().getValue() != null
                    && likesPagerModel.getListResource().getValue().dataList.size() == 0
                    && likesPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && likesPagerModel.getListResource().getValue().state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(likesPagerModel, likeAdapter);
            } else if (position == collectionsPage()
                    && collectionsPagerModel.getListResource().getValue() != null
                    && collectionsPagerModel.getListResource().getValue().dataList.size() == 0
                    && collectionsPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && collectionsPagerModel.getListResource().getValue().state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(collectionsPagerModel, collectionAdapter);
            }
        });

        photoPagerModel.getPhotosOrder().observe(this, s -> {
            String original = photoPagerModel.getPhotosOrder().getValue();
            if (original != null && !original.equals(s)) {
                PagerViewManagePresenter.initRefresh(photoPagerModel, photoAdapter);
            }
        });
        photoPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource, pagers[photosPage()], photoAdapter));

        likesPagerModel.getPhotosOrder().observe(this, s -> {
            String original = likesPagerModel.getPhotosOrder().getValue();
            if (original != null && !original.equals(s)) {
                PagerViewManagePresenter.initRefresh(likesPagerModel, likeAdapter);
            }
        });
        likesPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource, pagers[likesPage()], likeAdapter));

        collectionsPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource, pagers[collectionsPage()], collectionAdapter));

        AnimUtils.translationYInitShow(viewPager, 400);
    }

    private void drawProfile() {
        TransitionManager.beginDelayedTransition(container);
        if (AuthManager.getInstance().getUser() != null) {
            profile.drawMeProfile(AuthManager.getInstance().getUser());
        }
        if (AuthManager.getInstance().getUser() != null) {
            drawTabTitles(AuthManager.getInstance().getUser());
        }
    }

    private void drawTabTitles(@NonNull User user) {
        List<String> titleList = new ArrayList<>();
        titleList.add(
                DisplayUtils.abridgeNumber(user.total_photos)
                        + " " + getResources().getStringArray(R.array.user_tabs)[0]);
        titleList.add(
                DisplayUtils.abridgeNumber(user.total_likes)
                        + " " + getResources().getStringArray(R.array.user_tabs)[1]);
        titleList.add(
                DisplayUtils.abridgeNumber(user.total_collections)
                        + " " + getResources().getStringArray(R.array.user_tabs)[2]);
        adapter.titleList = titleList;
        adapter.notifyDataSetChanged();
    }

    // control.

    private int getCurrentPagerPosition() {
        if (pagerManageViewModel.getPagerPosition().getValue() == null) {
            return photosPage();
        } else {
            return pagerManageViewModel.getPagerPosition().getValue();
        }
    }

    private static int photosPage() {
        return 0;
    }

    private static int likesPage() {
        return 1;
    }

    private static int collectionsPage() {
        return 2;
    }

    private static int pageCount() {
        return 3;
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(Downloadable downloadable, int requestCode) {
        if (downloadable instanceof Photo) {
            DownloadHelper.getInstance(this)
                    .addMission(this, (Photo) downloadable, DownloaderService.DOWNLOAD_TYPE);
        }
    }

    // interface.

    // pager view manage view.

    @Override
    public void onRefresh(int index) {
        if (index == photosPage()) {
            photoPagerModel.refresh();
        } else if (index == likesPage()) {
            likesPagerModel.refresh();
        } else if (index == collectionsPage()) {
            collectionsPagerModel.refresh();
        }
    }

    @Override
    public void onLoad(int index) {
        if (index == photosPage()) {
            photoPagerModel.load();
        } else if (index == likesPage()) {
            likesPagerModel.load();
        } else if (index == collectionsPage()) {
            collectionsPagerModel.load();
        }
    }

    @Override
    public boolean canLoadMore(int index) {
        if (index == photosPage()) {
            return photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && photoPagerModel.getListResource().getValue().state != ListResource.State.LOADING
                    && photoPagerModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
        } else if (index == likesPage()) {
            return likesPagerModel.getListResource().getValue() != null
                    && likesPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && likesPagerModel.getListResource().getValue().state != ListResource.State.LOADING
                    && likesPagerModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
        } else if (index == collectionsPage()) {
            return collectionsPagerModel.getListResource().getValue() != null
                    && collectionsPagerModel.getListResource().getValue().state != ListResource.State.REFRESHING
                    && collectionsPagerModel.getListResource().getValue().state != ListResource.State.LOADING
                    && collectionsPagerModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
        }
        return false;

    }

    @Override
    public boolean isLoading(int index) {
        if (index == photosPage()) {
            return photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().state == ListResource.State.LOADING;
        } else if (index == likesPage()) {
            return likesPagerModel.getListResource().getValue() != null
                    && likesPagerModel.getListResource().getValue().state == ListResource.State.LOADING;
        } else if (index == collectionsPage()) {
            return collectionsPagerModel.getListResource().getValue() != null
                    && collectionsPagerModel.getListResource().getValue().state == ListResource.State.LOADING;
        }
        return false;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    IntentHelper.startUpdateMeActivity(this);
                }
                break;

            case R.id.action_filter:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    if (getCurrentPagerPosition() == photosPage()) {
                        PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                                this,
                                toolbar,
                                photoPagerModel.getPhotosOrder().getValue(),
                                PhotoOrderPopupWindow.NO_RANDOM_TYPE);
                        window.setOnPhotoOrderChangedListener(orderValue -> photoPagerModel.setPhotosOrder(orderValue));
                    } else if (getCurrentPagerPosition() == likesPage()) {
                        PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                                this,
                                toolbar,
                                likesPagerModel.getPhotosOrder().getValue(),
                                PhotoOrderPopupWindow.NO_RANDOM_TYPE);
                        window.setOnPhotoOrderChangedListener(orderValue -> likesPagerModel.setPhotosOrder(orderValue));
                    } else {
                        NotificationHelper.showSnackbar(getString(R.string.feedback_no_filter));
                    }
                }
                break;

            case R.id.action_menu:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    MeMenuPopupWindow window = new MeMenuPopupWindow(this, toolbar);
                    window.setOnSelectItemListener(this);
                }
                break;
        }
        return true;
    }

    // on select item listener.

    @Override
    public void onSelectItem(int id) {
        switch (id) {
            case MeMenuPopupWindow.ITEM_SUBMIT:
                IntentHelper.startWebActivity(this, Mysplash.UNSPLASH_SUBMIT_URL);
                break;

            case MeMenuPopupWindow.ITEM_PORTFOLIO:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    String url = AuthManager.getInstance().getUser().portfolio_url;
                    if (!TextUtils.isEmpty(url)) {
                        IntentHelper.startWebActivity(this, url);
                    } else {
                        NotificationHelper.showSnackbar(getString(R.string.feedback_portfolio_is_null));
                    }
                }
                break;

            case MeMenuPopupWindow.ITEM_SHARE:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    ShareUtils.shareUser(AuthManager.getInstance().getUser());
                }
                break;
        }
    }

    // item event callback.

    @Override
    public void onLikeOrDislikePhoto(Photo photo, int adapterPosition, boolean setToLike) {
        likeOrDislikePhotoPresenter.likeOrDislikePhoto(photo, setToLike);
    }

    @Override
    public void onDownload(Photo photo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            DownloadHelper.getInstance(this).addMission(this, photo, DownloaderService.DOWNLOAD_TYPE);
        } else {
            requestReadWritePermission(photo);
        }
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManageViewModel.setPagerPosition(position);
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
    public boolean canSwipeBack(int dir) {
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
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }
}
