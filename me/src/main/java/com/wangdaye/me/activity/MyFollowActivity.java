package com.wangdaye.me.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.presenter.TabLayoutDoubleClickBackToTopPresenter;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarViewPager;
import com.wangdaye.me.R;
import com.wangdaye.me.R2;
import com.wangdaye.me.di.component.DaggerApplicationComponent;
import com.wangdaye.me.vm.MyFollowerViewModel;
import com.wangdaye.me.vm.MyFollowingViewModel;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.me.ui.adapter.MyFollowItemEventHelper;
import com.wangdaye.me.ui.view.MyFollowUserView;
import com.wangdaye.me.ui.adapter.MyFollowAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * My follow activity.
 *
 * This activity is used to show followers for application user.
 *
 * */

@Route(path = MyFollowActivity.MY_FOLLOW_ACTIVITY)
public class MyFollowActivity extends MysplashActivity
        implements PagerManageView, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_my_follow_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_my_follow_container) CoordinatorLayout container;
    @BindView(R2.id.activity_my_follow_shadow) View shadow;
    @BindView(R2.id.activity_my_follow_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_my_follow_viewPager) FitBottomSystemBarViewPager viewPager;

    private PagerView[] pagers = new PagerView[pageCount()];
    private MyFollowAdapter[] adapters = new MyFollowAdapter[pageCount()];

    private PagerManageViewModel pagerManageModel;
    private MyFollowerViewModel[] pagerModels = new MyFollowerViewModel[pageCount()];
    @Inject ParamsViewModelFactory viewModelFactory;

    public static final String MY_FOLLOW_ACTIVITY = "/me/MyFollowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        ButterKnife.bind(this);

        initModel();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AuthManager.getInstance().getUser() != null) {
            User user = AuthManager.getInstance().getUser();
            user.followers_count += ((MyFollowUserView) pagers[followerPage()]).getUserDeltaCount();
            user.following_count += ((MyFollowUserView) pagers[followingPage()]).getUserDeltaCount();
            AuthManager.getInstance().updateUser(user);
        }
    }

    @Override
    public void handleBackPressed() {
        if (pagers[getCurrentPagerPosition()].checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishSelf(true);
        }
    }

    @Override
    protected void backToTop() {
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        pagerManageModel = ViewModelProviders.of(this, viewModelFactory).get(PagerManageViewModel.class);
        pagerManageModel.init(followerPage());

        pagerModels[followerPage()] = ViewModelProviders.of(this, viewModelFactory).get(MyFollowerViewModel.class);
        pagerModels[followerPage()].init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));

        pagerModels[followingPage()] = ViewModelProviders.of(this, viewModelFactory).get(MyFollowingViewModel.class);
        pagerModels[followingPage()].init(ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
    }

    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_my_follow_toolbar);
        toolbar.setTitle(getString(R.string.my_follow));
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.setNavigationOnClickListener(v -> finishSelf(true));

        initPages();
    }

    private void initPages() {
        pagerModels[followerPage()].readDataList(list ->
                adapters[followerPage()] = new MyFollowAdapter(this, list).setItemEventCallback(
                        new MyFollowItemEventHelper(this)
                )
        );

        pagerModels[followingPage()].readDataList(list ->
                adapters[followingPage()] = new MyFollowAdapter(this, list).setItemEventCallback(
                        new MyFollowItemEventHelper(this)
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new MyFollowUserView(
                                this,
                                adapters[followerPage()],
                                followerPage(),
                                this
                        ), new MyFollowUserView(
                                this,
                                adapters[followingPage()],
                                followingPage(),
                                this
                        )
                )
        );
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] myFollowTabs = getResources().getStringArray(R.array.my_follow_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, myFollowTabs);
        PagerAdapter adapter = new PagerAdapter(viewPager, pageList, tabList);
        if (AuthManager.getInstance().getUser() != null) {
            adapter.titleList.set(
                    followerPage(),
                    AuthManager.getInstance().getUser().followers_count + " " + myFollowTabs[followerPage()]
            );
            adapter.titleList.set(
                    followingPage(),
                    AuthManager.getInstance().getUser().following_count + " " + myFollowTabs[followingPage()]
            );
            adapter.notifyDataSetChanged();
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);

        TabLayout tabLayout = findViewById(R.id.activity_my_follow_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayoutDoubleClickBackToTopPresenter(MyFollowActivity.this::backToTop));

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = followerPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            if (pagerModels[position].getListSize() == 0
                    && pagerModels[position].getListState() != ListResource.State.REFRESHING
                    && pagerModels[position].getListState() != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(pagerModels[position], adapters[position]);
            }
        });

        pagerModels[followerPage()].observeListResource(this, viewModel ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        viewModel,
                        pagers[followerPage()],
                        adapters[followerPage()]
                )
        );
        pagerModels[followingPage()].observeListResource(this, viewModel ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        viewModel,
                        pagers[followingPage()],
                        adapters[followingPage()]
                )
        );
    }
    
    // control.

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return followerPage();
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int followerPage() {
        return 0;
    }

    private static int followingPage() {
        return 1;
    }

    private static int pageCount() {
        return 2;
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
        return pagerModels[index].getListState() != ListResource.State.REFRESHING
                && pagerModels[index].getListState() != ListResource.State.LOADING
                && pagerModels[index].getListState() != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return pagerModels[index].getListState() == ListResource.State.LOADING;
    }

    // on page change listener.

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
        // do nothing.
    }

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight()
                    + getResources().getDimensionPixelSize(R.dimen.tab_layout_height);
        } else {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
