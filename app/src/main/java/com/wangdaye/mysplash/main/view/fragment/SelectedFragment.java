package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.MysplashFragment;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.SelectedView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Selected fragment.
 *
 * This fragment is used to show selected collection from Unsplash.
 *
 * */

public class SelectedFragment extends MysplashFragment
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R.id.fragment_selected_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.fragment_selected_container)
    CoordinatorLayout container;

    @BindView(R.id.fragment_selected_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.fragment_selected_toolbar)
    Toolbar toolbar;

    @BindView(R.id.fragment_selected_collectionView)
    SelectedView selectedView;

    private ToolbarPresenter toolbarPresenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected, container, false);
        ButterKnife.bind(this, view);
        initPresenter();
        initView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedView.cancelRequest();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initStatusBarStyle() {
        DisplayUtils.setStatusBarStyle(getActivity(), needSetDarkStatusBar());
    }

    @Override
    public void initNavigationBarStyle() {
        DisplayUtils.setNavigationBarStyle(getActivity(), selectedView.isNormalState(), true);
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        // do nothing.
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        // do nothing.
    }

    @Override
    public boolean needBackToTop() {
        return selectedView.needBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(getActivity(), false);
        BackToTopUtils.showTopBar(appBar, selectedView);
        selectedView.scrollToTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
    }

    private void initView() {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setTitle(getString(R.string.action_selected));
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        selectedView.setActivity((MainActivity) getActivity());
        selectedView.initRefresh();
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;
        }
    }

    @OnClick(R.id.fragment_selected_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
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

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }
}