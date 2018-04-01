package com.wangdaye.mysplash.search.view.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;
import com.wangdaye.mysplash.common._basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.model.PagerManageModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;
import com.wangdaye.mysplash.common.i.view.PagerManageView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.SearchBarView;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.search.model.activity.DownloadObject;
import com.wangdaye.mysplash.search.model.activity.PagerManageObject;
import com.wangdaye.mysplash.search.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.search.presenter.activity.MessageManageImplementor;
import com.wangdaye.mysplash.search.presenter.activity.PagerManageImplementor;
import com.wangdaye.mysplash.search.presenter.activity.SearchBarImplementor;
import com.wangdaye.mysplash.search.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.search.view.widget.SearchPageView;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Search activity.
 * 
 * This activity is used to search something.
 * 
 * */

public class SearchActivity extends LoadableActivity<Photo>
        implements SwipeBackManageView, SearchBarView, MessageManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener, PhotoAdapter.OnDownloadPhotoListener,
        SafeHandler.HandlerContainer {

    @BindView(R.id.activity_search_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_search_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_search_background)
    View background;

    @BindView(R.id.activity_search_shadow)
    View shadow;

    @BindView(R.id.activity_search_appBar)
    NestedScrollAppBarLayout appBar;

    @BindView(R.id.activity_search_editText)
    EditText editText;

    @BindView(R.id.activity_search_viewPager)
    ViewPager viewPager;

    @BindView(R.id.activity_search_indicator)
    AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[3];

    private SafeHandler<SearchActivity> handler;

    private DownloadModel downloadModel;
    private DownloadPresenter downloadPresenter;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    private SearchBarPresenter searchBarPresenter;

    private MessageManagePresenter messageManagePresenter;

    private PagerManageModel pagerManageModel;
    private PagerManagePresenter pagerManagePresenter;

    public static final String KEY_SEARCH_ACTIVITY_QUERY = "search_activity_query";
    private static final String KEY_SEARCH_ACTIVITY_PAGE_POSITION = "search_activity_page_position";

    public static class SavedStateFragment extends BaseSavedStateFragment {

        private List<Photo> searchPhotoList;
        private List<Collection> searchCollectionList;
        private List<User> searchUserList;

        public List<User> getSearchUserList() {
            return searchUserList;
        }

        public void setSearchUserList(List<User> searchUserList) {
            this.searchUserList = searchUserList;
        }

        public List<Photo> getSearchPhotoList() {
            return searchPhotoList;
        }

        public void setSearchPhotoList(List<Photo> searchPhotoList) {
            this.searchPhotoList = searchPhotoList;
        }

        public List<Collection> getSearchCollectionList() {
            return searchCollectionList;
        }

        public void setSearchCollectionList(List<Collection> searchCollectionList) {
            this.searchCollectionList = searchCollectionList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initModel();
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
        if (data != null) {
            switch (requestCode) {
                case Mysplash.COLLECTION_ACTIVITY:
                    Collection collection = data.getParcelableExtra(
                            CollectionActivity.KEY_COLLECTION_ACTIVITY_COLLECTION);
                    if (collection != null) {
                        ((SearchPageView) pagers[1]).updateCollection(collection, false);
                    }
                    break;

                case Mysplash.USER_ACTIVITY:
                    User user = data.getParcelableExtra(
                            UserActivity.KEY_USER_ACTIVITY_USER);
                    if (user != null) {
                        ((SearchPageView) pagers[2]).updateUser(user, false);
                    }
                    break;

                case Mysplash.ME_ACTIVITY:
                    User me = AuthManager.getInstance().getUser();
                    if (me != null) {
                        ((SearchPageView) pagers[2]).updateUser(me, false);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (PagerView p : pagers) {
            if (p != null) {
                p.cancelRequest();
            }
        }
        searchBarPresenter.hideKeyboard();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Common);
        }
        if (DisplayUtils.isLandscape(this)) {
            DisplayUtils.cancelTranslucentNavigation(this);
        }
    }

    @Override
    public boolean hasTranslucentNavigationBar() {
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save large data.
        SavedStateFragment f = new SavedStateFragment();
        if (pagers[0] != null) {
            f.setSearchPhotoList(((SearchPageView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            f.setSearchCollectionList(((SearchPageView) pagers[1]).getCollections());
        }
        if (pagers[2] != null) {
            f.setSearchUserList(((SearchPageView) pagers[2]).getUsers());
        }
        f.saveData(this);

        // save normal data.
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_ACTIVITY_QUERY, getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY));
        outState.putInt(KEY_SEARCH_ACTIVITY_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
        for (PagerView pager : pagers) {
            pager.onSaveInstanceState(outState);
        }
    }

    @Override
    public void handleBackPressed() {
        if (pagerManagePresenter.needPagerBackToTop()) {
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
        pagerManagePresenter.pagerScrollToTop();
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
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection, Bundle bundle) {
        int pagerIndex = bundle.getInt(KEY_SEARCH_ACTIVITY_PAGE_POSITION, -1);
        switch (pagerIndex) {
            case 0:
                if (((SearchPageView) pagers[pagerIndex])
                        .getQuery()
                        .equals(bundle.getString(KEY_SEARCH_ACTIVITY_PAGE_POSITION, ""))) {
                    return ((SearchPageView) pagers[pagerIndex]).loadMore(list, headIndex, headDirection);
                }

        }
        return new ArrayList<>();
    }

    @Override
    public Bundle getBundleOfList() {
        Bundle bundle = new Bundle();
        int pagerIndex = pagerManagePresenter.getPagerPosition();
        bundle.putString(KEY_SEARCH_ACTIVITY_QUERY, ((SearchPageView) pagers[pagerIndex]).getQuery());
        bundle.putInt(KEY_SEARCH_ACTIVITY_PAGE_POSITION, pagerIndex);
        return bundle;
    }

    @Override
    public void updateData(Photo photo) {
        ((SearchPageView) pagers[0]).updatePhoto(photo, true);
    }

    // init.

    private void initModel() {
        this.downloadModel = new DownloadObject();
        if (getBundle() != null) {
            this.pagerManageModel = new PagerManageObject(
                    getBundle().getInt(KEY_SEARCH_ACTIVITY_PAGE_POSITION, 0));
        } else {
            this.pagerManageModel = new PagerManageObject(0);
        }
    }

    private void initPresenter() {
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
        this.searchBarPresenter = new SearchBarImplementor(this);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
    }

    private void initView() {
        this.handler = new SafeHandler<>(this);

        if (getBackground() != null) {
            background.setBackground(new BitmapDrawable(getResources(), getBackground()));
        }

        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_search_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.activity_search_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        ThemeManager.inflateMenu(
                toolbar, R.menu.activity_search_toolbar_light, R.menu.activity_search_toolbar_dark);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        DisplayUtils.setTypeface(this, editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();

        String query;
        if (getBundle() != null) {
            query = getBundle().getString(KEY_SEARCH_ACTIVITY_QUERY);
        } else {
            query = getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY);
        }
        if (!TextUtils.isEmpty(query)) {
            editText.setText(query);
            searchBarPresenter.hideKeyboard();
        }

        initPages();
    }

    private void initPages() {
        List<View> pageList = new ArrayList<>();
        pageList.add(
                new SearchPageView(
                        this,
                        SearchPageView.SEARCH_PHOTOS_TYPE,
                        R.id.activity_search_page_photo,
                        0, pagerManagePresenter.getPagerPosition() == 0)
                        .setOnClickListenerForFeedbackView(hideKeyboardListener));
        pageList.add(
                new SearchPageView(
                        this,
                        SearchPageView.SEARCH_COLLECTIONS_TYPE,
                        R.id.activity_search_page_collection,
                        1, pagerManagePresenter.getPagerPosition() == 1)
                        .setOnClickListenerForFeedbackView(hideKeyboardListener));
        pageList.add(
                new SearchPageView(
                        this,
                        SearchPageView.SEARCH_USERS_TYPE,
                        R.id.activity_search_page_user,
                        2, pagerManagePresenter.getPagerPosition() == 2)
                        .setOnClickListenerForFeedbackView(hideKeyboardListener));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] searchTabs = getResources().getStringArray(R.array.search_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);

        TabLayout tabLayout = ButterKnife.findById(this, R.id.activity_search_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        if (getBundle() != null) {
            for (PagerView pager : pagers) {
                pager.onRestoreInstanceState(getBundle());
            }
            BaseSavedStateFragment f = SavedStateFragment.getData(this);
            if (f != null && f instanceof SavedStateFragment) {
                ((SearchPageView) pagers[0]).setPhotos(((SavedStateFragment) f).getSearchPhotoList());
                ((SearchPageView) pagers[1]).setCollections(((SavedStateFragment) f).getSearchCollectionList());
                ((SearchPageView) pagers[2]).setUsers(((SavedStateFragment) f).getSearchUserList());
            } else {
                String query = getBundle().getString(KEY_SEARCH_ACTIVITY_QUERY);
                if (!TextUtils.isEmpty(query)) {
                    searchBarPresenter.submitSearchInfo(query);
                }
            }
        } else {
            String query = getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY);
            if (!TextUtils.isEmpty(query)) {
                searchBarPresenter.submitSearchInfo(query);
            }
        }
    }

    // control.

    public void touchToolbar() {
        backToTop();
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
                searchBarPresenter.touchNavigatorIcon(this);
                break;
        }
    }

    private View.OnClickListener hideKeyboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchBarPresenter.hideKeyboard();
        }
    };

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return searchBarPresenter.touchMenuItem(this, item.getItemId());
    }

    // on editor action listener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            getIntent().putExtra(KEY_SEARCH_ACTIVITY_QUERY, text);
            searchBarPresenter.submitSearchInfo(text);
        }
        searchBarPresenter.hideKeyboard();
        return true;
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < pagers.length; i ++) {
            pagers[i].setSelected(i == position);
        }
        pagerManagePresenter.setPagerPosition(position);
        pagerManagePresenter.checkToRefresh(position);
        DisplayUtils.setNavigationBarStyle(
                this,
                pagers[position].isNormalState(),
                true);
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
            searchBarPresenter.hideKeyboard();
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
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
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

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(message.what, message.obj);
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
        } else {
            return pagerManagePresenter.canPagerSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    // search bar view.

    @Override
    public void clearSearchBarText() {
        editText.setText("");
    }

    @Override
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.showSoftInput(editText, 0);
        }
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @Override
    public void submitSearchInfo(String text) {
        for (PagerView p : pagers) {
            p.setKey(text);
            p.cancelRequest();
            ((SearchPageView) p).clearAdapter();
        }
        pagerManagePresenter.getPagerView(pagerManagePresenter.getPagerPosition()).refreshPager();
    }

    // message manage view.

    @Override
    public void sendMessage(final int what, final Object o) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, o).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case 1:
                showKeyboard();
                editText.clearFocus();
                break;
        }
    }

    // page manage view.

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
}
