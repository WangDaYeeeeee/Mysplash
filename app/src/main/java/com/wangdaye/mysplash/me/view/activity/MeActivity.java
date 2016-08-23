package com.wangdaye.mysplash.me.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.i.view.ToolbarView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash.me.model.activity.PagerManageObject;
import com.wangdaye.mysplash.me.model.widget.PhotosObject;
import com.wangdaye.mysplash.me.presenter.activity.PagerManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.PopupManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.me.view.widget.MeCollectionsView;
import com.wangdaye.mysplash.me.view.widget.MePhotosView;
import com.wangdaye.mysplash.me.view.widget.MeProfileView;

import java.util.ArrayList;
import java.util.List;

/**
 * Me activity.
 * */

public class MeActivity extends MysplashActivity
        implements ToolbarView, PagerManageView, PopupManageView,
        Toolbar.OnMenuItemClickListener, View.OnClickListener, ViewPager.OnPageChangeListener,
        SwipeBackLayout.OnSwipeListener, AuthManager.OnAuthDataChangedListener {
    // model.
    private PagerManageModel pagerManageModel;
    public static final int COLLECTION_ACTIVITY = 1;

    // view.
    private AppBarLayout appBar;
    private Toolbar toolbar;
    private CircleImageView avatar;
    private TextView title;
    private MyPagerAdapter adapter;
    private MeProfileView meProfileView;

    private PagerView[] pagers = new PagerView[3];
    private DisplayUtils utils;

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private PagerManagePresenter pagerManagePresenter;
    private PopupManagePresenter popupManagePresenter;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initModel();
            initView();
            initPresenter();
            animShowView((View) pagers[0], 400);
            pagers[0].refreshPager();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        for (PagerView p : pagers) {
            p.cancelRequest();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Me);
        } else {
            setTheme(R.style.MysplashTheme_dark_Me);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COLLECTION_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra(CollectionActivity.DELETE_COLLECTION, false)) {
                        meProfileView.cutCollection(adapter);
                        ((MeCollectionsView) pagers[1]).removeCollection(Mysplash.getInstance().getCollection());
                    } else {
                        ((MeCollectionsView) pagers[1]).changeCollection(Mysplash.getInstance().getCollection());
                    }
                }
                break;
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.popupManagePresenter = new PopupManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_me_swipeBackLayout);
        swipeBackLayout.setOnSwipeListener(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_me_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        this.appBar = (AppBarLayout) findViewById(R.id.activity_me_appBar);

        this.toolbar = (Toolbar) findViewById(R.id.activity_me_toolbar);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            toolbar.inflateMenu(R.menu.activity_me_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.inflateMenu(R.menu.activity_me_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.avatar = (CircleImageView) findViewById(R.id.activity_me_avatar);
        this.title = (TextView) findViewById(R.id.activity_me_title);
        this.meProfileView = (MeProfileView) findViewById(R.id.activity_me_profileView);

        initPages();
        this.utils = new DisplayUtils(this);

        if (AuthManager.getInstance().getState() == AuthManager.FREEDOM_STATE) {
            AuthManager.getInstance().refreshPersonalProfile();
        }
        drawProfile();
    }

    private void initPages() {
        List<View> pageList = new ArrayList<>();
        pageList.add(new MePhotosView(this, PhotosObject.PHOTOS_TYPE_PHOTOS));
        pageList.add(new MeCollectionsView(this));
        pageList.add(new MePhotosView(this, PhotosObject.PHOTOS_TYPE_LIKES));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        List<String> tabList = new ArrayList<>();
        tabList.add("PHOTOS");
        tabList.add("COLLECTIONS");
        tabList.add("LIKES");
        this.adapter = new MyPagerAdapter(pageList, tabList);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_me_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_me_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    @SuppressLint("SetTextI18n")
    private void drawProfile() {
        if (AuthManager.getInstance().getMe() != null) {
            Me me = AuthManager.getInstance().getMe();
            title.setText(me.first_name + " " + me.last_name);
            meProfileView.drawMeProfile(me, adapter);
        } else if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            title.setText(AuthManager.getInstance().getFirstName()
                    + " " + AuthManager.getInstance().getLastName());
        } else {
            title.setText("...");
        }

        if (AuthManager.getInstance().getUser() != null) {
            Glide.with(this)
                    .load(AuthManager.getInstance().getUser().profile_image.large)
                    .priority(Priority.HIGH)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(avatar);
        } else if (!TextUtils.isEmpty(AuthManager.getInstance().getAvatarPath())) {
            Glide.with(this)
                    .load(AuthManager.getInstance().getAvatarPath())
                    .priority(Priority.HIGH)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(avatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .priority(Priority.HIGH)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(avatar);
        }
    }

    // interface.

    public void addCollection(Collection c) {
        meProfileView.addCollection(adapter);
        ((MeCollectionsView) pagers[1]).addCollection(c);
    }

    public void changeCollection(Collection c) {
        ((MeCollectionsView) pagers[1]).changeCollection(c);
    }

    /** <br> model. */

    private void initModel() {
        this.pagerManageModel = new PagerManageObject(0);
        AuthManager.getInstance().addOnWriteDataListener(this);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        toolbarPresenter.touchMenuItem(item.getItemId());
        return true;
    }

    // on page change listener.

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

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(int dir) {
        if (pagerManagePresenter.getPagerItemCount() <= 0) {
            return true;
        }
        if (dir == SwipeBackLayout.UP_DIR) {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight() + utils.dpToPx(48);
        } else {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    // on author data changed listener.

    @Override
    public void onWriteAccessToken() {
        drawProfile();
    }

    @Override
    public void onWriteUserInfo() {
        drawProfile();
        pagerManagePresenter.checkToRefresh(pagerManagePresenter.getPagerPosition());
    }

    @Override
    public void onWriteAvatarPath() {
        drawProfile();
    }

    @Override
    public void onLogout() {
        drawProfile();
        meProfileView.setLoading();
        for (PagerView pager : pagers) {
            pager.refreshPager();
        }
    }

    // view.

    // toolbar view.

    @Override
    public void touchNavigatorIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    public void touchToolbar() {
        // do nothing.
    }

    @Override
    public void touchMenuItem(int itemId) {
        switch (itemId) {/*
            case R.id.action_update_profile:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    // TODO: 2016/8/19 update activity.
                }
                break;
*/
            case R.id.action_open_portfolio:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    String url = AuthManager.getInstance().getMe().portfolio_url;
                    if (!TextUtils.isEmpty(url)) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(i);
                    } else {
                        Toast.makeText(
                                this,
                                getString(R.string.feedback_portfolio_is_null),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.action_filter:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    int page = pagerManagePresenter.getPagerPosition();
                    popupManagePresenter.showPopup(
                            this,
                            toolbar,
                            pagerManagePresenter.getPageKey(page),
                            page);
                }
                break;
        }
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

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        pagers[position].setKey(value);
        if (AuthManager.getInstance().getState() != AuthManager.LOADING_ME_STATE) {
            pagers[position].refreshPager();
        }
    }
}
