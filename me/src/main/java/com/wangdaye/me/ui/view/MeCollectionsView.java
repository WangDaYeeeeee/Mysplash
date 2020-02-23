package com.wangdaye.me.ui.view;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.presenter.pager.PagerScrollablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.common.ui.adapter.collection.CollectionAdapter;
import com.wangdaye.common.ui.adapter.multipleState.MiniErrorStateAdapter;
import com.wangdaye.common.ui.adapter.multipleState.MiniLoadingStateAdapter;
import com.wangdaye.common.ui.decoration.GridMarginsItemDecoration;
import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarBothWaySwipeRefreshLayout;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerStateManagePresenter;
import com.wangdaye.me.R;
import com.wangdaye.me.R2;
import com.wangdaye.me.activity.MeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Me collection view.
 *
 * This view is used to show application's collections.
 *
 * */

@SuppressLint("ViewConstructor")
public class MeCollectionsView extends FitBottomSystemBarBothWaySwipeRefreshLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        MiniErrorStateAdapter.OnRetryListener {

    @BindView(R2.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;

    private PagerStateManagePresenter stateManagePresenter;

    private int index;
    private PagerManageView pagerManageView;

    public MeCollectionsView(MeActivity a, CollectionAdapter adapter, int index, PagerManageView v) {
        super(a);
        this.init(adapter, index, v);
    }

    // init.

    @SuppressLint("InflateParams")
    private void init(CollectionAdapter adapter, int index, PagerManageView v) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initData(index, v);
        initView(adapter);
    }

    private void initData(int index, PagerManageView v) {
        this.index = index;
        this.pagerManageView = v;
    }

    private void initView(CollectionAdapter adapter) {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setRefreshEnabled(false);
        setLoadEnabled(false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(getContext())
        );
        recyclerView.addItemDecoration(new GridMarginsItemDecoration(getContext(), recyclerView));

        recyclerView.setAdapter(new MiniLoadingStateAdapter(), MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(new MiniErrorStateAdapter(this), MultipleStateRecyclerView.STATE_ERROR);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        MeCollectionsView.this,
                        recyclerView,
                        adapter.getItemCount(),
                        pagerManageView,
                        index,
                        dy
                );
            }
        });

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        return stateManagePresenter.setState(state);
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public void setSwipeRefreshing(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setSwipeLoading(boolean loading) {
        setLoading(loading);
    }

    @Override
    public void setPermitSwipeRefreshing(boolean permit) {
        // do nothing.
    }

    @Override
    public void setPermitSwipeLoading(boolean permit) {
        setLoadEnabled(permit);
    }

    @Override
    public boolean checkNeedBackToTop() {
        return recyclerView.canScrollVertically(-1)
                && stateManagePresenter.getState() == State.NORMAL;
    }

    @Override
    public void scrollToPageTop() {
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return stateManagePresenter.getState() != State.NORMAL
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        pagerManageView.onRefresh(index);
    }

    @Override
    public void onLoad() {
        pagerManageView.onLoad(index);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        pagerManageView.onRefresh(index);
    }
}