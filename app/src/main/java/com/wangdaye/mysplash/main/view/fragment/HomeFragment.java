package com.wangdaye.mysplash.main.view.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.PagerManageModel;
import com.wangdaye.mysplash.common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common._basic.MysplashFragment;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.i.view.PagerManageView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.fragment.PagerManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.HomeFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.PagerManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.ToolbarImplementor;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.HomeCollectionsView;
import com.wangdaye.mysplash.main.view.widget.HomePhotosView;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Home fragment.
 *
 * This fragment is used to show the home page of Mysplash.
 *
 * */

public class HomeFragment extends MysplashFragment
        implements PopupManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener/*,
        UserNotificationManager.OnUpdateNotificationListener*/ {

    @BindView(R.id.fragment_home_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.fragment_home_container)
    CoordinatorLayout container;

    @BindView(R.id.fragment_home_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.fragment_home_toolbar)
    Toolbar toolbar;

    @BindView(R.id.container_notification_bar_button)
    ImageButton bellBtn;

    @BindView(R.id.container_notification_bar_unreadFlag)
    ImageView redDot;

    @BindView(R.id.fragment_home_viewPager)
    ViewPager viewPager;

    @BindView(R.id.fragment_home_indicator)
    AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[3];

    private ToolbarPresenter toolbarPresenter;

    private HomeFragmentPopupManageImplementor popupManageImplementor;

    private PagerManageModel pagerManageModel;
    private PagerManagePresenter pagerManagePresenter;

    // private NotificationBarPresenter notificationBarPresenter;

    private final String KEY_HOME_FRAGMENT_PAGE_POSITION = "home_fragment_page_position";
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthManager.getInstance()
                .getNotificationManager()
                .addOnUpdateNotificationListener(this);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initModel(savedInstanceState);
        initPresenter();
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        AuthManager.getInstance()
                .getNotificationManager()
                .removeOnUpdateNotificationListener(this);*/
        for (PagerView p : pagers) {
            if (p != null) {
                p.cancelRequest();
            }
        }
    }

    @Override
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }
/*
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            notificationBarPresenter.setVisible(bellBtn, redDot);
        }
    }
*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_HOME_FRAGMENT_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
        for (PagerView p : pagers) {
            p.onSaveInstanceState(outState);
        }
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (pagers[0] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeNewList(
                    ((HomePhotosView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeFeaturedList(
                    ((HomePhotosView) pagers[1]).getPhotos());
        }
        if (pagers[2] != null) {
            ((MainActivity.SavedStateFragment) outState).setHomeCollectionList(
                    ((HomeCollectionsView) pagers[2]).getCollections());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (pagers[0] != null) {
            ((HomePhotosView) pagers[0]).setPhotos(
                    ((MainActivity.SavedStateFragment) savedInstanceState).getHomeNewList());
        }
        if (pagers[1] != null) {
            ((HomePhotosView) pagers[1]).setPhotos(
                    ((MainActivity.SavedStateFragment) savedInstanceState).getHomeFeaturedList());
        }
        if (pagers[2] != null) {
            ((HomeCollectionsView) pagers[2]).setCollections(
                    ((MainActivity.SavedStateFragment) savedInstanceState).getHomeCollectionList());
        }
    }

    @Override
    public boolean needBackToTop() {
        return pagerManagePresenter.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Mysplash.PHOTO_ACTIVITY:
                Photo photo = data.getParcelableExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO);
                if (photo != null) {
                    ((HomePhotosView) pagers[0]).updatePhoto(photo);
                    ((HomePhotosView) pagers[1]).updatePhoto(photo);
                }
                break;

            case Mysplash.COLLECTION_ACTIVITY:
                Collection collection = data.getParcelableExtra(
                        CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION);
                if (collection != null) {
                    ((HomeCollectionsView) pagers[2]).updateCollection(collection);
                }
                break;
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel(Bundle savedInstanceState) {
        this.pagerManageModel = new PagerManageObject(
                savedInstanceState == null ?
                        0 : savedInstanceState.getInt(KEY_HOME_FRAGMENT_PAGE_POSITION, 0));
    }

    private void initView(View v, Bundle savedInstanceState) {
        appBar.setOnNestedScrollingListener(this);

        ThemeManager.inflateMenu(
                toolbar, R.menu.fragment_home_toolbar_light, R.menu.fragment_home_toolbar_dark);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        TextView title = ButterKnife.findById(v, R.id.container_notification_bar_title);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            title.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getContext().getResources().getDimension(R.dimen.subtitle_text_size));
        } else {
            title.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    getContext().getResources().getDimension(R.dimen.large_title_text_size));
        }
        TextPaint paint = title.getPaint();
        paint.setFakeBoldText(true);

        bellBtn.setAlpha(0F);
        bellBtn.setEnabled(false);
        redDot.setAlpha(0F);
        redDot.setEnabled(false);
/*
        notificationBarPresenter.setVisible(bellBtn, redDot);
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getNumericId() > 0) {
            AuthManager.getInstance().getNotificationManager().requestPersonalNotifications();
        }
*/
        initPages(v, savedInstanceState);
    }

    private void initPages(View v, Bundle savedInstanceState) {
        List<View> pageList = new ArrayList<>();
        pageList.add(
                new HomePhotosView(
                        (MainActivity) getActivity(),
                        Mysplash.CATEGORY_TOTAL_NEW,
                        R.id.fragment_home_page_new));
        pageList.add(
                new HomePhotosView(
                        (MainActivity) getActivity(),
                        Mysplash.CATEGORY_TOTAL_FEATURED,
                        R.id.fragment_home_page_featured));
        pageList.add(
                new HomeCollectionsView(
                        (MysplashActivity) getActivity(),
                        R.id.fragment_home_page_collection));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] homeTabs = getResources().getStringArray(R.array.home_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, homeTabs);

        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = ButterKnife.findById(v, R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        if (savedInstanceState == null) {
            for (PagerView pager : pagers) {
                pager.refreshPager();
            }
        } else {
            for (PagerView pager : pagers) {
                pager.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.popupManageImplementor = new HomeFragmentPopupManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        // this.notificationBarPresenter = new HomeFragmentNotificationBarImplementor();
    }

    // control.

    public void showPopup() {
        int page = pagerManagePresenter.getPagerPosition();
        popupManageImplementor.showPopup(
                getActivity(),
                toolbar,
                pagerManagePresenter.getPagerKey(page),
                page);
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

    @OnClick(R.id.fragment_home_toolbar) void clickToolbar() {
        toolbarPresenter.touchToolbar((MysplashActivity) getActivity());
    }
/*
    @OnClick(R.id.container_notification_bar_button) void clickBellBtn() {
        IntentHelper.startNotificationActivity((MysplashActivity) getActivity());
    }
*/
    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
    }

    // on page changed listener.

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
        if (appBar.getY() <= -appBar.getMeasuredHeight()) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    indicator.setDisplayState(true);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    indicator.setDisplayState(false);
                    break;
            }
        }
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on update notification listener.
/*
    @Override
    public void onRequestNotificationSucceed(List<NotificationResult> resultList) {
        notificationBarPresenter.setImage(bellBtn, redDot);
    }

    @Override
    public void onRequestNotificationFailed() {
        // do nothing.
    }

    @Override
    public void onAddNotification(NotificationResult result, int position) {
        // do nothing.
    }

    @Override
    public void onClearNotification() {
        notificationBarPresenter.setVisible(bellBtn, redDot);
    }

    @Override
    public void onSetLatestTime() {
        notificationBarPresenter.setVisible(bellBtn, redDot);
    }
*/
    // view.

    // pager manage view.

    @Override
    public PagerView getPagerView(int position) {
        return pagers[position];
    }

    @Override
    public boolean canPagerSwipeBack(int position, int dir) {
        return false;
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
}
