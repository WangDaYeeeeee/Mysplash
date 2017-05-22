package com.wangdaye.mysplash.me.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.wangdaye.mysplash.common.data.entity.unsplash.Me;
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
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.PagerManageModel;
import com.wangdaye.mysplash.common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.i.view.PagerManageView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.me.model.activity.DownloadObject;
import com.wangdaye.mysplash.me.model.activity.PagerManageObject;
import com.wangdaye.mysplash.me.model.widget.PhotosObject;
import com.wangdaye.mysplash.me.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.me.presenter.activity.PagerManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.PopupManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.me.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.me.view.widget.MeCollectionsView;
import com.wangdaye.mysplash.me.view.widget.MePhotosView;
import com.wangdaye.mysplash.me.view.widget.MeProfileView;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Me activity.
 *
 * This activity is used to show information of the application user.
 *
 * */

public class MeActivity extends ReadWriteActivity
        implements PagerManageView, PopupManageView, SwipeBackManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, PhotoAdapter.OnDownloadPhotoListener,
        ViewPager.OnPageChangeListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SelectCollectionDialog.OnCollectionsChangedListener, AuthManager.OnAuthDataChangedListener {

    @BindView(R.id.activity_me_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_me_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_me_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.activity_me_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_me_avatar)
    CircleImageView avatar;

    @BindView(R.id.activity_me_title)
    TextView title;

    @BindView(R.id.activity_me_viewPager)
    ViewPager viewPager;

    private MyPagerAdapter adapter;

    private PagerView[] pagers = new PagerView[3];

    private ToolbarPresenter toolbarPresenter;

    private PagerManageModel pagerManageModel;
    private PagerManagePresenter pagerManagePresenter;

    private PopupManagePresenter popupManagePresenter;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    public static final String EXTRA_BROWSABLE = "browsable";

    public static final String KEY_ME_ACTIVITY_DELETE_COLLECTION = "me_activity_delete_collection";
    public static final String KEY_ME_ACTIVITY_COLLECTION = "me_activity_collection";
    public static final String KEY_ME_ACTIVITY_PAGE_POSITION = "me_activity_page_position";

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
        setContentView(R.layout.activity_me);
        initModel(savedInstanceState);
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initView();
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
                    ((MePhotosView) pagers[0]).updatePhoto(photo, false);
                    ((MePhotosView) pagers[1]).updatePhoto(photo, false);
                }
                break;

            case Mysplash.COLLECTION_ACTIVITY:
                Collection collection = data.getParcelableExtra(
                        CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION);
                if (collection != null) {
                    if (data.getBooleanExtra(KEY_ME_ACTIVITY_DELETE_COLLECTION, false)) {
                        // the collection was deleted.
                        if (AuthManager.getInstance().getMe() != null) {
                            AuthManager.getInstance().getMe().total_collections --;
                            drawTabTitles(AuthManager.getInstance().getMe());
                        }
                        ((MeCollectionsView) pagers[2]).removeCollection(collection);
                    } else {
                        ((MeCollectionsView) pagers[2]).updateCollection(collection, false);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        AuthManager.getInstance().cancelRequest();
        for (PagerView p : pagers) {
            p.cancelRequest();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Me);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Me);
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
            f.setPhotoList(((MePhotosView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            f.setLikeList(((MePhotosView) pagers[1]).getPhotos());
        }
        if (pagers[2] != null) {
            f.setCollectionList(((MeCollectionsView) pagers[2]).getCollections());
        }
        f.saveData(this);

        // write normal data.
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ME_ACTIVITY_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
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
        setResult(RESULT_OK);
        SwipeBackCoordinatorLayout.hideBackgroundShadow(container);
        if (Mysplash.getInstance().getActivityCount() > 1
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
            page = savedInstanceState.getInt(KEY_ME_ACTIVITY_PAGE_POSITION, page);
        } else {
            page = getIntent().getIntExtra(KEY_ME_ACTIVITY_PAGE_POSITION, page);
        }
        this.pagerManageModel = new PagerManageObject(page);
        this.downloadModel = new DownloadObject();
        AuthManager.getInstance().addOnWriteDataListener(this);
        if (AuthManager.getInstance().getState() == AuthManager.FREEDOM_STATE) {
            AuthManager.getInstance().requestPersonalProfile();
        }
    }

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
        this.popupManagePresenter = new PopupManageImplementor(this);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.downloadPresenter = new DownloadImplementor(downloadModel);
    }

    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_me_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        if (getIntent().getBooleanExtra(EXTRA_BROWSABLE, false)) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        ThemeManager.inflateMenu(
                toolbar, R.menu.activity_me_toolbar_light, R.menu.activity_me_toolbar_dark);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        avatar.setOnClickListener(new OnClickAvatarListener());

        initPages();
        drawProfile();
    }

    private void initPages() {
        List<View> pageList = new ArrayList<>();
        pageList.add(new MePhotosView(this, PhotosObject.PHOTOS_TYPE_PHOTOS, R.id.activity_me_page_photo));
        pageList.add(new MePhotosView(this, PhotosObject.PHOTOS_TYPE_LIKES, R.id.activity_me_page_like));
        pageList.add(new MeCollectionsView(this, R.id.activity_me_page_collection));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = ButterKnife.findById(this, R.id.activity_me_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        BaseSavedStateFragment f = SavedStateFragment.getData(this);
        if (f != null && f instanceof SavedStateFragment) {
            ((MePhotosView) pagers[0]).setPhotos(((SavedStateFragment) f).getPhotoList());
            ((MePhotosView) pagers[1]).setPhotos(((SavedStateFragment) f).getLikeList());
            ((MeCollectionsView) pagers[2]).setCollections(((SavedStateFragment) f).getCollectionList());

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

    private void drawTabTitles(@Nullable Me me) {
        if (me != null) {
            List<String> titleList = new ArrayList<>();
            titleList.add(
                    DisplayUtils.abridgeNumber(me.total_photos)
                            + " " + getResources().getStringArray(R.array.user_tabs)[0]);
            titleList.add(
                    DisplayUtils.abridgeNumber(me.total_likes)
                            + " " + getResources().getStringArray(R.array.user_tabs)[1]);
            titleList.add(
                    DisplayUtils.abridgeNumber(me.total_collections)
                            + " " + getResources().getStringArray(R.array.user_tabs)[2]);
            adapter.titleList = titleList;
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("SetTextI18n")
    private void drawProfile() {
        MeProfileView meProfileView = (MeProfileView) findViewById(R.id.activity_me_profileView);
        if (AuthManager.getInstance().getMe() != null) {
            Me me = AuthManager.getInstance().getMe();
            title.setText(me.first_name + " " + me.last_name);
            meProfileView.drawMeProfile(me);
            drawTabTitles(me);
        } else if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            title.setText(AuthManager.getInstance().getFirstName()
                    + " " + AuthManager.getInstance().getLastName());
        } else {
            title.setText("...");
        }

        if (AuthManager.getInstance().getUser() != null) {
            ImageHelper.loadAvatar(this, avatar, AuthManager.getInstance().getUser(), null);
        } else if (!TextUtils.isEmpty(AuthManager.getInstance().getAvatarPath())) {
            ImageHelper.loadAvatar(this, avatar, AuthManager.getInstance().getAvatarPath(), null);
        } else {
            ImageHelper.loadAvatar(this, avatar, new User(), null);
        }
    }

    // control.

    public void showPopup(boolean filter) {
        if (filter) {
            int page = pagerManagePresenter.getPagerPosition();
            popupManagePresenter.showPopup(
                    this,
                    toolbar,
                    pagerManagePresenter.getPagerKey(page),
                    page);
        } else {
            popupManagePresenter.showPopup(
                    this,
                    toolbar,
                    null,
                    -1);
        }
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
                if (getIntent().getBooleanExtra(EXTRA_BROWSABLE, false)) {
                    IntentHelper.startMainActivity(this);
                }
                toolbarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    private class OnClickAvatarListener implements View.OnClickListener {

        // interface.

        @Override
        public void onClick(View v) {
            if (AuthManager.getInstance().getUser() != null) {
                IntentHelper.startPreviewActivity(
                        MeActivity.this, AuthManager.getInstance().getUser(), false);
            }
        }
    }

    @OnClick(R.id.activity_me_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()
                && !TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            ProfileDialog dialog = new ProfileDialog();
            dialog.setUsername(AuthManager.getInstance().getUsername());
            dialog.show(getFragmentManager(), null);
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return toolbarPresenter.touchMenuItem(this, item.getItemId());
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
        if (AuthManager.getInstance().getState() != AuthManager.LOADING_ME_STATE) {
            pagerManagePresenter.checkToRefresh(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }

    // on swipe listener. (swipe back listener)

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
        if (AuthManager.getInstance().getMe() != null) {
            AuthManager.getInstance().getMe().total_collections ++;
            drawTabTitles(AuthManager.getInstance().getMe());
        }
        ((MeCollectionsView) pagers[2]).addCollection(c);
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        ((MePhotosView) pagers[0]).updatePhoto(p, true);
        ((MePhotosView) pagers[1]).updatePhoto(p, true);
        ((MeCollectionsView) pagers[2]).updateCollection(c, true);
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
        // do nothing.
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
        if (AuthManager.getInstance().getState() != AuthManager.LOADING_ME_STATE) {
            pagers[position].refreshPager();
        }
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
}
