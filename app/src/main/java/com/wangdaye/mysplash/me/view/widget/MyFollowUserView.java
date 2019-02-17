package com.wangdaye.mysplash.me.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.item.MyFollowUser;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.MyFollowModel;
import com.wangdaye.mysplash.common.i.model.PagerModel;
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
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.MyFollowAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.model.widget.MyFollowObject;
import com.wangdaye.mysplash.me.model.widget.PagerObject;
import com.wangdaye.mysplash.me.model.widget.ScrollObject;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.me.presenter.widget.MyFollowImplementor;
import com.wangdaye.mysplash.me.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.me.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.me.presenter.widget.SwipeBackImplementor;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * My follow user view.
 *
 * This view is used to show followers fo application user.
 *
 * */

@SuppressLint("ViewConstructor")
public class MyFollowUserView extends BothWaySwipeRefreshLayout
        implements MyFollowView, PagerView, LoadView, ScrollView, SwipeBackView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, LargeErrorStateAdapter.OnRetryListener,
        MyFollowAdapter.OnFollowStateChangedListener {

    @BindView(R.id.container_photo_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    private MyFollowModel myFollowModel;
    private MyFollowPresenter myFollowPresenter;

    private PagerModel pagerModel;
    private PagerPresenter pagerPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private SwipeBackPresenter swipeBackPresenter;

    public MyFollowUserView(MysplashActivity a, int followType,
                            int index, boolean selected) {
        super(a);
        this.initialize(a, followType, index, selected);
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize(MysplashActivity a, int followType,
                            int index, boolean selected) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel(a, followType, index, selected);
        initPresenter();
        initView();
    }

    // init.

    private void initModel(MysplashActivity a, int followType,
                           int index, boolean selected) {
        this.myFollowModel = new MyFollowObject(
                new MyFollowAdapter(a, new ArrayList<MyFollowUser>(Mysplash.DEFAULT_PER_PAGE), this),
                followType);
        this.pagerModel = new PagerObject(index, selected);
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    private void initPresenter() {
        this.myFollowPresenter = new MyFollowImplementor(myFollowModel, this);
        this.pagerPresenter = new PagerImplementor(pagerModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
    }

    private void initView() {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setPermitRefresh(false);
        setPermitLoad(false);

        recyclerView.setAdapter(myFollowPresenter.getAdapter());
        recyclerView.setLayoutManager(
                new GridLayoutManager(
                        getContext(),
                        DisplayUtils.getGirdColumnCount(getContext())));
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 56),
                MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 56,
                        R.drawable.feedback_search,
                        getContext().getString(R.string.feedback_load_failed_tv),
                        getContext().getString(R.string.feedback_click_retry),
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    // control.

    public int getDeltaValue() {
        return myFollowPresenter.getDeltaValue();
    }

    // interface.

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        myFollowPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        myFollowPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        myFollowPresenter.initRefresh(getContext());
    }

    // on follow state changed listener.

    @Override
    public void onFollowStateChanged(String username, int position, boolean switchTo, boolean succeed) {
        if (succeed) {
            myFollowPresenter.setDeltaValue(switchTo ? 1 : -1);
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstPosition = layoutManager.findFirstVisibleItemPosition();
            int lastPosition = layoutManager.findLastVisibleItemPosition();
            if (firstPosition <= position && position <= lastPosition) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
                if (holder instanceof MyFollowAdapter.ViewHolder) {
                    ((MyFollowAdapter.ViewHolder) holder).setSwitchResult(succeed);
                }
            }
        }
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // view.

    // photos view.

    @Override
    public void setRefreshingFollow(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setLoadingFollow(boolean loading) {
        setLoading(loading);
    }

    @Override
    public void setPermitRefreshing(boolean permit) {
        setPermitRefresh(permit);
    }

    @Override
    public void setPermitLoading(boolean permit) {
        setPermitLoad(permit);
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
        if (myFollowPresenter.getAdapter().getItemCount() > 0) {
            loadPresenter.setNormalState();
        } else {
            loadPresenter.setFailedState();
        }
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
        return loadPresenter.getLoadState() == LoadModel.FAILED_STATE
                || (loadPresenter.getLoadState() == LoadModel.LOADING_STATE
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
    public void setSelected(boolean selected) {
        pagerPresenter.setSelected(selected);
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
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadModel.NORMAL_STATE) {
            return 0;
        } else {
            return myFollowPresenter.getAdapter().getItemCount();
        }
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackPresenter.checkCanSwipeBack(dir);
    }

    @Override
    public boolean isNormalState() {
        return loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
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
    public void setLoadingState(@Nullable MysplashActivity activity, int old) {
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        setPermitLoad(true);
        recyclerView.setState(MultipleStateRecyclerView.STATE_NORMALLY);
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        if (recyclerView.getLayoutManager() != null) {
            int lastVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (recyclerView.getAdapter() != null) {
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (myFollowPresenter.canLoadMore()
                        && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
                    myFollowPresenter.loadMore(getContext(), false);
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    scrollPresenter.setToTop(true);
                } else {
                    scrollPresenter.setToTop(false);
                }
                if (!recyclerView.canScrollVertically(1) && myFollowPresenter.isLoading()) {
                    setLoading(true);
                }
            }
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }

    // swipe back view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        switch (loadPresenter.getLoadState()) {
            case LoadModel.NORMAL_STATE:
                return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                        || myFollowPresenter.getAdapter().getItemCount() <= 0;

            default:
                return true;
        }
    }
}
