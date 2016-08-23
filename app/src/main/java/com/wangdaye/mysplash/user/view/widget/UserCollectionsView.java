package com.wangdaye.mysplash.user.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.CollectionsModel;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.CollectionsView;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash.user.model.widget.CollectionsObject;
import com.wangdaye.mysplash.user.model.widget.LoadObject;
import com.wangdaye.mysplash.user.presenter.widget.CollectionsImplementor;
import com.wangdaye.mysplash.user.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.user.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.user.presenter.widget.ScrollImplementor;

/**
 * User collections view.
 * */

@SuppressLint("ViewConstructor")
public class UserCollectionsView extends FrameLayout
        implements CollectionsView, PagerView, LoadView, ScrollView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // model.
    private CollectionsModel collectionsModel;
    private LoadModel loadModel;

    private CircularProgressView progressView;
    private Button retryButton;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // presenter.
    private CollectionsPresenter collectionsPresenter;
    private PagerPresenter pagerPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;

    /** <br> life cycle. */

    public UserCollectionsView(Activity a) {
        super(a);
        this.initialize(a);
    }

    @SuppressLint("InflateParams")
    private void initialize(Activity a) {
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_mini, null);
        addView(loadingView);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        initModel(a);
        initView();
        initPresenter();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.collectionsPresenter = new CollectionsImplementor(Mysplash.getInstance().getUser(), collectionsModel, this);
        this.pagerPresenter = new PagerImplementor(this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(this);
    }

    /** <br> view. */

    private void initView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_view_mini_progressView);
        this.retryButton = (Button) findViewById(R.id.container_loading_view_mini_retryButton);
        retryButton.setOnClickListener(this);
        retryButton.setVisibility(GONE);

        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        if (ThemeUtils.getInstance(getContext()).isLightTheme()) {
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
        recyclerView.setAdapter(collectionsModel.getAdapter());
        recyclerView.addOnScrollListener(scrollListener);
    }

    /** <br> model. */

    private void initModel(Activity a) {
        this.collectionsModel = new CollectionsObject(a);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
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
        return collectionsPresenter.waitingRefresh();
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
        return SwipeBackLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public int getItemCount() {
        if (!loadPresenter.isNormalState()) {
            return 0;
        } else {
            return collectionsModel.getAdapter().getRealItemCount();
        }
    }

    // load view.

    @Override
    public void animShow(View v) {
        if (v.getVisibility() == GONE) {
            v.setVisibility(VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    @Override
    public void animHide(final View v) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 1, 0)
                .setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(GONE);
            }
        });
        anim.start();
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
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        if (collectionsPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            collectionsPresenter.loadMore(getContext(), true);
        }
    }
}