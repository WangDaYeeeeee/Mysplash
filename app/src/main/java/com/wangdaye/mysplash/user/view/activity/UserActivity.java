package com.wangdaye.mysplash.user.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.user.model.activity.BorwsableObject;
import com.wangdaye.mysplash.user.model.activity.PagerManageObject;
import com.wangdaye.mysplash.user.model.widget.PhotosObject;
import com.wangdaye.mysplash.user.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.user.presenter.activity.PagerManageImplementor;
import com.wangdaye.mysplash.user.presenter.activity.PopupManageImplementor;
import com.wangdaye.mysplash.user.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.user.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.user.view.widget.UserCollectionsView;
import com.wangdaye.mysplash.user.view.widget.UserPhotosView;
import com.wangdaye.mysplash.user.view.widget.UserProfileView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User activity.
 * */

public class UserActivity extends MysplashActivity
        implements PagerManageView, PopupManageView, SwipeBackManageView, BrowsableView,
        Toolbar.OnMenuItemClickListener, View.OnClickListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {
    // model.
    private PagerManageModel pagerManageModel;
    private BrowsableModel browsableModel;

    // view.
    private RequestBrowsableDataDialog requestDialog;

    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private NestedScrollAppBarLayout appBar;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private UserProfileView userProfileView;

    private PagerView[] pagers = new PagerView[3];
    private DisplayUtils utils;

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private PagerManagePresenter pagerManagePresenter;
    private PopupManagePresenter popupManagePresenter;
    private SwipeBackManagePresenter swipeBackManagePresenter;
    private BrowsablePresenter browsablePresenter;

    // data
    public static final String KEY_USER_ACTIVITY_USER = "user_activity_user";

    private final String KEY_USER_ACTIVITY_PAGE_POSITION = "user_activity_page_position";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initModel(savedInstanceState);
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browsablePresenter.cancelRequest();
        if (userProfileView != null) {
            userProfileView.cancelRequest();
        }
        for (PagerView p : pagers) {
            if (p != null) {
                p.cancelRequest();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_USER_ACTIVITY_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
        for (PagerView pager : pagers) {
            if (pager != null) {
                pager.writeBundle(outState);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Mysplash.getInstance().isActivityInBackstage()) {
            super.onBackPressed();
        } else if (pagerManagePresenter.needPagerBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
           finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_User);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_User);
        }
    }

    @Override
    protected void backToTop() {
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
    }

    @Override
    protected boolean needSetStatusBarTextDark() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        SwipeBackCoordinatorLayout.hideBackgroundShadow(container);
        if (!browsablePresenter.isBrowsable()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
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
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.popupManagePresenter = new PopupManageImplementor(this);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
    }

    /** <br> view. */

    // init.

    private void initView(boolean init) {
        if (init && browsablePresenter.isBrowsable()) {
            browsablePresenter.requestBrowsableData();
        } else {
            User u = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);

            this.container = (CoordinatorLayout) findViewById(R.id.activity_user_container);

            SwipeBackCoordinatorLayout swipeBackView
                    = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_user_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            this.statusBar = (StatusBarView) findViewById(R.id.activity_user_statusBar);
            if (DisplayUtils.isNeedSetStatusBarMask()) {
                statusBar.setBackgroundResource(R.color.colorPrimary_light);
                statusBar.setMask(true);
            }

            this.appBar = (NestedScrollAppBarLayout) findViewById(R.id.activity_user_appBar);

            this.toolbar = (Toolbar) findViewById(R.id.activity_user_toolbar);
            if (Mysplash.getInstance().isLightTheme()) {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_light);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
                }
                toolbar.inflateMenu(R.menu.activity_user_toolbar_light);
            } else {
                if (browsablePresenter.isBrowsable()) {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_home_dark);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
                }
                toolbar.inflateMenu(R.menu.activity_user_toolbar_dark);
            }
            toolbar.setOnMenuItemClickListener(this);
            toolbar.setNavigationOnClickListener(this);
            if (TextUtils.isEmpty(u.portfolio_url)) {
                toolbar.getMenu().getItem(0).setVisible(false);
            } else {
                toolbar.getMenu().getItem(0).setVisible(true);
            }

            CircleImageView avatar = (CircleImageView) findViewById(R.id.activity_user_avatar);
            Glide.with(this)
                    .load(u.profile_image.large)
                    .priority(Priority.HIGH)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(avatar);

            TextView title = (TextView) findViewById(R.id.activity_user_title);
            title.setText(u.name);

            this.userProfileView = (UserProfileView) findViewById(R.id.activity_user_profileView);
            initPages(u);

            userProfileView.setUser(u);
            userProfileView.requestUserProfile(adapter);
            this.utils = new DisplayUtils(this);

            AnimUtils.animInitShow(
                    (View) pagers[pagerManagePresenter.getPagerPosition()],
                    400);
            pagers[pagerManagePresenter.getPagerPosition()].refreshPager();
        }
    }

    private void initPages(User u) {
        List<View> pageList = new ArrayList<>();
        pageList.add(new UserPhotosView(this, getBundle(), u, PhotosObject.PHOTOS_TYPE_PHOTOS));
        pageList.add(new UserCollectionsView(this, u));
        pageList.add(new UserPhotosView(this, getBundle(), u, PhotosObject.PHOTOS_TYPE_LIKES));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new MyPagerAdapter(pageList, tabList);

        this.viewPager = (ViewPager) findViewById(R.id.activity_user_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_user_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    // interface.

    public void showPopup() {
        int page = pagerManagePresenter.getPagerPosition();
        popupManagePresenter.showPopup(
                this,
                toolbar,
                pagerManagePresenter.getPagerKey(page),
                page);
    }

    /** <br> model. */

    // init.

    private void initModel(Bundle savedInstanceState) {
        int page = 0;
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt(KEY_USER_ACTIVITY_PAGE_POSITION, page);
        }
        this.pagerManageModel = new PagerManageObject(page);
        this.browsableModel = new BorwsableObject(getIntent());
    }

    // interface.

    public User getUser() {
        return userProfileView.getUser();
    }

    public String getUserPortfolio() {
        return userProfileView.getUserPortfolio();
    }

    public boolean isBrowsable() {
        return browsablePresenter.isBrowsable();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (browsablePresenter.isBrowsable()) {
                    browsablePresenter.visitParentView();
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on page change listener.

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

    // on swipe listener.(swipe back listener)

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

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // view.

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

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        pagers[position].setKey(value);
        pagers[position].refreshPager();
    }

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

    // browsable view.

    @Override
    public void showRequestDialog() {
        requestDialog = new RequestBrowsableDataDialog();
        requestDialog.show(getFragmentManager(), null);
    }

    @Override
    public void dismissRequestDialog() {
        requestDialog.dismiss();
        requestDialog = null;
    }

    @Override
    public void drawBrowsableView() {
        User u = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
        if (AuthManager.getInstance().getUsername() != null
                && AuthManager.getInstance().getUsername().equals(u.username)) {
            AuthManager.getInstance().writeUserInfo(u);
            Intent intent = new Intent(this, MeActivity.class);
            intent.putExtra(MeActivity.EXTRA_BROWSABLE, true);
            startActivity(intent);
            finish();
        } else {
            initModel(getBundle());
            initPresenter();
            initView(false);
        }
    }

    @Override
    public void visitParentView() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
