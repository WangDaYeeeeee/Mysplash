package com.wangdaye.mysplash.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.ui.activity.MainActivity;
import com.wangdaye.mysplash.ui.dialog.SelectOrderDialog;
import com.wangdaye.mysplash.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.ui.widget.StatusBarView;
import com.wangdaye.mysplash.ui.widget.customWidget.HomePageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Home fragment.
 * */

public class HomeFragment extends Fragment
        implements View.OnClickListener, ViewPager.OnPageChangeListener, Toolbar.OnMenuItemClickListener,
        SelectOrderDialog.OnOrderSelectedListener, HomePageView.OnStartActivityCallback {
    // data
    private MyPagerAdapter adapter;
    private int pagePosition = 0;
    private String pageOrder = PhotoApi.ORDER_BY_LATEST;
    private boolean normalMode = false;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        initWidget(view);
        ((HomePageView) adapter.viewList.get(0)).initRefresh();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < adapter.viewList.size(); i ++) {
            ((HomePageView) adapter.viewList.get(i)).cancelRequest();
        }
    }

    /** <br> UI. */

    private void initWidget(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_home_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_home_toolbar);
        if (normalMode) {
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
        pageList.add(new HomePageView(getActivity(), HomePageView.NEW_TYPE, pageOrder, normalMode));
        pageList.add(new HomePageView(getActivity(), HomePageView.FEATURED_TYPE, pageOrder, normalMode));
        for (int i = 0; i < pageList.size(); i ++) {
            ((HomePageView) pageList.get(i)).setOnStartActivityCallback(this);
        }

        List<String> tabList = new ArrayList<>();
        tabList.add("NEW");
        tabList.add("FEATURED");
        this.adapter = new MyPagerAdapter(pageList, tabList);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.fragment_home_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    /** <br> data. */

    private void initData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        this.pageOrder = sharedPreferences.getString(
                getString(R.string.key_default_order),
                PhotoApi.ORDER_BY_LATEST);
        this.normalMode = sharedPreferences.getBoolean(
                getString(R.string.key_normal_mode),
                true);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.fragment_home_toolbar:
                ((HomePageView) adapter.viewList.get(pagePosition)).scrollToTop();
                break;
        }
    }

    // on start activity callback.

    @Override
    public void startActivity(Intent intent, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(
                            view,
                            (int) view.getX(), (int) view.getY(),
                            view.getMeasuredWidth(), view.getMeasuredHeight());
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        } else {
            View imageView = ((RelativeLayout) ((CardView) view).getChildAt(0)).getChildAt(0);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            getActivity(),
                            Pair.create(imageView, getString(R.string.transition_photo_image)));
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchFragment searchFragment = new SearchFragment();
                ((MainActivity) getActivity()).addFragment(searchFragment);
                return true;

            case R.id.action_order:
                SelectOrderDialog selectOrderDialog = new SelectOrderDialog(getActivity(), pageOrder, normalMode);
                selectOrderDialog.setOnOrderSelectedListener(this);
                selectOrderDialog.show();
                return true;

            case R.id.action_random_mode:
            case R.id.action_normal_mode:
                SharedPreferences.Editor editor1 = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor1.putBoolean(
                        getString(R.string.key_normal_mode),
                        item.getItemId() == R.id.action_normal_mode);
                editor1.apply();

                HomeFragment fragment = new HomeFragment();
                ((MainActivity) getActivity()).changeFragment(fragment);
                return true;
        }
        return false;
    }

    // on order selected listener.

    @Override
    public void onOrderSelect(String order) {
        if (!pageOrder.equals(order)) {
            pageOrder = order;
            HomePageView v = (HomePageView) adapter.viewList.get(pagePosition);
            v.setOrder(pageOrder);
            v.setState(HomePageView.RESET_STATE);
            v.initRefresh();
        }
    }

    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagePosition = position;
        HomePageView v = (HomePageView) adapter.viewList.get(pagePosition);
        if (v.cheekNeedChangOrder(pageOrder)) {
            v.setOrder(pageOrder);
            v.setState(HomePageView.RESET_STATE);
            v.initRefresh();
        } else if (v.cheekNeedRefresh()) {
            v.setState(HomePageView.RESET_STATE);
            v.initRefresh();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }
}
