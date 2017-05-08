package com.wangdaye.mysplash.user.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash.common._basic.ReadWriteActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.ProfileDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.common.i.model.PagerManageModel;
import com.wangdaye.mysplash.common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.i.view.BrowsableView;
import com.wangdaye.mysplash.common.i.view.PagerManageView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.dialog.RequestBrowsableDataDialog;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.me.view.activity.MeActivity;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User activity.
 *
 * This activity is used to show the information of a user.
 *
 * */

public class UserActivity extends ReadWriteActivity
        implements PagerManageView, PopupManageView, SwipeBackManageView, BrowsableView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, UserProfileView.OnRequestUserListener,
        PhotoAdapter.OnDownloadPhotoListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.activity_user_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_user_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_user_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.activity_user_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_user_profileView)
    UserProfileView userProfileView;

    @BindView(R.id.activity_user_viewPager)
    ViewPager viewPager;

    private MyPagerAdapter adapter;

    private PagerView[] pagers = new PagerView[3];

    private RequestBrowsableDataDialog requestDialog;

    private ToolbarPresenter toolbarPresenter;

    private PagerManageModel pagerManageModel;
    private PagerManagePresenter pagerManagePresenter;

    private PopupManagePresenter popupManagePresenter;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    private BrowsableModel browsableModel;
    private BrowsablePresenter browsablePresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    public static final String KEY_USER_ACTIVITY_USER = "user_activity_user";
    public static final String KEY_USER_ACTIVITY_PAGE_POSITION = "user_activity_page_position";

    public static final int PAGE_PHOTO = 0;
    public static final int PAGE_LIKE = 1;
    public static final int PAGE_COLLECTION = 2;
    @IntDef({PAGE_PHOTO, PAGE_LIKE, PAGE_COLLECTION})
    public @interface UserPageRule {}

    public static class SavedStateFragment extends BaseSavedStateFragment {

        private List<Photo> photoList;
        private List<Collection> collectionList;
        private List<Photo> likeList;

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
            ButterKnife.bind(this);
            initView(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case Mysplash.PHOTO_ACTIVITY:
                Photo photo = data.getParcelableExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO);
                if (photo != null) {
                    ((UserPhotosView) pagers[0]).updatePhoto(photo, false);
                    ((UserPhotosView) pagers[1]).updatePhoto(photo, false);
                }
                break;

            case Mysplash.COLLECTION_ACTIVITY:
                Collection collection = data.getParcelableExtra(
                        CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION);
                if (collection != null) {
                    ((UserCollectionsView) pagers[2]).updateCollection(collection, false);
                }
                break;
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
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_User);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_User);
        }
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
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
    protected void backToTop() {
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

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

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.popupManagePresenter = new PopupManageImplementor(this);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.browsablePresenter = new BrowsableImplementor(browsableModel, this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
    }

    private void initView(boolean init) {
        User u = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);

        if (init && browsablePresenter.isBrowsable() && u == null) {
            browsablePresenter.requestBrowsableData();
        } else {
            SwipeBackCoordinatorLayout swipeBackView
                    = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_user_swipeBackView);
            swipeBackView.setOnSwipeListener(this);

            if (browsablePresenter.isBrowsable()) {
                ThemeManager.setNavigationIcon(
                        toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
            } else {
                ThemeManager.setNavigationIcon(
                        toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
            }
            ThemeManager.inflateMenu(
                    toolbar, R.menu.activity_user_toolbar_light, R.menu.activity_user_toolbar_dark);
            toolbar.setOnMenuItemClickListener(this);
            toolbar.setNavigationOnClickListener(this);
            if (TextUtils.isEmpty(u.portfolio_url)) {
                toolbar.getMenu().getItem(0).setVisible(false);
            } else {
                toolbar.getMenu().getItem(0).setVisible(true);
            }

            CircleImageView avatar = ButterKnife.findById(this, R.id.activity_user_avatar);
            avatar.setOnClickListener(new OnClickAvatarListener(u));
            ImageHelper.loadAvatar(this, avatar, u, null);

            TextView title = ButterKnife.findById(this, R.id.activity_user_title);
            title.setText(u.name);

            initPages(u);

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

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition());
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = ButterKnife.findById(this, R.id.activity_user_tabLayout);
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

    // control.

    public User getUser() {
        return userProfileView.getUser();
    }

    public String getUserPortfolio() {
        return userProfileView.getUserPortfolio();
    }

    public void showPopup() {
        int page = pagerManagePresenter.getPagerPosition();
        popupManagePresenter.showPopup(
                this,
                toolbar,
                pagerManagePresenter.getPagerKey(page),
                page);
    }

    public boolean isBrowsable() {
        return browsablePresenter.isBrowsable();
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(int requestCode) {
        downloadPresenter.download(this);
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                if (browsablePresenter.isBrowsable()) {
                    browsablePresenter.visitPreviousPage();
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    @OnClick(R.id.activity_user_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()) {
            User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
            ProfileDialog dialog = new ProfileDialog();
            dialog.setUsername(user.username);
            dialog.show(getFragmentManager(), null);
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

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on request user listener.

    @Override
    public void onRequestUserSucceed(User u) {
        getIntent().putExtra(KEY_USER_ACTIVITY_USER, u);
    }

    // on download photo listener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestReadWritePermission();
        }
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

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        for (PagerView pager : pagers) {
            if (pager instanceof UserPhotosView) {
                ((UserPhotosView) pager).updatePhoto(p, true);
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
                    && appBar.getY() <= -appBar.getMeasuredHeight()
                    + getResources().getDimensionPixelSize(R.dimen.tab_layout_height);
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
    public void visitPreviousPage() {
        IntentHelper.startMainActivity(this);
    }
}
