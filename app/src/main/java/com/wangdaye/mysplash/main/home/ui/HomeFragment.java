package com.wangdaye.mysplash.main.home.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.fragment.LoadableFragment;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.adapter.photo.PhotoItemEventHelper;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.popup.PhotoOrderPopupWindow;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.singleOrientationScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.ui.adapter.PagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.mysplash.main.MainActivity;
import com.wangdaye.mysplash.main.home.vm.FeaturedHomePhotosViewModel;
import com.wangdaye.mysplash.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.mysplash.main.home.vm.AbstractHomePhotosViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Home fragment.
 *
 * This fragment is used to show the home page of Mysplash.
 *
 * */

public class HomeFragment extends LoadableFragment<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R.id.fragment_home_statusBar) StatusBarView statusBar;
    @BindView(R.id.fragment_home_container) CoordinatorLayout container;

    @BindView(R.id.fragment_home_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.fragment_home_toolbar) Toolbar toolbar;

    @BindView(R.id.fragment_home_viewPager) ViewPager viewPager;
    @BindView(R.id.fragment_home_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private PhotoAdapter[] adapters = new PhotoAdapter[pageCount()];

    private PagerLoadablePresenter loadablePresenter;
    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    private PagerManageViewModel pagerManageModel;
    private AbstractHomePhotosViewModel[] pagerModels = new AbstractHomePhotosViewModel[pageCount()];
    @Inject DaggerViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initModel();
        initView(getView());
    }

    @Override
    public void initStatusBarStyle() {
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), needSetDarkStatusBar());
        }
    }

    @Override
    public void initNavigationBarStyle() {
        if (getActivity() != null) {
            DisplayUtils.setNavigationBarStyle(
                    getActivity(),
                    pagers[getCurrentPagerPosition()].getState() == PagerView.State.NORMAL,
                    true
            );
        }
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return pagers[getCurrentPagerPosition()].checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
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
        pagerManageModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PagerManageViewModel.class);
        pagerManageModel.init(newPage());

        pagerModels[newPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(NewHomePhotosViewModel.class);
        pagerModels[newPage()].init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(getActivity()).getDefaultPhotoOrder(),
                ValueUtils.getRandomPageList(Mysplash.TOTAL_NEW_PHOTOS_COUNT, Mysplash.DEFAULT_PER_PAGE),
                getResources().getStringArray(R.array.photo_order_values)[3]
        );

        pagerModels[featuredPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(FeaturedHomePhotosViewModel.class);
        pagerModels[featuredPage()].init(
                ListResource.refreshing(0, Mysplash.DEFAULT_PER_PAGE),
                SettingsOptionManager.getInstance(getActivity()).getDefaultPhotoOrder(),
                ValueUtils.getRandomPageList(Mysplash.TOTAL_FEATURED_PHOTOS_COUNT, Mysplash.DEFAULT_PER_PAGE),
                getResources().getStringArray(R.array.photo_order_values)[3]
        );
    }

    private void initView(View v) {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.fragment_home_toolbar);
        toolbar.setOnClickListener(v12 -> backToTop());
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(v1 -> {
            if (getActivity() != null) {
                DrawerLayout drawer = getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        initPages(v);
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return Objects.requireNonNull(
                        pagerModels[getCurrentPagerPosition()].getListResource().getValue()
                ).dataList.subList(fromIndex, toIndex);
            }
        };
    }

    private void initPages(View v) {
        adapters[newPage()] = new PhotoAdapter(
                getActivity(),
                Objects.requireNonNull(pagerModels[newPage()].getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(getActivity())
        ).setItemEventCallback(new PhotoItemEventHelper(
                (MysplashActivity) getActivity(),
                Objects.requireNonNull(pagerModels[newPage()].getListResource().getValue()).dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                ((MainActivity) Objects.requireNonNull(getActivity())).downloadPhoto(photo);
            }
        });

        adapters[featuredPage()] = new PhotoAdapter(
                getActivity(),
                Objects.requireNonNull(pagerModels[featuredPage()].getListResource().getValue()).dataList,
                DisplayUtils.getGirdColumnCount(getActivity())
        ).setItemEventCallback(new PhotoItemEventHelper(
                (MysplashActivity) getActivity(),
                Objects.requireNonNull(pagerModels[featuredPage()].getListResource().getValue()).dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                ((MainActivity) Objects.requireNonNull(getActivity())).downloadPhoto(photo);
            }
        });

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new HomePhotosView(
                                (MainActivity) getActivity(),
                                R.id.fragment_home_page_new,
                                adapters[newPage()],
                                getCurrentPagerPosition() == newPage(),
                                newPage(),
                                this
                        ), new HomePhotosView(
                                (MainActivity) getActivity(),
                                R.id.fragment_home_page_featured,
                                adapters[featuredPage()],
                                getCurrentPagerPosition() == featuredPage(),
                                featuredPage(),
                                this
                        )
                )
        );
        for (int i = newPage(); i < pageCount(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] homeTabs = getResources().getStringArray(R.array.home_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, homeTabs);

        PagerAdapter adapter = new PagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = v.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = newPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            if (getActivity() != null) {
                DisplayUtils.setNavigationBarStyle(
                        getActivity(),
                        pagers[position].getState() == PagerView.State.NORMAL,
                        true
                );
            }
            ListResource resource = pagerModels[position].getListResource().getValue();
            if (resource != null
                    && resource.dataList.size() == 0
                    && resource.state != ListResource.State.REFRESHING
                    && resource.state != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(pagerModels[position], adapters[position]);
            }
        });

        pagerModels[newPage()].getPhotosOrder().observe(this, s -> {
            if (!pagerModels[newPage()].getLatestOrder().equals(s)) {
                pagerModels[newPage()].setLatestOrder(s);
                PagerViewManagePresenter.initRefresh(pagerModels[newPage()], adapters[newPage()]);
            }
        });
        pagerModels[featuredPage()].getPhotosOrder().observe(this, s ->{
            if (!pagerModels[featuredPage()].getLatestOrder().equals(s)) {
                pagerModels[featuredPage()].setLatestOrder(s);
                PagerViewManagePresenter.initRefresh(pagerModels[featuredPage()], adapters[featuredPage()]);
            }
        });
        pagerModels[newPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[newPage()],
                        adapters[newPage()]
                )
        );
        pagerModels[featuredPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[featuredPage()],
                        adapters[featuredPage()]
                )
        );
    }

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return newPage();
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int newPage() {
        return 0;
    }

    private static int featuredPage() {
        return 1;
    }

    private static int pageCount() {
        return 2;
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
        ListResource resource = pagerModels[index].getListResource().getValue();
        return resource != null
                && resource.state != ListResource.State.REFRESHING
                && resource.state != ListResource.State.LOADING
                && resource.state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                pagerModels[index].getListResource().getValue()).state == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                IntentHelper.startSearchActivity((MysplashActivity) getActivity(), null);
                break;

            case R.id.action_filter:
                PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                        getActivity(),
                        toolbar,
                        pagerModels[getCurrentPagerPosition()].getPhotosOrder().getValue(),
                        PhotoOrderPopupWindow.NORMAL_TYPE
                );
                window.setOnPhotoOrderChangedListener(orderValue ->
                        pagerModels[getCurrentPagerPosition()].setPhotosOrder(orderValue)
                );
                break;
        }
        return true;
    }

    // on page changed listener.

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
        if (getActivity() != null) {
            if (needSetDarkStatusBar()) {
                if (statusBar.isInitState()) {
                    statusBar.animToDarkerAlpha();
                    DisplayUtils.setStatusBarStyle(getActivity(), true);
                }
            } else {
                if (!statusBar.isInitState()) {
                    statusBar.animToInitAlpha();
                    DisplayUtils.setStatusBarStyle(getActivity(), false);
                }
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }
}
