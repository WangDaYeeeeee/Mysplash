package com.wangdaye.mysplash.main.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.main.model.fragment.OrderObject;
import com.wangdaye.mysplash.main.model.fragment.PagerObject;
import com.wangdaye.mysplash.main.model.fragment.i.OrderModel;
import com.wangdaye.mysplash.main.model.fragment.i.PagerModel;
import com.wangdaye.mysplash.main.model.widget.TypeStateObject;
import com.wangdaye.mysplash.main.presenter.fragment.HomeMenuImp;
import com.wangdaye.mysplash.main.presenter.fragment.PagerImp;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImp;
import com.wangdaye.mysplash.main.presenter.fragment.i.HomeMenuPresenter;
import com.wangdaye.mysplash.main.presenter.fragment.i.PagerPresenter;
import com.wangdaye.mysplash.main.presenter.fragment.i.ToolbarPresenter;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.fragment.i.FragmentView;
import com.wangdaye.mysplash.main.view.fragment.i.PagerView;
import com.wangdaye.mysplash.main.view.fragment.i.ToolbarView;
import com.wangdaye.mysplash.main.view.widget.HomePageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Home fragment.
 * */

public class HomeFragment extends Fragment
        implements ToolbarView, FragmentView, PagerView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener {
    // model.
    private OrderModel orderModel;
    private PagerModel pagerModel;

    // view.
    private HomePageView[] pages = new HomePageView[2];

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private HomeMenuPresenter homeMenuPresenter;
    private PagerPresenter pagerPresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initModel();
        initView(view);
        initPresenter();
        pages[0].initRefresh();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (HomePageView p : pages) {
            p.cancelRequest();
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImp(pagerModel, this);
        this.homeMenuPresenter = new HomeMenuImp(orderModel, pagerModel, this, this);
        this.pagerPresenter = new PagerImp(orderModel, pagerModel, this);
    }

    /** <br> view. */

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_home_statusBar);
        if (ModeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_home_toolbar);
        if (ModeUtils.getInstance(getActivity()).isNormalMode()) {
            toolbar.inflateMenu(R.menu.menu_fragment_home_toolbar_normal);
        } else {
            toolbar.inflateMenu(R.menu.menu_fragment_home_toolbar_random);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        initPages(v);
    }

    private void initPages(View v) {
        List<View> pageList = new ArrayList<>();
        pages[0] = new HomePageView(
                getActivity(),
                TypeStateObject.NEW_TYPE,
                orderModel.getOrder(),
                ModeUtils.getInstance(getActivity()).isNormalMode());
        pages[1] = new HomePageView(
                getActivity(),
                TypeStateObject.FEATURED_TYPE,
                orderModel.getOrder(),
                ModeUtils.getInstance(getActivity()).isNormalMode());
        Collections.addAll(pageList, pages);

        List<String> tabList = new ArrayList<>();
        tabList.add("NEW");
        tabList.add("FEATURED");
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.fragment_home_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    /** <br> model. */

    private void initModel() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String pageOrder = sharedPreferences.getString(
                getString(R.string.key_default_order),
                PhotoApi.ORDER_BY_LATEST);
        this.orderModel = new OrderObject(pageOrder);
        this.pagerModel = new PagerObject();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.clickNavigationIcon();
                break;

            case R.id.fragment_home_toolbar:
                toolbarPresenter.clickToolbar();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                homeMenuPresenter.clickSearchItem();
                return true;

            case R.id.action_order:
                homeMenuPresenter.clickOrderItem(getActivity());
                return true;

            case R.id.action_random_mode:
                homeMenuPresenter.clickRandomItem(getActivity());
                return true;

            case R.id.action_normal_mode:
                homeMenuPresenter.clickNormalItem(getActivity());
                return true;
        }
        return false;
    }

    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerPresenter.checkPageRefresh(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }

    // view.

    // toolbar view.

    @Override
    public void clickNavigationIcon() {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void scrollToTop(int i) {
        pages[i].scrollToTop();
    }

    // home menu view.

    @Override
    public void addFragment(Fragment f) {
        ((MainActivity) getActivity()).addFragment(f);
    }

    @Override
    public void changeFragment(Fragment f) {
        ((MainActivity) getActivity()).changeFragment(f);
    }

    // pager view.

    @Override
    public void resetPage(int page, String order) {
        HomePageView v = pages[page];
        v.setOrder(order);
        v.initRefresh();
    }

    @Override
    public HomePageView getPage(int page) {
        return pages[page];
    }
}
