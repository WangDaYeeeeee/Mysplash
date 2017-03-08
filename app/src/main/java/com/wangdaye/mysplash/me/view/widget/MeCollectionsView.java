package com.wangdaye.mysplash.me.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.i.model.CollectionsModel;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash._common.i.view.CollectionsView;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.i.view.SwipeBackView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash.me.model.widget.CollectionsObject;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.model.widget.ScrollObject;
import com.wangdaye.mysplash.me.presenter.widget.CollectionsImplementor;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.me.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.me.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.me.presenter.widget.SwipeBackImplementor;

/**
 * Me collection view.
 * */

@SuppressLint("ViewConstructor")
public class MeCollectionsView extends NestedScrollFrameLayout
        implements CollectionsView, PagerView, LoadView, ScrollView, SwipeBackView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // model.
    private CollectionsModel collectionsModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    private CircularProgressView progressView;
    private Button retryButton;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // presenter.
    private CollectionsPresenter collectionsPresenter;
    private PagerPresenter pagerPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;
    private SwipeBackPresenter swipeBackPresenter;

    /** <br> life cycle. */

    public MeCollectionsView(Activity a) {
        super(a);
        this.initialize(a);
    }

    @SuppressLint("InflateParams")
    private void initialize(Activity a) {
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_mini, this, false);
        addView(loadingView);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        initModel(a);
        initPresenter();
        initView();
    }

    @Override
    public boolean isParentOffset() {
        return true;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.collectionsPresenter = new CollectionsImplementor(collectionsModel, this);
        this.pagerPresenter = new PagerImplementor(this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_view_mini_progressView);
        this.retryButton = (Button) findViewById(R.id.container_loading_view_mini_retryButton);
        retryButton.setOnClickListener(this);
        retryButton.setVisibility(GONE);

        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        if (Mysplash.getInstance().isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(collectionsPresenter.getAdapter());
        recyclerView.addOnScrollListener(scrollListener);
    }

    // interface.

    public void addCollection(Collection c) {
        if (loadPresenter.getLoadState() == LoadObject.LOADING_STATE) {
            collectionsPresenter.initRefresh(getContext());
        } else {
            collectionsPresenter.getAdapter().insertItem(c, 0);
        }
    }

    public void removeCollection(Collection c) {
        switch (loadPresenter.getLoadState()) {
            case LoadObject.LOADING_STATE:
                collectionsPresenter.initRefresh(getContext());
                break;

            case LoadObject.NORMAL_STATE:
                collectionsPresenter.getAdapter().removeItem(c);
                break;
        }
    }

    public void changeCollection(Collection c) {
        switch (loadPresenter.getLoadState()) {
            case LoadObject.LOADING_STATE:
                collectionsPresenter.initRefresh(getContext());
                break;

            case LoadObject.NORMAL_STATE:
                collectionsPresenter.getAdapter().changeItem(c);
                break;
        }
    }

    /** <br> model. */

    private void initModel(Activity a) {
        this.collectionsModel = new CollectionsObject(a);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_view_mini_retryButton:
                collectionsPresenter.initRefresh(getContext());
                break;
        }
    }// on refresh an load listener.

    @Override
    public void onRefresh() {
        collectionsPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        collectionsPresenter.loadMore(getContext(), false);
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // view.

    // collections view.

    @Override
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setLoading(boolean loading) {
        refreshLayout.setLoading(loading);
    }

    @Override
    public void setPermitRefreshing(boolean permit) {
        refreshLayout.setPermitRefresh(permit);
    }

    @Override
    public void setPermitLoading(boolean permit) {
        refreshLayout.setPermitLoad(permit);
    }

    @Override
    public void initRefreshStart() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void requestCollectionsSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestCollectionsFailed(String feedback) {
        loadPresenter.setFailedState();
    }

    // pager view.

    @Override
    public void checkToRefresh() { // interface
        if (pagerPresenter.checkNeedRefresh()) {
            pagerPresenter.refreshPager();
        }
    }

    @Override
    public boolean checkNeedRefresh() {
        return loadPresenter.getLoadState() == LoadObject.FAILED_STATE
                || (loadPresenter.getLoadState() == LoadObject.LOADING_STATE
                && !collectionsPresenter.isRefreshing() && !collectionsPresenter.isLoading());
    }

    @Override
    public boolean checkNeedBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    @Override
    public void refreshPager() {
        collectionsPresenter.initRefresh(getContext());
    }

    @Override
    public void scrollToPageTop() { // interface.
        scrollPresenter.scrollToTop();
    }

    @Override
    public void cancelRequest() {
        collectionsPresenter.cancelRequest();
    }

    @Override
    public void setKey(String key) {
        // do nothing.
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackPresenter.checkCanSwipeBack(dir);
    }

    @Override
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadObject.NORMAL_STATE) {
            return 0;
        } else {
            return collectionsPresenter.getAdapter().getItemCount();
        }
    }

    @Override
    public void writeBundle(Bundle outState) {
        // do nothing.
    }

    // load view.

    @Override
    public void animShow(View v) {
        AnimUtils.animShow(v);
    }

    @Override
    public void animHide(final View v) {
        AnimUtils.animHide(v);
    }

    @Override
    public void setLoadingState() {
        animShow(progressView);
        animHide(retryButton);
    }

    @Override
    public void setFailedState() {
        animShow(retryButton);
        animHide(progressView);
    }

    @Override
    public void setNormalState() {
        animShow(refreshLayout);
        animHide(progressView);
    }

    @Override
    public void resetLoadingState() {
        animShow(progressView);
        animHide(refreshLayout);
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        if (collectionsPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            collectionsPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && collectionsPresenter.isLoading()) {
            refreshLayout.setLoading(true);
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }

    // swipe back view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        switch (loadPresenter.getLoadState()) {
            case LoadObject.NORMAL_STATE:
                return SwipeBackCoordinatorLayout.canSwipeBackForThisView(recyclerView, dir)
                        || collectionsPresenter.getAdapter().getItemCount() <= 0;

            default:
                return true;
        }
    }
}