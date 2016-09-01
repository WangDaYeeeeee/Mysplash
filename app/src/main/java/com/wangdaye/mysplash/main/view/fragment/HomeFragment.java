package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.ToolbarView;
import com.wangdaye.mysplash.main.model.fragment.PagerManageObject;
import com.wangdaye.mysplash.main.model.widget.PhotosObject;
import com.wangdaye.mysplash.main.presenter.fragment.HomeFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.PagerManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.widget.HomeCollectionsView;
import com.wangdaye.mysplash.main.view.widget.HomePhotosView;

import java.util.ArrayList;
import java.util.List;

/**
 * Home fragment.
 * */

public class HomeFragment extends Fragment
        implements ToolbarView, PopupManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        NotificationUtils.SnackbarContainer {
    // model.
    private PagerManageModel pagerManageModel;

    // view.
    private CoordinatorLayout container;
    private Toolbar toolbar;
    private PagerView[] pagers = new PagerView[3];

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private HomeFragmentPopupManageImplementor popupManageImplementor;
    private PagerManagePresenter pagerManagePresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initModel();
        initView(view);
        initPresenter();
        pagers[0].refreshPager();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (PagerView p : pagers) {
            p.cancelRequest();
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor(this);
        this.popupManageImplementor = new HomeFragmentPopupManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_home_statusBar);
        if (ThemeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_home_container);

        this.toolbar = (Toolbar) v.findViewById(R.id.fragment_home_toolbar);
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            toolbar.inflateMenu(R.menu.fragment_home_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_light);
        } else {
            toolbar.inflateMenu(R.menu.fragment_home_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        initPages(v);
    }

    private void initPages(View v) {
        List<View> pageList = new ArrayList<>();
        pageList.add(new HomePhotosView(getActivity(), PhotosObject.PHOTOS_TYPE_NEW));
        pageList.add(new HomePhotosView(getActivity(), PhotosObject.PHOTOS_TYPE_FEATURED));
        pageList.add(new HomeCollectionsView(getActivity()));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        List<String> tabList = new ArrayList<>();
        tabList.add("NEW");
        tabList.add("FEATURED");
        tabList.add("COLLECTIONS");
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.fragment_home_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    // interface.

    public void pagerBackToTop() {
        pagerManagePresenter.pagerScrollToTop();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.pagerManageModel = new PagerManageObject(0);
    }

    // interface.

    public boolean needPagerBackToTop() {
        return pagerManagePresenter.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon();
                break;

            case R.id.fragment_home_toolbar:
                toolbarPresenter.touchToolbar();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        toolbarPresenter.touchMenuItem(item.getItemId());
        return true;
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
        // do nothing.
    }

    // snackbar container;

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

    // toolbar view.

    @Override
    public void touchNavigatorIcon() {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void touchToolbar() {
        pagers[pagerManageModel.getPagerPosition()].scrollToPageTop();
    }

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {
            case R.id.action_search:
                ((MainActivity) getActivity()).insertFragment(itemId);
                break;

            case R.id.action_filter:
                int page = pagerManagePresenter.getPagerPosition();
                popupManageImplementor.showPopup(
                        getActivity(),
                        toolbar,
                        pagerManagePresenter.getPagerKey(page),
                        page);
                break;
        }
    }

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
