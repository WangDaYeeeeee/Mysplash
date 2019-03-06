package com.wangdaye.mysplash.search.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.utils.presenter.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerViewManagePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.download.imp.DownloaderService;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.search.vm.CollectionSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.PhotoSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.SearchActivityModel;
import com.wangdaye.mysplash.search.vm.UserSearchPageViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Search activity.
 * 
 * This activity is used to search something.
 * 
 * */

public class SearchActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, PhotoAdapter.ItemEventCallback {

    @BindView(R.id.activity_search_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_search_container) CoordinatorLayout container;
    @BindView(R.id.activity_search_shadow) View shadow;

    @BindView(R.id.activity_search_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R.id.activity_search_editText) EditText editText;

    @BindView(R.id.activity_search_viewPager) ViewPager viewPager;
    @BindView(R.id.activity_search_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];

    private SearchActivityModel activityModel;
    private PhotoSearchPageViewModel photoPagerModel;
    private CollectionSearchPageViewModel collectionPagerModel;
    private UserSearchPageViewModel userPagerModel;
    @Inject DaggerViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    public static final String KEY_SEARCH_ACTIVITY_QUERY = "search_activity_query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().finishSameActivity(getClass());
        }

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(() -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                showKeyboard();
            } else {
                hideKeyboard();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyboard();
    }

    @Override
    protected void setTheme() {
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
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
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return ((PhotoSearchPageView) pagers[photoPage()]).loadMore(list, headIndex, headDirection);
    }

    // update data.

    @Override
    public void updatePhoto(@NonNull Photo photo, Mysplash.MessageType type) {
        ((PhotoSearchPageView) pagers[photoPage()]).updateItem(photo, true);
    }

    @Override
    public void updateUser(@NonNull User user, Mysplash.MessageType type) {
        ((UserSearchPageView) pagers[userPage()]).updateItem(user, true);
    }

    @Override
    public void updateCollection(@NonNull Collection collection, Mysplash.MessageType type) {
        switch (type) {
            case UPDATE:
                ((CollectionSearchPageView) pagers[collectionPage()]).updateItem(collection, true);
                break;

            case DELETE:
                ((CollectionSearchPageView) pagers[collectionPage()]).removeCollection(collection);
                break;
        }
    }

    // init.

    private void initModel() {
        String query = getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY);
        if (query == null) {
            query = "";
        }

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(SearchActivityModel.class);
        activityModel.init(photoPage(), query);

        photoPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PhotoSearchPageViewModel.class);
        photoPagerModel.init(
                ListResource.refreshError(new ArrayList<>(), 0, Mysplash.DEFAULT_PER_PAGE),
                query);

        collectionPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CollectionSearchPageViewModel.class);
        collectionPagerModel.init(
                ListResource.refreshError(new ArrayList<>(), 0, Mysplash.DEFAULT_PER_PAGE),
                query);

        userPagerModel = ViewModelProviders.of(this, viewModelFactory).get(UserSearchPageViewModel.class);
        userPagerModel.init(
                ListResource.refreshError(new ArrayList<>(), 0, Mysplash.DEFAULT_PER_PAGE),
                query);
    }

    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_search_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = findViewById(R.id.activity_search_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.inflateMenu(R.menu.activity_search_toolbar);
        toolbar.setNavigationOnClickListener(v -> finishSelf(true));
        toolbar.setOnMenuItemClickListener(this);

        editText.setOnEditorActionListener(this);

        initPages();

        activityModel.getSearchQuery().observe(this, s -> {
            if (!TextUtils.equals(s, editText.getText().toString())) {
                editText.setText(s);
            }
            if (!TextUtils.equals(s, photoPagerModel.getQuery())) {
                photoPagerModel.setQuery(s);
                PagerViewManagePresenter.initRefresh(photoPagerModel, pagers[photoPage()]);
            }
            if (!TextUtils.equals(s, collectionPagerModel.getQuery())) {
                collectionPagerModel.setQuery(s);
                PagerViewManagePresenter.initRefresh(collectionPagerModel, pagers[collectionPage()]);
            }
            if (!TextUtils.equals(s, userPagerModel.getQuery())) {
                userPagerModel.setQuery(s);
                PagerViewManagePresenter.initRefresh(userPagerModel, pagers[userPage()]);
            }
        });
    }

    private void initPages() {
        List<View> pageList = new ArrayList<>();
        pageList.add(
                new PhotoSearchPageView(
                        this, R.id.activity_search_page_photo,
                        Objects.requireNonNull(photoPagerModel.getListResource().getValue()).dataList,
                        getCurrentPagerPosition() == photoPage(),
                        photoPage(),
                        this).setOnClickListenerForFeedbackView(v -> hideKeyboard()));
        pageList.add(
                new CollectionSearchPageView(
                        this, R.id.activity_search_page_collection,
                        Objects.requireNonNull(collectionPagerModel.getListResource().getValue()).dataList,
                        getCurrentPagerPosition() == collectionPage(),
                        collectionPage(),
                        this).setOnClickListenerForFeedbackView(v -> hideKeyboard()));
        pageList.add(
                new UserSearchPageView(
                        this, R.id.activity_search_page_user,
                        Objects.requireNonNull(userPagerModel.getListResource().getValue()).dataList,
                        getCurrentPagerPosition() == userPage(),
                        userPage(),
                        this).setOnClickListenerForFeedbackView(v -> hideKeyboard()));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] searchTabs = getResources().getStringArray(R.array.search_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_search_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        activityModel.getPagerPosition().observe(this, position -> {
            for (int i = photoPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }
            DisplayUtils.setNavigationBarStyle(
                    this,
                    pagers[position].getState() == PagerView.State.NORMAL,
                    true);
            if (position == photoPage()
                    && photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().dataList.size() == 0
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED
                    && !TextUtils.isEmpty(photoPagerModel.getQuery())) {
                PagerViewManagePresenter.initRefresh(photoPagerModel, pagers[position]);
            } else if (position == collectionPage()
                    && collectionPagerModel.getListResource().getValue() != null
                    && collectionPagerModel.getListResource().getValue().dataList.size() == 0
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED
                    && !TextUtils.isEmpty(collectionPagerModel.getQuery())) {
                PagerViewManagePresenter.initRefresh(collectionPagerModel, pagers[position]);
            } else if (position == userPage()
                    && userPagerModel.getListResource().getValue() != null
                    && userPagerModel.getListResource().getValue().dataList.size() == 0
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED
                    && !TextUtils.isEmpty(userPagerModel.getQuery())) {
                PagerViewManagePresenter.initRefresh(userPagerModel, pagers[position]);
            }
        });

        photoPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(resource, pagers[photoPage()]));
        collectionPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(resource, pagers[collectionPage()]));
        userPagerModel.getListResource().observe(this, resource ->
                PagerViewManagePresenter.responsePagerListResourceChanged(resource, pagers[userPage()]));
    }

    // control.

    private int getCurrentPagerPosition() {
        if (activityModel.getPagerPosition().getValue() == null) {
            return photoPage();
        } else {
            return activityModel.getPagerPosition().getValue();
        }
    }

    private static int photoPage() {
        return 0;
    }

    private static int collectionPage() {
        return 1;
    }

    private static int userPage() {
        return 2;
    }

    private static int pageCount() {
        return 3;
    }

    private void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            editText.setFocusable(true);
            editText.requestFocus();
            manager.showSoftInput(editText, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.clearFocus();
        }
    }

    // permission.

    @Override
    protected void requestReadWritePermissionSucceed(Downloadable downloadable, int requestCode) {
        DownloadHelper.getInstance(this)
                .addMission(this, (Photo) downloadable, DownloaderService.DOWNLOAD_TYPE);
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        if (index == photoPage()) {
            photoPagerModel.refresh();
        } else if (index == collectionPage()) {
            collectionPagerModel.refresh();
        } else if (index == userPage()) {
            userPagerModel.refresh();
        }
    }

    @Override
    public void onLoad(int index) {
        if (index == photoPage()) {
            photoPagerModel.load();
        } else if (index == collectionPage()) {
            collectionPagerModel.load();
        } else if (index == userPage()) {
            userPagerModel.load();
        }
    }

    @Override
    public boolean canLoadMore(int index) {
        if (index == photoPage()) {
            return photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && photoPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED;
        } else if (index == collectionPage()) {
            return collectionPagerModel.getListResource().getValue() != null
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && collectionPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED;
        } else if (index == userPage()) {
            return userPagerModel.getListResource().getValue() != null
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.REFRESHING
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.LOADING
                    && userPagerModel.getListResource().getValue().status != ListResource.Status.ALL_LOADED;
        }
        return false;

    }

    @Override
    public boolean isLoading(int index) {
        if (index == photoPage()) {
            return photoPagerModel.getListResource().getValue() != null
                    && photoPagerModel.getListResource().getValue().status == ListResource.Status.LOADING;
        } else if (index == collectionPage()) {
            return collectionPagerModel.getListResource().getValue() != null
                    && collectionPagerModel.getListResource().getValue().status == ListResource.Status.LOADING;
        } else if (index == userPage()) {
            return userPagerModel.getListResource().getValue() != null
                    && userPagerModel.getListResource().getValue().status == ListResource.Status.LOADING;
        }
        return false;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_text:
                editText.setText("");
                showKeyboard();
                break;
        }
        return true;
    }

    // on editor action listener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        if (!TextUtils.isEmpty(text)
                && !TextUtils.equals(activityModel.getSearchQuery().getValue(), text)) {
            activityModel.setSearchQuery(text);
            hideKeyboard();
        }
        return true;
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        activityModel.setPagerPosition(position);
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
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null && manager.isActive(editText)) {
            hideKeyboard();
        }
        if (appBar.getY() > -appBar.getMeasuredHeight()) {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                DisplayUtils.setStatusBarStyle(this, true);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
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
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }

    // item event callback.

    @Override
    public void onLikeOrDislikePhoto(Photo photo, int adapterPosition, boolean setToLike) {
        likeOrDislikePhotoPresenter.likeOrDislikePhoto(
                (PhotoAdapter) pagers[photoPage()].getRecyclerViewAdapter(),
                photo,
                setToLike);
    }

    @Override
    public void onDownload(Photo photo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            DownloadHelper.getInstance(this).addMission(this, photo, DownloaderService.DOWNLOAD_TYPE);
        } else {
            requestReadWritePermission(photo);
        }
    }
}
