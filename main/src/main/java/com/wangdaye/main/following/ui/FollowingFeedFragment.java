package com.wangdaye.main.following.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.di.component.DaggerApplicationComponent;
import com.wangdaye.main.following.ui.adapter.FollowingAdapter;
import com.wangdaye.main.following.ui.adapter.FollowingItemEventHelper;
import com.wangdaye.main.following.ui.adapter.holder.PhotoFeedHolder;
import com.wangdaye.main.following.ui.adapter.holder.TitleFeedHolder;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.fragment.LoadableFragment;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.main.following.FollowingFeedViewManagePresenter;
import com.wangdaye.main.following.FollowingFeedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Following feed fragment.
 *
 * This fragment is used to show the personal following feeds.
 *
 * */

public class FollowingFeedFragment extends LoadableFragment<Photo>
        implements PagerManageView, NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R2.id.fragment_following_statusBar) StatusBarView statusBar;
    @BindView(R2.id.fragment_following_container) CoordinatorLayout container;
    @BindView(R2.id.fragment_following_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.fragment_following_feedView) FollowingFeedView feedView;
    private FollowingAdapter followingAdapter;

    private PagerLoadablePresenter loadablePresenter;
    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    private FollowingFeedViewModel feedViewModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    public static final int HOLDER_TYPE_TITLE = 0;
    public static final int HOLDER_TYPE_PHOTO = 1;

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
            feedView.setOffsetY(appBar.getY());
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerApplicationComponent.create().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initModel();
        initView(Objects.requireNonNull(getView()));
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
                        && feedView != null && feedView.getState() == PagerView.State.NORMAL,
                ((MainActivity) activity).hasTranslucentNavigationBar()
        );
    }

    @Override
    public boolean needSetDarkStatusBar() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needBackToTop() {
        return feedView.checkNeedBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.switchToInitAlpha();
        if (getActivity() != null) {
            DisplayUtils.setStatusBarStyle(getActivity(), false);
        }
        animShowTopBar();
        feedView.scrollToPageTop();
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return loadablePresenter.loadMore(
                list, headIndex, headDirection,
                feedView, feedView.getRecyclerView(), followingAdapter,
                this, 0
        );
    }

    // init.

    private void initModel() {
        feedViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(FollowingFeedViewModel.class);
        feedViewModel.init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
    }

    private void initView(View v) {
        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = v.findViewById(R.id.fragment_following_toolbar);
        toolbar.setTitle(getString(R.string.action_following));
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnClickListener(v12 -> backToTop());
        toolbar.setNavigationOnClickListener(v1 -> {
            if (getActivity() != null) {
                DrawerLayout drawer = getActivity().findViewById(R.id.activity_main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        FollowingItemEventHelper itemEventHelper = new FollowingItemEventHelper(
                (MysplashActivity) getActivity(),
                Objects.requireNonNull(feedViewModel.getListResource().getValue()).dataList,
                likeOrDislikePhotoPresenter) {
            @Override
            public void downloadPhoto(Photo photo) {
                ((MainActivity) Objects.requireNonNull(getActivity())).downloadPhoto(photo);
            }
        };
        followingAdapter = new FollowingAdapter(
                Objects.requireNonNull(feedViewModel.getListResource().getValue()).dataList,
                new ArrayList<>(Arrays.asList(
                        new TitleFeedHolder.Factory(HOLDER_TYPE_TITLE, itemEventHelper),
                        new PhotoFeedHolder.Factory(HOLDER_TYPE_PHOTO, itemEventHelper)
                ))
        );
        feedView.setAdapterAndMangeView(followingAdapter, this);

        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return feedViewModel.getListResource().getValue().dataList.subList(fromIndex, toIndex);
            }
        };

        feedViewModel.getListResource().observe(this, resource ->
                FollowingFeedViewManagePresenter.responsePagerListResourceChanged(
                        resource,
                        feedView,
                        followingAdapter
                )
        );
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

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        feedViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        feedViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return feedViewModel.getListResource().getValue() != null
                && feedViewModel.getListResource().getValue().state != ListResource.State.REFRESHING
                && feedViewModel.getListResource().getValue().state != ListResource.State.LOADING
                && feedViewModel.getListResource().getValue().state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return Objects.requireNonNull(
                feedViewModel.getListResource().getValue()).state == ListResource.State.LOADING;
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        feedView.setOffsetY(appBar.getY());
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
