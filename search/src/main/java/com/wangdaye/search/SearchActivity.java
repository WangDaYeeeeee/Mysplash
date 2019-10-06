package com.wangdaye.search;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
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

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ListPager;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.ui.adapter.collection.CollectionItemEventHelper;
import com.wangdaye.common.ui.adapter.user.UserItemEventHelper;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.common.ui.adapter.user.UserAdapter;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.presenter.pager.PagerLoadablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.base.activity.LoadableActivity;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.adapter.photo.PhotoAdapter;
import com.wangdaye.common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.ui.widget.NestedScrollAppBarLayout;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerViewManagePresenter;
import com.wangdaye.search.base.PhotoItemEventHelper;
import com.wangdaye.search.di.component.DaggerApplicationComponent;
import com.wangdaye.search.ui.CollectionSearchPageView;
import com.wangdaye.search.ui.PhotoSearchPageView;
import com.wangdaye.search.ui.UserSearchPageView;
import com.wangdaye.search.vm.AbstractSearchPageViewModel;
import com.wangdaye.search.vm.CollectionSearchPageViewModel;
import com.wangdaye.search.vm.PhotoSearchPageViewModel;
import com.wangdaye.search.vm.SearchActivityModel;
import com.wangdaye.search.vm.UserSearchPageViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Search activity.
 * 
 * This activity is used to search something.
 * 
 * */

