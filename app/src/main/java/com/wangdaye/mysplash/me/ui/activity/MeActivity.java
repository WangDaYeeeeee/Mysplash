package com.wangdaye.mysplash.me.ui.activity;

import android.annotation.SuppressLint;
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
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoItemEventHelper;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash.common.ui.popup.PhotoOrderPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.singleOrientationScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.download.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.adapter.PagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.me.ui.view.MeCollectionsView;
import com.wangdaye.mysplash.me.ui.MeMenuPopupWindow;
import com.wangdaye.mysplash.me.ui.view.MePhotosView;
import com.wangdaye.mysplash.me.ui.view.MeProfileView;
import com.wangdaye.mysplash.me.vm.AbstractMePagerViewModel;
import com.wangdaye.mysplash.me.vm.MeActivityModel;
import com.wangdaye.mysplash.me.vm.MeCollectionsViewModel;
import com.wangdaye.mysplash.me.vm.MeLikesViewModel;
import com.wangdaye.mysplash.me.vm.MePhotosViewModel;

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
 * Me activity.
 *
 * This activity is used to show information of the application user.
 *
 * */

public class MeActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener,
        MeMenuPopupWindow.OnSelectItemListener, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SwipeBackCoordinatorLayout.OnSwipeListener {

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
    private PagerAdapter adapter;

    private PagerView[] pagers = new PagerView[pageCount()];
    private FooterAdapter[] adapters = new FooterAdapter[pageCount()];

    private MeActivityModel activityModel;
    private PagerManageViewModel pagerManageViewModel;
    private AbstractMePagerViewModel<?, ?>[] pagerModels = new AbstractMePagerViewModel<?, ?>[pageCount()];
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
                adapters[getCurrentPagerPosition()],
                this, getCurrentPagerPosition()
        );
    }

    // init.

    private void initModel() {
        activityModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MeActivityModel.class);
        activityModel.init();

        pagerManageViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PagerManageViewModel.class);
        pagerManageViewModel.init(photosPage());


        photoPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MePhotosViewModel.class);
        photoPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder()
        );
        pagerModels[photosPage()] = photoPagerModel;


        likesPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MeLikesViewModel.class);
        likesPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(this).getDefaultPhotoOrder()
        );
        pagerModels[likesPage()] = likesPagerModel;


        collectionsPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MeCollectionsViewModel.class);
        collectionsPagerModel.init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE)
        );
        pagerModels[collectionsPage()] = collectionsPagerModel;
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
                int page = getCurrentPagerPosition();
                if (page == photosPage()) {
                    return Objects.requireNonNull(
                            ((MePhotosViewModel) pagerModels[page]).getListResource().getValue()
                    ).dataList.subList(fromIndex, toIndex);
                } else if (page == likesPage()) {
                    return Objects.requireNonNull(
                            ((MeLikesViewModel) pagerModels[page]).getListResource().getValue()
                    ).dataList.subList(fromIndex, toIndex);
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

            for (int i = photosPage(); i < pageCount(); i ++) {
                if (TextUtils.isEmpty(pagerModels[i].getUsername())) {
                    PagerViewManagePresenter.initRefresh(pagerModels[i], adapters[i]);
                }
            }
        });
    }

    private void initPages() {
        adapters[photosPage()] = new PhotoAdapter(
                this,
                Objects.requireNonNull(photoPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this)
        ).setItemEventCallback(new PhotoItemEventHelper(
                this,
                photoPagerModel.getListResource().getValue().dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                MeActivity.this.downloadPhoto(photo);
            }
        });

        adapters[likesPage()] = new PhotoAdapter(
                this,
                Objects.requireNonNull(likesPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this)
        ).setItemEventCallback(new PhotoItemEventHelper(
                this,
                likesPagerModel.getListResource().getValue().dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                MeActivity.this.downloadPhoto(photo);
            }
        });

        adapters[collectionsPage()] = new CollectionAdapter(
                this,
                Objects.requireNonNull(collectionsPagerModel.getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(this)
        ).setItemEventCallback(new CollectionItemEventHelper(this));

        List<View> pageList = Arrays.asList(
                new MePhotosView(
                        this, R.id.activity_me_page_photo,
                        (PhotoAdapter) adapters[photosPage()],
                        getCurrentPagerPosition() == photosPage(),
                        photosPage(),
                        this
                ), new MePhotosView(
                        this, R.id.activity_me_page_like,
                        (PhotoAdapter) adapters[likesPage()],
                        getCurrentPagerPosition() == likesPage(),
                        likesPage(),
                        this
                ), new MeCollectionsView(
                        this, R.id.activity_me_page_collection,
                        (CollectionAdapter) adapters[collectionsPage()],
                        getCurrentPagerPosition() == collectionsPage(),
                        collectionsPage(), this
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
                    true
            );

            ListResource resource = pagerModels[getCurrentPagerPosition()].getListResource().getValue();
            if (pagerModels[getCurrentPagerPosition()].getListResource().getValue() != null
                    && resource.dataList.size() == 0
                    && resource.state != ListResource.State.REFRESHING
                    && resource.state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(
                        pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = photosPage(); i < pageCount(); i ++) {
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
                PagerViewManagePresenter.initRefresh(photoPagerModel, adapters[photosPage()]);
            }
        });
        likesPagerModel.getPhotosOrder().observe(this, s -> {
            String original = likesPagerModel.getPhotosOrder().getValue();
            if (original != null && !original.equals(s)) {
                PagerViewManagePresenter.initRefresh(likesPagerModel, adapters[likesPage()]);
            }
        });

        AnimUtils.translationYInitShow(viewPager, 400);
    }

    private void drawProfile() {
        TransitionManager.beginDelayedTransition(container);
        if (AuthManager.getInstance().getUser() != null) {
            profile.drawMeProfile(this, AuthManager.getInstance().getUser());
        }
        if (AuthManager.getInstance().getUser() != null) {
            drawTabTitles(AuthManager.getInstance().getUser());
        }
    }

    private void drawTabTitles(@NonNull User user) {
        adapter.titleList = Arrays.asList(
                DisplayUtils.abridgeNumber(user.total_photos)
                        + " " + getResources().getStringArray(R.array.user_tabs)[0],
                DisplayUtils.abridgeNumber(user.total_likes)
                        + " " + getResources().getStringArray(R.array.user_tabs)[1],
                DisplayUtils.abridgeNumber(user.total_collections)
                        + " " + getResources().getStringArray(R.array.user_tabs)[2]
        );
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

    // pager view manage view.

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
                        new PhotoOrderPopupWindow(
                                this,
                                toolbar,
                                photoPagerModel.getPhotosOrder().getValue(),
                                PhotoOrderPopupWindow.NO_RANDOM_TYPE
                        ).setOnPhotoOrderChangedListener(orderValue ->
                                photoPagerModel.setPhotosOrder(orderValue)
                        );
                    } else if (getCurrentPagerPosition() == likesPage()) {
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
                }
                break;

            case R.id.action_menu:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getUser() != null) {
                    new MeMenuPopupWindow(this, toolbar).setOnSelectItemListener(this);
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
