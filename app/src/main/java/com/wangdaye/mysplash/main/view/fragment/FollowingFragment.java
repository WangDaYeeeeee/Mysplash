package com.wangdaye.mysplash.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.fragment.LoadableFragment;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.FollowingFeedView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Following fragment.
 *
 * This fragment is used to show the personal following feeds.
 *
 * */

public class FollowingFragment extends LoadableFragment<Photo>
        implements View.OnClickListener, NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R.id.fragment_following_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.fragment_following_container)
    CoordinatorLayout container;

    @BindView(R.id.fragment_following_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.fragment_following_feedView)
    FollowingFeedView feedView;

    private ToolbarPresenter toolbarPresenter;

    private class TopBarAnim extends Animation {

        private float topBarStartY;
        private float topBarEndY;

        TopBarAnim() {
            this.topBarStartY = appBar.getY();
            this.topBarEndY = 0;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            appBar.setY(topBarStartY + (topBarEndY - topBarStartY) * interpolatedTime);
        }
    }

    private class ContentAnim extends Animation {

        private float contentStartY;
        private float contentEndY;

        ContentAnim() {
            this.contentStartY = feedView.getY();
            this.contentEndY = appBar.getMeasuredHeight();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            feedView.setY(contentStartY + (contentEndY - contentStartY) * interpolatedTime);
            feedView.setOffsetY(-appBar.getY());
        }
    }

    private class TopBarAnimListener implements Animation.AnimationListener {

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        ButterKnife.bind(this, view);
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

    @Override
    public void initStatusBarStyle() {
        DisplayUtils.setStatusBarStyle(getActivity(), needSetDarkStatusBar());
    }

    @Override
    public void initNavigationBarStyle() {
        DisplayUtils.setNavigationBarStyle(getActivity(), feedView.isNormalState(), true);
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return feedView.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(getActivity(), false);
        animShowTopBar();
        feedView.pagerScrollToTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection, Bundle bundle) {
        return feedView.loadMore(list, headIndex, headDirection);
    }

    @Override
    public Bundle getBundleOfList(Bundle bundle) {
        return bundle;
    }

    @Override
    public void updateData(Photo photo) {
        feedView.updatePhoto(photo, true);
    }

    // init.

    private void initView(View v, Bundle saveInstanceState) {
        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = ButterKnife.findById(v, R.id.fragment_following_toolbar);
        toolbar.setTitle(getString(R.string.action_following));
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setNavigationOnClickListener(this);

        feedView.setActivity((MysplashActivity) getActivity());
        if (saveInstanceState == null) {
            feedView.initRefresh();
        }
    }

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
    }

    // control.

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

    @OnClick(R.id.fragment_following_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        feedView.setOffsetY(-appBar.getY());
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
