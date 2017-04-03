package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.PagerManageModel;
import com.wangdaye.mysplash.common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common._basic.MysplashFragment;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.i.view.PagerManageView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.fragment.PagerManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.HomeFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.PagerManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.HomeCollectionsView;
import com.wangdaye.mysplash.main.view.widget.HomePhotosView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Home fragment.
 *
 * This fragment is used to show the home page of Mysplash.
 *
 * */

public class HomeFragment extends MysplashFragment
        implements PopupManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener {
    // model.
    private PagerManageModel pagerManageModel;

    // view.
    @BindView(R.id.fragment_home_statusBar) StatusBarView statusBar;

    @BindView(R.id.fragment_home_container) CoordinatorLayout container;
    @BindView(R.id.fragment_home_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.fragment_home_toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_home_viewPager) ViewPager viewPager;
    @BindView(R.id.fragment_home_indicator) AutoHideInkPageIndicator indicator;
    private PagerView[] pagers = new PagerView[3];

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private HomeFragmentPopupManageImplementor popupManageImplementor;
    private PagerManagePresenter pagerManagePresenter;

    // data.
    private final String KEY_HOME_FRAGMENT_PAGE_POSITION = "home_fragment_page_position";

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initModel(savedInstanceState);
        initPresenter();
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (PagerView p : pagers) {
            if (p != null) {
                p.cancelRequest();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_HOME_FRAGMENT_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
        for (PagerView p : pagers) {
            p.onSaveInstanceState(outState);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return pagerManagePresenter.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (pagers[0] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeNewList(
                    ((HomePhotosView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeFeaturedList(
                    ((HomePhotosView) pagers[1]).getPhotos());
        }
        if (pagers[2] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeCollectionList(
                    ((HomeCollectionsView) pagers[2]).getCollections());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (pagers[0] != null) {
            ((HomePhotosView) pagers[0]).setPhotos(
                    ((MainActivity.SavedStateFragment) savedInstanceState).getHomeNewList());
        }
        if (pagers[1] != null) {
            ((HomePhotosView) pagers[1])
                    .setPhotos(
                            ((MainActivity.SavedStateFragment) savedInstanceState).getHomeFeaturedList());
        }
        if (pagers[2] != null) {
            ((HomeCollectionsView) pagers[2]).setCollections(
                    ((MainActivity.SavedStateFragment) savedInstanceState).getHomeCollectionList());
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManageImplementor = new HomeFragmentPopupManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
    }

    /** <br> view. */

    // init.

    private void initView(View v, Bundle savedInstanceState) {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.inflateMenu(
                toolbar, R.menu.fragment_home_toolbar_light, R.menu.fragment_home_toolbar_dark);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        initPages(v, savedInstanceState);
    }

    private void initPages(View v, Bundle savedInstanceState) {
        List<View> pageList = new ArrayList<>();
        pageList.add(
                new HomePhotosView(
                        (MainActivity) getActivity(),
                        Mysplash.CATEGORY_TOTAL_NEW,
                        R.id.fragment_home_page_new));
        pageList.add(
                new HomePhotosView(
                        (MainActivity) getActivity(),
                        Mysplash.CATEGORY_TOTAL_FEATURED,
                        R.id.fragment_home_page_featured));
        pageList.add(
                new HomeCollectionsView(
                        (MysplashActivity) getActivity(),
                        R.id.fragment_home_page_collection));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] homeTabs = getResources().getStringArray(R.array.home_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, homeTabs);

        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = ButterKnife.findById(v, R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        if (savedInstanceState == null) {
            for (PagerView pager : pagers) {
                pager.refreshPager();
            }
        } else {
            for (PagerView pager : pagers) {
                pager.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    // interface.

    public void showPopup() {
        int page = pagerManagePresenter.getPagerPosition();
        popupManageImplementor.showPopup(
                getActivity(),
                toolbar,
                pagerManagePresenter.getPagerKey(page),
                page);
    }

    /** <br> model. */

    private void initModel(Bundle savedInstanceState) {
        this.pagerManageModel = new PagerManageObject(
                savedInstanceState == null ?
                        0 : savedInstanceState.getInt(KEY_HOME_FRAGMENT_PAGE_POSITION, 0));
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;
        }
    }

    @OnClick(R.id.fragment_home_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManagePresenter.setPagerPosition(position);
        pagerManagePresenter.checkToRefresh(position);
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
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // view.

    // pager manage view.

    @Override
    public PagerView getPagerView(int position) {
        return pagers[position];
    }

    @Override
    public boolean canPagerSwipeBack(int position, int dir) {
        return false;
    }

    @Override
    public int getPagerItemCount(int position) {
        return pagers[position].getItemCount();
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        pagers[position].setKey(value);
        pagers[position].refreshPager();
    }
}