@Route(path = SearchActivity.SEARCH_ACTIVITY)
public class SearchActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_search_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_search_statusBar) StatusBarView statusBar;
    @BindView(R2.id.activity_search_container) CoordinatorLayout container;
    @BindView(R2.id.activity_search_shadow) View shadow;

    @BindView(R2.id.activity_search_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_search_editText) EditText editText;

    @BindView(R2.id.activity_search_viewPager) ViewPager viewPager;
    @BindView(R2.id.activity_search_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private FooterAdapter[] adapters = new FooterAdapter[pageCount()];

    private SearchActivityModel activityModel;
    private AbstractSearchPageViewModel<?, ?>[] pagerModels = new AbstractSearchPageViewModel<?, ?>[pageCount()];
    @Inject ParamsViewModelFactory viewModelFactory;

    @Inject LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;
    private PagerLoadablePresenter loadablePresenter;

    private boolean executeTransition;

    public static final String SEARCH_ACTIVITY = "/search/SearchActivity";
    public static final String ACTION_SEARCH_ACTIVITY = "com.wangdaye.mysplash.Search";
    public static final String KEY_SEARCH_ACTIVITY_QUERY = "search_activity_query";
    public static final String KEY_EXECUTE_TRANSITION = "execute_transition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            executeTransition = getIntent().getBooleanExtra(KEY_EXECUTE_TRANSITION, false);
        } else {
            executeTransition = false;
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
        statusBar.switchToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        if (executeTransition) {
            appBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            finishAfterTransition();
        } else {
            finish();
            if (backPressed) {
                overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
            } else {
                overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
            }
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

    @Override
    public List<Photo> loadMoreData(List<Photo> list, int headIndex, boolean headDirection) {
        return loadablePresenter.loadMore(
                list, headIndex, headDirection, pagers[photoPage()],
                pagers[photoPage()].getRecyclerView(),
                adapters[getCurrentPagerPosition()],
                this, photoPage()
        );
    }

    // init.

    private void initModel() {
        String query = getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY);
        if (query == null) {
            query = "";
        }

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(SearchActivityModel.class);
        activityModel.init(photoPage(), query);

        pagerModels[photoPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(PhotoSearchPageViewModel.class);
        pagerModels[photoPage()].init(ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);

        pagerModels[collectionPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(CollectionSearchPageViewModel.class);
        pagerModels[collectionPage()].init(ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);

        pagerModels[userPage()] = ViewModelProviders.of(this, viewModelFactory)
                .get(UserSearchPageViewModel.class);
        pagerModels[userPage()].init(ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);
    }

    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = findViewById(R.id.activity_search_toolbar);
        if (MysplashApplication.getInstance().getActivityCount() == 1) {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        toolbar.inflateMenu(R.menu.activity_search_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (MysplashApplication.getInstance().getActivityCount() == 1) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });
        toolbar.setOnMenuItemClickListener(this);

        editText.setOnEditorActionListener(this);

        initPages();
        loadablePresenter = new PagerLoadablePresenter() {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return Objects.requireNonNull(
                        ((PhotoSearchPageViewModel) pagerModels[photoPage()]).getListResource().getValue()
                ).dataList.subList(fromIndex, toIndex);
            }
        };

        activityModel.getSearchQuery().observe(this, s -> {
            if (!TextUtils.equals(s, editText.getText().toString())) {
                editText.setText(s);
            }
            for (int i = photoPage(); i < pageCount(); i ++) {
                if (!TextUtils.equals(s, pagerModels[i].getQuery())) {
                    pagerModels[i].setQuery(s);
                    PagerViewManagePresenter.initRefresh(pagerModels[i], adapters[i]);
                }
            }
        });

        if (executeTransition) {
            appBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            Observable.timer(350, TimeUnit.MILLISECONDS)
                    .compose(RxLifecycleCompact.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        AnimUtils.animShow(appBar);
                        AnimUtils.animShow(viewPager);
                    }).subscribe();
        }
    }

    private void initPages() {
        adapters[photoPage()] = new PhotoAdapter(
                Objects.requireNonNull(
                        ((PhotoSearchPageViewModel) pagerModels[photoPage()]).getListResource().getValue()
                ).dataList
        ).setItemEventCallback(
                new PhotoItemEventHelper(
                        this,
                        Objects.requireNonNull(
                                ((PhotoSearchPageViewModel) pagerModels[photoPage()])
                                        .getListResource()
                                        .getValue()
                        ).dataList,
                        likeOrDislikePhotoPresenter
                )
        );

        adapters[collectionPage()] = new CollectionAdapter(
                Objects.requireNonNull(
                        ((CollectionSearchPageViewModel) pagerModels[collectionPage()])
                                .getListResource()
                                .getValue()
                ).dataList
        ).setItemEventCallback(new CollectionItemEventHelper(this));

        adapters[userPage()] = new UserAdapter(
                Objects.requireNonNull(
                        ((UserSearchPageViewModel) pagerModels[userPage()]).getListResource().getValue()
                ).dataList
        ).setItemEventCallback(new UserItemEventHelper(this));

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new PhotoSearchPageView(
                                this,
                                adapters[photoPage()],
                                getCurrentPagerPosition() == photoPage(),
                                photoPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard()),
                        new CollectionSearchPageView(
                                this,
                                adapters[collectionPage()],
                                getCurrentPagerPosition() == collectionPage(),
                                collectionPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard()),
                        new UserSearchPageView(
                                this,
                                adapters[userPage()],
                                getCurrentPagerPosition() == userPage(),
                                userPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard())
                )
        );
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] searchTabs = getResources().getStringArray(R.array.search_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        PagerAdapter adapter = new PagerAdapter(pageList, tabList);

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
                    hasTranslucentNavigationBar()
            );
            ListResource resource = pagerModels[getCurrentPagerPosition()].getListResource().getValue();
            if (resource != null
                    && resource.dataList.size() == 0
                    && resource.state != ListResource.State.REFRESHING
                    && resource.state != ListResource.State.LOADING
                    && resource.state != ListResource.State.ALL_LOADED
                    && !TextUtils.isEmpty(pagerModels[getCurrentPagerPosition()].getQuery())) {
                PagerViewManagePresenter.initRefresh(
                        pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = photoPage(); i < pageCount(); i ++) {
            int finalI = i;
            pagerModels[i].getListResource().observe(this, resource ->
                    PagerViewManagePresenter.responsePagerListResourceChanged(
                            resource, pagers[finalI], adapters[finalI]
                    )
            );
        }
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
        return pagerModels[index].getListResource().getValue() != null

                && Objects.requireNonNull(
                        pagerModels[index].getListResource().getValue()
                ).state != ListResource.State.REFRESHING

                && Objects.requireNonNull(
                        pagerModels[index].getListResource().getValue()
                ).state != ListResource.State.LOADING

                && Objects.requireNonNull(
                        pagerModels[index].getListResource().getValue()
                ).state != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return pagerModels[index].getListResource().getValue() != null
                && Objects.requireNonNull(
                        pagerModels[index].getListResource().getValue()
                ).state == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_text) {
            editText.setText("");
            showKeyboard();
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
                statusBar.switchToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            if (statusBar.isInitState()) {
                statusBar.switchToDarkerAlpha();
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
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
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
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
