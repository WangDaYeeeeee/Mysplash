package com.wangdaye.main.collection.ui;

import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.collection.vm.AbstractCollectionsViewModel;
import com.wangdaye.main.collection.vm.AllCollectionsViewModel;
import com.wangdaye.main.collection.vm.CuratedCollectionsViewModel;
import com.wangdaye.main.collection.vm.FeaturedCollectionsViewModel;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.fragment.MysplashFragment;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.main.di.component.DaggerApplicationComponent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection fragment.
 *
 * This fragment is used to show the collections.
 *
 * */

public class CollectionFragment extends MysplashFragment
        implements PagerManageView, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R2.id.fragment_collection_statusBar) StatusBarView statusBar;
    @BindView(R2.id.fragment_collection_container) CoordinatorLayout container;

    @BindView(R2.id.fragment_collection_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.fragment_collection_toolbar) Toolbar toolbar;

    @BindView(R2.id.fragment_collection_viewPager) ViewPager viewPager;
    @BindView(R2.id.fragment_collection_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private CollectionAdapter[] adapters = new CollectionAdapter[pageCount()];

    private PagerManageViewModel pagerManageModel;
    private AbstractCollectionsViewModel[] pagerModels = new AbstractCollectionsViewModel[pageCount()];
    @Inject ParamsViewModelFactory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerApplicationComponent.create().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
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
    public void initStatusBarStyle(Activity activity, boolean newInstance) {
        DisplayUtils.setStatusBarStyle(
                activity, !newInstance && needSetDarkStatusBar());
    }

    @Override
    public void initNavigationBarStyle(Activity activity, boolean newInstance) {
        DisplayUtils.setNavigationBarStyle(
                activity,
                !newInstance
                        && pagers[getCurrentPagerPosition()] != null
                        && pagers[getCurrentPagerPosition()].getState() == PagerView.State.NORMAL,
                ((MainActivity) activity).hasTranslucentNavigationBar()
        );
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
        statusBar.switchToInitAlpha();
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

    // init.

    private void initModel() {
        pagerManageModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PagerManageViewModel.class);
        pagerManageModel.init(featuredPage());

        pagerModels[featuredPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(FeaturedCollectionsViewModel.class);
        pagerModels[featuredPage()].init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));

        pagerModels[allPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(AllCollectionsViewModel.class);
        pagerModels[allPage()].init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));

        pagerModels[curatedPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(CuratedCollectionsViewModel.class);
        pagerModels[curatedPage()].init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
    }

    private void initView(View v) {
        appBar.setOnNestedScrollingListener(this);

        toolbar.setTitle(R.string.action_collection);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnClickListener(v12 -> backToTop());
        toolbar.setNavigationOnClickListener(v1 -> {
            if (getActivity() != null) {
                DrawerLayout drawer = getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        initPages(v);
    }

    private void initPages(View v) {
        for (int i = featuredPage(); i < pageCount(); i ++) {
            adapters[i] = new CollectionAdapter(
                    Objects.requireNonNull(pagerModels[i].getListResource().getValue()).dataList
            ).setItemEventCallback(new CollectionItemEventHelper((MysplashActivity) getActivity()));
        }

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new CollectionsView(
                                (MainActivity) getActivity(),
                                adapters[featuredPage()],
                                getCurrentPagerPosition() == featuredPage(),
                                featuredPage(),
                                this
                        ), new CollectionsView(
                                (MainActivity) getActivity(),
                                adapters[allPage()],
                                getCurrentPagerPosition() == allPage(),
                                allPage(),
                                this
                        ), new CollectionsView(
                                (MainActivity) getActivity(),
                                adapters[curatedPage()],
                                getCurrentPagerPosition() == curatedPage(),
                                curatedPage(),
                                this
                        )
                )
        );
        for (int i = featuredPage(); i < pageCount(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] homeTabs = getResources().getStringArray(R.array.collection_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, homeTabs);

        PagerAdapter adapter = new PagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = v.findViewById(R.id.fragment_collection_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = featuredPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            if (getActivity() != null) {
                DisplayUtils.setNavigationBarStyle(
                        getActivity(),
                        pagers[position].getState() == PagerView.State.NORMAL,
                        ((MainActivity) getActivity()).hasTranslucentNavigationBar()
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

        pagerModels[featuredPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[featuredPage()],
                        adapters[featuredPage()]
                )
        );
        pagerModels[allPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[allPage()],
                        adapters[allPage()]
                )
        );
        pagerModels[curatedPage()].getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        pagers[curatedPage()],
                        adapters[curatedPage()]
                )
        );
    }

    // control.

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return featuredPage();
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int featuredPage() {
        return 0;
    }

    private static int allPage() {
        return 1;
    }

    private static int curatedPage() {
        return 2;
    }

    private static int pageCount() {
        return 3;
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
                    statusBar.switchToDarkerAlpha();
                    DisplayUtils.setStatusBarStyle(getActivity(), true);
                }
            } else {
                if (!statusBar.isInitState()) {
                    statusBar.switchToInitAlpha();
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