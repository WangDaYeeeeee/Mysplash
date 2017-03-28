package com.wangdaye.mysplash.user.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
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
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.user.model.activity.BorwsableObject;
import com.wangdaye.mysplash.user.model.activity.DownloadObject;
import com.wangdaye.mysplash.user.model.activity.PagerManageObject;
import com.wangdaye.mysplash.user.model.widget.PhotosObject;
import com.wangdaye.mysplash.user.presenter.activity.BrowsableImplementor;
import com.wangdaye.mysplash.user.presenter.activity.DownloadImplementor;
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
        View.OnClickListener, Toolbar.OnMenuItemClickListener, UserProfileView.OnRequestUserListener,
        PhotoAdapter.OnDownloadPhotoListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, SelectCollectionDialog.OnCollectionsChangedListener {
    // model.
    private PagerManageModel pagerManageModel;
    private BrowsableModel browsableModel;
    private DownloadModel downloadModel;

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
    private DownloadPresenter downloadPresenter;

    // data
    public static final String KEY_USER_ACTIVITY_USER = "user_activity_user";
    public static final String KEY_USER_ACTIVITY_PAGE_POSITION = "user_activity_page_position";

    public static final int PAGE_PHOTO = 0;
    public static final int PAGE_LIKE = 1;

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
        // write large data.
        SavedStateFragment f = new SavedStateFragment();
        if (pagers[0] != null) {
            f.setPhotoList(((UserPhotosView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            f.setLikeList(((UserPhotosView) pagers[1]).getPhotos());
        }
        if (pagers[2] != null) {
            f.setCollectionList(((UserCollectionsView) pagers[2]).getCollections());
        }
        f.saveData(this);

        // write normal data.
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_USER_ACTIVITY_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
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
    protected boolean isFullScreen() {
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

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.popupManagePresenter = new PopupManageImplementor(this);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
    }

    /** <br> view. */

    // init.

    private void initView(boolean init) {
        User u = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);

        if (init && browsablePresenter.isBrowsable() && u == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            this.utils = new DisplayUtils(this);

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
            ImageHelper.loadAvatar(this, avatar, u, null);
            avatar.setOnClickListener(new OnClickAvatarListener(u));

            TextView title = (TextView) findViewById(R.id.activity_user_title);
            title.setText(u.name);
            if (AuthManager.getInstance().isAuthorized()) {
                title.setOnClickListener(this);
            }

            initPages(u);

            this.userProfileView = (UserProfileView) findViewById(R.id.activity_user_profileView);
            userProfileView.setOnRequestUserListener(this);
            userProfileView.setUser(u, adapter);
            if (u.complete) {
                userProfileView.drawUserInfo(u);
            } else {
                userProfileView.requestUserProfile();
            }
        }
    }

    private void initPages(User u) {
        List<View> pageList = new ArrayList<>();
        pageList.add(new UserPhotosView(this, u, PhotosObject.PHOTOS_TYPE_PHOTOS, R.id.activity_user_page_photo));
        pageList.add(new UserPhotosView(this, u, PhotosObject.PHOTOS_TYPE_LIKES, R.id.activity_user_page_like));
        pageList.add(new UserCollectionsView(this, u, R.id.activity_user_page_collection));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new MyPagerAdapter(pageList, tabList);

        this.viewPager = (ViewPager) findViewById(R.id.activity_user_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition());
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_user_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        BaseSavedStateFragment f = SavedStateFragment.getData(this);
        if (f != null && f instanceof SavedStateFragment) {
            ((UserPhotosView) pagers[0]).setPhotos(((SavedStateFragment) f).getPhotoList());
            ((UserPhotosView) pagers[1]).setPhotos(((SavedStateFragment) f).getLikeList());
            ((UserCollectionsView) pagers[2]).setCollections(((SavedStateFragment) f).getCollectionList());

            if (getBundle() != null) {
                for (PagerView pager : pagers) {
                    pager.onRestoreInstanceState(getBundle());
                }
            }
        } else {
            AnimUtils.animInitShow(
                    (View) pagers[pagerManagePresenter.getPagerPosition()],
                    400);
            for (PagerView pager : pagers) {
                pager.refreshPager();
            }
        }
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
        } else {
            page = getIntent().getIntExtra(KEY_USER_ACTIVITY_PAGE_POSITION, page);
        }
        this.pagerManageModel = new PagerManageObject(page);
        this.browsableModel = new BorwsableObject(getIntent());
        this.downloadModel = new DownloadObject();
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

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    downloadPresenter.download(this);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadPresenter.download(this);
                    } else {
                        NotificationHelper.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (browsablePresenter.isBrowsable()) {
                    browsablePresenter.visitParentView();
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;

            case R.id.activity_user_title:
                if (AuthManager.getInstance().isAuthorized()) {
                    User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
                    ProfileDialog dialog = new ProfileDialog();
                    dialog.setUsername(user.username);
                    dialog.show(getFragmentManager(), null);
                }
                break;
        }
    }

    private class OnClickAvatarListener implements View.OnClickListener {
        // data
        private User user;

        // life cycle.

        OnClickAvatarListener(User user) {
            this.user = user;
        }

        // interface.

        @Override
        public void onClick(View v) {
            IntentHelper.startPreviewActivity(UserActivity.this, user, false);
        }
    }

    // on menu item click swipeListener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on request user listener.

    @Override
    public void onRequestUserSucceed(User u) {
        getIntent().putExtra(KEY_USER_ACTIVITY_USER, u);
    }

    // on download photo swipeListener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_TYPE);
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
        pagerManagePresenter.checkToRefresh(position);
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

    // on collections changed swipeListener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        for (PagerView pager : pagers) {
            if (pager instanceof UserPhotosView) {
                ((UserPhotosView) pager).updatePhoto(p);
            }
        }
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
    public void drawBrowsableView(Object result) {
        User u = (User) result;
        getIntent().putExtra(KEY_USER_ACTIVITY_USER, u);
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
        IntentHelper.startMainActivity(this);
    }

    /** <br> inner class. */

    public static class SavedStateFragment extends BaseSavedStateFragment {
        // data
        private List<Photo> photoList;
        private List<Collection> collectionList;
        private List<Photo> likeList;

        // data.

        public List<Photo> getPhotoList() {
            return photoList;
        }

        public void setPhotoList(List<Photo> photoList) {
            this.photoList = photoList;
        }

        public List<Collection> getCollectionList() {
            return collectionList;
        }

        public void setCollectionList(List<Collection> collectionList) {
            this.collectionList = collectionList;
        }

        public List<Photo> getLikeList() {
            return likeList;
        }

        public void setLikeList(List<Photo> likeList) {
            this.likeList = likeList;
        }
    }
}
