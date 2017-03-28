package com.wangdaye.mysplash.me.view.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.model.UserModel;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.presenter.UserPresenter;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.i.view.UserView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash.me.model.activity.PagerManageObject;
import com.wangdaye.mysplash.me.model.activity.UserObject;
import com.wangdaye.mysplash.me.model.widget.MyFollowObject;
import com.wangdaye.mysplash.me.presenter.activity.PagerManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.me.presenter.activity.UserImplementor;
import com.wangdaye.mysplash.me.view.widget.MyFollowUserView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * My follow activity.
 * */

public class MyFollowActivity extends MysplashActivity
        implements UserView, PagerManageView, SwipeBackManageView,
        View.OnClickListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {
    // model.
    private UserModel userModel;
    private PagerManageModel pagerManageModel;

    // view.
    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private NestedScrollAppBarLayout appBar;
    private ViewPager viewPager;

    private PagerView[] pagers = new PagerView[2];
    private DisplayUtils utils;

    // presenter.
    private UserPresenter userPresenter;
    private ToolbarPresenter toolbarPresenter;
    private PagerManagePresenter pagerManagePresenter;
    private SwipeBackManagePresenter swipeBackManagePresenter;

    // data
    public static final String KEY_MY_FOLLOW_ACTIVITY_PAGE_POSITION = "my_follow_activity_page_position";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        initModel(savedInstanceState);
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView();
            userPresenter.requestUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userPresenter.getUser() != null) {
            User user = userPresenter.getUser();
            user.followers_count += ((MyFollowUserView) pagers[0]).getDeltaValue();
            user.following_count += ((MyFollowUserView) pagers[1]).getDeltaValue();
            if (AuthManager.getInstance().getUser() == null) {
                AuthManager.getInstance().updateUser(user);
            } else {
                User oldUser = AuthManager.getInstance().getUser();
                oldUser.followers_count = user.followers_count;
                oldUser.following_count = user.following_count;
                AuthManager.getInstance().updateUser(oldUser);
            }
        }
        for (PagerView p : pagers) {
            p.cancelRequest();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_MY_FOLLOW_ACTIVITY_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
    }

    @Override
    public void handleBackPressed() {
        if (pagerManagePresenter.needPagerBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        SwipeBackCoordinatorLayout.hideBackgroundShadow(container);
        finish();
        switch (dir) {
            case SwipeBackCoordinatorLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackCoordinatorLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.userPresenter = new UserImplementor(userModel, this);
        this.toolbarPresenter = new ToolbarImplementor();
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_my_follow_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_my_follow_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) findViewById(R.id.activity_my_follow_container);
        this.appBar = (NestedScrollAppBarLayout) findViewById(R.id.activity_my_follow_appBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_my_follow_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setTitle(getString(R.string.my_follow));
        toolbar.setNavigationOnClickListener(this);

        initPages();
        this.utils = new DisplayUtils(this);

        AnimUtils.animInitShow(
                (View) pagers[pagerManagePresenter.getPagerPosition()],
                400);
        for (PagerView p : pagers) {
            p.refreshPager();
        }
    }

    private void initPages() {
        List<View> pageList = new ArrayList<>();
        pageList.add(new MyFollowUserView(this, MyFollowObject.FOLLOW_TYPE_FOLLOWERS));
        pageList.add(new MyFollowUserView(this, MyFollowObject.FOLLOW_TYPE_FOLLOWING));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] myFollowTabs = getResources().getStringArray(R.array.my_follow_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, myFollowTabs);
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        this.viewPager = (ViewPager) findViewById(R.id.activity_my_follow_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_my_follow_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    /** <br> model. */

    private void initModel(Bundle savedInstanceState) {
        int page = 0;
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt(KEY_MY_FOLLOW_ACTIVITY_PAGE_POSITION, page);
        } else {
            page = getIntent().getIntExtra(KEY_MY_FOLLOW_ACTIVITY_PAGE_POSITION, page);
        }
        this.userModel = new UserObject();
        this.pagerManageModel = new PagerManageObject(page);
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    // on page change swipeListener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManagePresenter.setPagerPosition(position);
        if (AuthManager.getInstance().getState() != AuthManager.LOADING_ME_STATE) {
            pagerManagePresenter.checkToRefresh(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }

    // on swipe swipeListener.(swipe back swipeListener)

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // view.

    // user view.

    @Override
    public void drawUserInfo(User user) {
        String[] myFollowTabs = getResources().getStringArray(R.array.my_follow_tabs);
        MyPagerAdapter adapter = (MyPagerAdapter) viewPager.getAdapter();
        adapter.titleList.set(0, user.followers_count + " " + myFollowTabs[0]);
        adapter.titleList.set(1, user.following_count + " " + myFollowTabs[1]);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initRefreshStart() {
        // do nothing.
    }

    @Override
    public void followRequestSuccess(boolean follow) {
        // do nothing.
    }

    @Override
    public void followRequestFailed(boolean follow) {
        // do nothing.
    }

    // pager manage view.

    @Override
    public PagerView getPagerView(int position) {
        return pagers[position];
    }

    @Override
    public boolean canPagerSwipeBack(int position, int dir) {
        return pagers[position].canSwipeBack(dir);
    }

    @Override
    public int getPagerItemCount(int position) {
        return pagers[position].getItemCount();
    }

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight() + utils.dpToPx(48);
        } else {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }
}
