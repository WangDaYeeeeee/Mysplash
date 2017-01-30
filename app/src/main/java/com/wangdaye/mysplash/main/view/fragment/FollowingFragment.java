package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui._basic.MysplashFragment;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.widget.FollowingFeedView;

/**
 * Following fragment.
 * */

public class FollowingFragment extends MysplashFragment
        implements View.OnClickListener {
    // view.
    private CoordinatorLayout container;
    private NestedScrollAppBarLayout appBar;
    private FollowingFeedView feedView;

    // presenter.
    private ToolbarPresenter toolbarPresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        initPresenter();
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        feedView.cancelRequest();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public MysplashFragment readBundle(@Nullable Bundle savedInstanceState) {
        setBundle(savedInstanceState);
        return this;
    }

    @Override
    public void writeBundle(Bundle outState) {
        // do nothing.
    }

    @Override
    public boolean needPagerBackToTop() {
        return feedView.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        BackToTopUtils.showTopBar(appBar, feedView);
        feedView.pagerScrollToTop();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_following_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_following_container);

        this.appBar = (NestedScrollAppBarLayout) v.findViewById(R.id.fragment_following_appBar);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_following_toolbar);
        toolbar.setTitle(getString(R.string.action_following));
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_dark);
        }
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.feedView = (FollowingFeedView) v.findViewById(R.id.fragment_following_feedView);
        feedView.setActivity((MysplashActivity) getActivity());
        feedView.initRefresh();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;

            case R.id.fragment_following_toolbar:
                toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
                break;
        }
    }
}
