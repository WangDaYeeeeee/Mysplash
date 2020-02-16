package com.wangdaye.me.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
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

import com.wangdaye.base.i.Downloadable;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.base.vm.pager.PagerViewModel;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper;
import com.wangdaye.common.ui.dialog.ProfileDialog;
import com.wangdaye.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.me.R;
import com.wangdaye.me.R2;
import com.wangdaye.me.base.RoutingHelper;
import com.wangdaye.me.di.component.DaggerApplicationComponent;
import com.wangdaye.me.ui.view.MeCollectionsView;
import com.wangdaye.me.ui.view.MePhotosView;
import com.wangdaye.me.ui.view.MeProfileView;
import com.wangdaye.me.vm.MeActivityModel;
import com.wangdaye.me.vm.MeCollectionsViewModel;
import com.wangdaye.me.vm.MeLikesViewModel;
import com.wangdaye.me.vm.MePagerViewModel;
import com.wangdaye.me.vm.MePhotosViewModel;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.base.activity.LoadableActivity;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.ShareUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

@Route(path=MeActivity.ME_ACTIVITY)
public class MeActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_me_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_me_container) CoordinatorLayout container;
    @BindView(R2.id.activity_me_shadow) View shadow;

    @BindView(R2.id.activity_me_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_me_toolbar) Toolbar toolbar;
    @BindView(R2.id.activity_me_avatar) CircularImageView avatar;
    @BindView(R2.id.activity_me_title) TextView title;
    @OnClick(R2.id.activity_me_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()
                && !TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            ProfileDialog dialog = new ProfileDialog();
            dialog.setUsername(AuthManager.getInstance().getUsername());
            dialog.show(getSupportFragmentManager(), null);
        }
    }
    @BindView(R2.id.activity_me_profileView) MeProfileView profile;

    @BindView(R2.id.activity_me_viewPager) ViewPager viewPager;
    @BindView(R2.id.activity_me_indicator) AutoHideInkPageIndicator indicator;
    private PagerAdapter adapter;

    private PagerView[] pagers = new PagerView[pageCount()];
    private BaseAdapter[] adapters = new BaseAdapter[pageCount()];

    private MeActivityModel activityModel;
    private PagerManageViewModel pagerManageViewModel;
    private PagerViewModel[] pagerModels = new PagerViewModel[pageCount()];
    private MePhotosViewModel photoPagerModel;
    private MeLikesViewModel likesPagerModel;
    private MeCollectionsViewModel collectionsPagerModel;
    @Inject ParamsViewModelFactory viewModelFactory;
    
    private Handler handler;

    public static final String ME_ACTIVITY = "/me/MeActivity";
    public static final String ACTION_ME_ACTIVITY = "com.wangdaye.mysplash.Me";
    public static final String KEY_ME_ACTIVITY_BROWSABLE = "me_activity_browsable";
    public static final String KEY_ME_ACTIVITY_PAGE_POSITION = "me_activity_page_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
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
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        setResult(RESULT_OK);
        finish();
        if (backPressed) {
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Photo> loadMoreData(int currentCount) {
        if (getCurrentPagerPosition() != collectionsPage()) {
            int page = getCurrentPagerPosition();
            return PagerLoadablePresenter.loadMore(
                    (PagerViewModel<Photo>) pagerModels[page],
                    currentCount,
                    pagers[page],
                    pagers[page].getRecyclerView(),
                    this,
                    page
            );
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isValidProvider(Class clazz) {
        return clazz == Photo.class;
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
        photoPagerModel.init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
        pagerModels[photosPage()] = photoPagerModel;

        likesPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MeLikesViewModel.class);
        likesPagerModel.init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
        pagerModels[likesPage()] = likesPagerModel;

        collectionsPagerModel = ViewModelProviders.of(this, viewModelFactory).get(
                MeCollectionsViewModel.class);
        collectionsPagerModel.init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
        pagerModels[collectionsPage()] = collectionsPagerModel;
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("SetTextI18n")
    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        DisplayUtils.inflateToolbarMenu(toolbar, R.menu.activity_me_toolbar, this);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(MeActivity.this);
            }
            finishSelf(true);
        });

        initPages();

        handler = new Handler();
        handler.postDelayed(
                () -> activityModel.getResource().observe(this, userResource -> drawProfile()),
                800);
        activityModel.getResource().observe(this, user -> {

            LoadImagePresenter.loadUserAvatar(
                    MeActivity.this, avatar, AuthManager.getInstance().getUser(), null);
            if (AuthManager.getInstance().getUser() != null) {
                title.setText(AuthManager.getInstance().getUser().name);
            } else {
                title.setText("...");
            }

            for (int i = photosPage(); i < pageCount(); i ++) {
                if (TextUtils.isEmpty(((MePagerViewModel) pagerModels[i]).getUsername())) {
                    PagerViewManagePresenter.initRefresh(pagerModels[i], adapters[i]);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initPages() {
        photoPagerModel.readDataList(list ->
                adapters[photosPage()] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this, photoPagerModel, (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        likesPagerModel.readDataList(list ->
                adapters[likesPage()] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this, likesPagerModel, (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        collectionsPagerModel.readDataList(list ->
                adapters[collectionsPage()] = new CollectionAdapter(this, list).setItemEventCallback(
                        new CollectionItemEventHelper(this)
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new MePhotosView(
                                this,
                                (PhotoAdapter) adapters[photosPage()],
                                photosPage(),
                                this
                        ), new MePhotosView(
                                this,
                                (PhotoAdapter) adapters[likesPage()],
                                likesPage(),
                                this
                        ), new MeCollectionsView(
                                this,
                                (CollectionAdapter) adapters[collectionsPage()],
                                collectionsPage(),
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

        TabLayout tabLayout = findViewById(R.id.activity_me_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageViewModel.getPagerPosition().observe(this, position -> {
            for (int i = photosPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }

            if (pagerModels[getCurrentPagerPosition()].getListSize() == 0
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.REFRESHING
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(
                        pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = photosPage(); i < pageCount(); i ++) {
            int finalI = i;
            pagerModels[i].observeListResource(this, viewModel ->
                    PagerViewManagePresenter.responsePagerListResourceChanged(
                            viewModel, pagers[finalI], adapters[finalI]
                    )
            );
        }

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
        adapter.titleList = new ArrayList<>(
                Arrays.asList(
                        DisplayUtils.abridgeNumber(user.total_photos)
                                + " " + getResources().getStringArray(R.array.user_tabs)[0],
                        DisplayUtils.abridgeNumber(user.total_likes)
                                + " " + getResources().getStringArray(R.array.user_tabs)[1],
                        DisplayUtils.abridgeNumber(user.total_collections)
                                + " " + getResources().getStringArray(R.array.user_tabs)[2]
                )
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

    public void downloadPhoto(Photo photo) {
        requestReadWritePermission(photo, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                ComponentFactory.getDownloaderService().addTask(
                        MeActivity.this,
                        (Photo) downloadable,
                        DownloadTask.DOWNLOAD_TYPE,
                        ComponentFactory.getSettingsService().getDownloadScale()
                );
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        MeActivity.this, getString(R.string.feedback_need_permission));
            }
        });
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
        return pagerModels[index].getListState() != ListResource.State.REFRESHING
                && pagerModels[index].getListState() != ListResource.State.LOADING
                && pagerModels[index].getListState() != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return pagerModels[index].getListState() == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_edit) {
            if (AuthManager.getInstance().isAuthorized()
                    && AuthManager.getInstance().getUser() != null) {
                RoutingHelper.startUpdateMeActivity(this);
            }
        } else if (i == R.id.action_submit) {
            RoutingHelper.startWebActivity(this, UrlCollection.UNSPLASH_SUBMIT_URL);
        } else if (i == R.id.action_portfolio) {
            if (AuthManager.getInstance().isAuthorized()
                    && AuthManager.getInstance().getUser() != null) {
                String url = AuthManager.getInstance().getUser().portfolio_url;
                if (!TextUtils.isEmpty(url)) {
                    RoutingHelper.startWebActivity(this, url);
                } else {
                    NotificationHelper.showSnackbar(this, getString(R.string.feedback_portfolio_is_null));
                }
            }
        } else if (i == R.id.action_share) {
            if (AuthManager.getInstance().isAuthorized()
                    && AuthManager.getInstance().getUser() != null) {
                ShareUtils.shareUser(AuthManager.getInstance().getUser());
            }
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
