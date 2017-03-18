package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common._basic.MysplashFragment;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.FollowingFeedView;

/**
 * Following fragment.
 * */

public class FollowingFragment extends MysplashFragment
        implements View.OnClickListener, NestedScrollAppBarLayout.OnNestedScrollingListener {
    // view.
    private StatusBarView statusBar;

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
        initView(view, savedInstanceState);
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
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needPagerBackToTop() {
        return feedView.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        animShowTopBar();
        feedView.pagerScrollToTop();
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (feedView != null) {
            ((MainActivity.SavedStateFragment) outState).setFollowingFeedList(feedView.getFeeds());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (feedView != null) {
            feedView.setFeeds(((MainActivity.SavedStateFragment) savedInstanceState).getFollowingFeedList());
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
    }

    /** <br> view. */

    // init.

    private void initView(View v, Bundle saveInstanceState) {
        this.statusBar = (StatusBarView) v.findViewById(R.id.fragment_following_statusBar);
        statusBar.setInitMaskAlpha();

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_following_container);

        this.appBar = (NestedScrollAppBarLayout) v.findViewById(R.id.fragment_following_appBar);
        appBar.setOnNestedScrollingListener(this);

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
        if (saveInstanceState == null) {
            feedView.initRefresh();
        }
    }

    // interface.

    private void animShowTopBar() {
        if (appBar.getY() < 0) {
            TopBarAnim topBarAnim = new TopBarAnim();
            topBarAnim.setDuration(300);
            topBarAnim.setAnimationListener(new TopBarAnimListener());

            ContentAnim contentAnim = new ContentAnim();
            contentAnim.setDuration(300);

            appBar.clearAnimation();
            feedView.clearAnimation();

            appBar.startAnimation(topBarAnim);
            feedView.startAnimation(contentAnim);
        }
    }

    /** <br> interface. */

    // on click swipeListener.

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

    // on nested scrolling swipeListener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        feedView.setOffsetY(-appBar.getY());
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitAlpha()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitAlpha()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    /** <br> inner class. */

    private class TopBarAnim extends Animation {
        // data
        private float topBarStartY;
        private float topBarEndY;

        // life cycle.

        TopBarAnim() {
            this.topBarStartY = appBar.getY();
            this.topBarEndY = 0;
        }

        // parent methods.

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            appBar.setY(topBarStartY + (topBarEndY - topBarStartY) * interpolatedTime);
        }
    }

    private class ContentAnim extends Animation {
        // data
        private float contentStartY;
        private float contentEndY;

        // life cycle.

        ContentAnim() {
            this.contentStartY = feedView.getY();
            this.contentEndY = appBar.getMeasuredHeight();
        }

        // parent methods.

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            feedView.setY(contentStartY + (contentEndY - contentStartY) * interpolatedTime);
            feedView.setOffsetY(-appBar.getY());
        }
    }

    private class TopBarAnimListener implements Animation.AnimationListener {

        // interface.

        @Override
        public void onAnimationStart(Animation animation) {
            // do nothing.
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            params.setBehavior(new NestedScrollAppBarLayout.Behavior());
            appBar.setLayoutParams(params);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    }
}
