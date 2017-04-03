package com.wangdaye.mysplash.me.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.item.MyFollowUser;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.MyFollowModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.MyFollowPresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.MyFollowView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.MyFollowAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.model.widget.MyFollowObject;
import com.wangdaye.mysplash.me.model.widget.ScrollObject;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.me.presenter.widget.MyFollowImplementor;
import com.wangdaye.mysplash.me.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.me.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.me.presenter.widget.SwipeBackImplementor;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * My follow user view.
 *
 * This view is used to show followers fo application user.
 *
 * */

@SuppressLint("ViewConstructor")
public class MyFollowUserView extends NestedScrollFrameLayout
        implements MyFollowView, PagerView, LoadView, ScrollView, SwipeBackView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        MyFollowAdapter.OnFollowStateChangedListener {
    // model.
    private MyFollowModel myFollowModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    @BindView(R.id.container_loading_view_large_progressView) CircularProgressView progressView;
    @BindView(R.id.container_loading_view_large_feedbackContainer) RelativeLayout feedbackContainer;
    @BindView(R.id.container_loading_view_large_feedbackTxt) TextView feedbackText;

    @BindView(R.id.container_photo_list_swipeRefreshLayout) BothWaySwipeRefreshLayout refreshLayout;
    @BindView(R.id.container_photo_list_recyclerView) RecyclerView recyclerView;

    // presenter.
    private MyFollowPresenter myFollowPresenter;
    private PagerPresenter pagerPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;
    private SwipeBackPresenter swipeBackPresenter;

    /** <br> life cycle. */

    public MyFollowUserView(MysplashActivity a, int photosType) {
        super(a);
        this.initialize(a, photosType);
    }

    @SuppressLint("InflateParams")
    private void initialize(MysplashActivity a, int followType) {
        View loadingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_loading_view_large, this, false);
        addView(loadingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel(a, followType);
        initPresenter();
        initView();
    }

    @Override
    public boolean isParentOffset() {
        return true;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.myFollowPresenter = new MyFollowImplementor(myFollowModel, this);
        this.pagerPresenter = new PagerImplementor(this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
    }

    /** <br> view. */

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);

        recyclerView.setAdapter(myFollowPresenter.getAdapter());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initLoadingView() {
        progressView.setVisibility(VISIBLE);

        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = ButterKnife.findById(
                this, R.id.container_loading_view_large_feedbackImg);
        ImageHelper.loadIcon(getContext(), feedbackImg, R.drawable.feedback_no_photos);

    }

    /** <br> model. */

    // init.

    private void initModel(MysplashActivity a, int followType) {
        this.myFollowModel = new MyFollowObject(
                new MyFollowAdapter(a, new ArrayList<MyFollowUser>(Mysplash.DEFAULT_PER_PAGE), this),
                followType);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    // interface.

    public int getDeltaValue() {
        return myFollowPresenter.getDeltaValue();
    }

    /** <br> interface. */

    // on click listener.

    @OnClick(R.id.container_loading_view_large_feedbackBtn) void retryRefresh() {
        myFollowPresenter.initRefresh(getContext());
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        myFollowPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        myFollowPresenter.loadMore(getContext(), false);
    }

    // on follow state changed listener.

    @Override
    public void onFollowStateChanged(String username, int position, boolean switchTo, boolean succeed) {
        if (succeed) {
            myFollowPresenter.setDeltaValue(switchTo ? 1 : -1);
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition <= position && position <= lastPosition) {
            MyFollowAdapter.ViewHolder holder
                    = (MyFollowAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            holder.setSwitchResult(succeed);
        }
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // view.

    // photos view.

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
    public void requestMyFollowSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestMyFollowFailed(String feedback) {
        feedbackText.setText(feedback);
        loadPresenter.setFailedState();
    }

    // pager view.

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {

    }

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
                && !myFollowPresenter.isRefreshing() && !myFollowPresenter.isLoading());
    }

    @Override
    public boolean checkNeedBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    @Override
    public void refreshPager() {
        myFollowPresenter.initRefresh(getContext());
    }

    @Override
    public void scrollToPageTop() { // interface.
        scrollPresenter.scrollToTop();
    }

    @Override
    public void cancelRequest() {
        myFollowPresenter.cancelRequest();
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
            return myFollowPresenter.getAdapter().getItemCount();
        }
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
        animHide(feedbackContainer);
    }

    @Override
    public void setFailedState() {
        animShow(feedbackContainer);
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
        if (myFollowPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            myFollowPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && myFollowPresenter.isLoading()) {
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
                        || myFollowPresenter.getAdapter().getItemCount() <= 0;

            default:
                return true;
        }
    }
}
