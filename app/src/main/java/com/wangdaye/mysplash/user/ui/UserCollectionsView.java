package com.wangdaye.mysplash.user.ui;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.presenter.PagerScrollablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerStateManagePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * User collections view.
 *
 * This view is used to show collections for
 * {@link UserActivity}.
 *
 * */

@SuppressLint("ViewConstructor")
public class UserCollectionsView extends BothWaySwipeRefreshLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        MiniErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;

    private PagerStateManagePresenter stateManagePresenter;

    private boolean selected;
    private int index;
    private PagerManageView pagerManageView;

    public UserCollectionsView(UserActivity a, int id, List<Collection> collectionList,
                             boolean selected, int index, PagerManageView v) {
        super(a);
        this.setId(id);
        this.init(a, collectionList, selected, index, v);
    }

    // init.

    @SuppressLint("InflateParams")
    private void init(UserActivity a, List<Collection> collectionList,
                      boolean selected, int index, PagerManageView v) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initData(selected, index, v);
        initView(a, collectionList);
    }

    private void initData(boolean selected, int index, PagerManageView v) {
        this.selected = selected;
        this.index = index;
        this.pagerManageView = v;
    }

    private void initView(UserActivity a, List<Collection> collectionList) {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setPermitRefresh(false);
        setPermitLoad(false);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                navigationBarHeight + getResources().getDimensionPixelSize(R.dimen.normal_margin));

        collectionAdapter = new CollectionAdapter(a, collectionList, DisplayUtils.getGirdColumnCount(a));
        recyclerView.setAdapter(collectionAdapter);
        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.normal_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MiniLoadingStateAdapter(), MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(new MiniErrorStateAdapter(this), MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        UserCollectionsView.this, recyclerView,
                        collectionAdapter.getRealItemCount(), pagerManageView, index, dy);
            }
        });

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // collection.

    public void removeCollection(Collection c) {
        if (stateManagePresenter.getState() == State.NORMAL) {
            collectionAdapter.removeItem(c);
        } else {
            pagerManageView.onRefresh(index);
        }
    }

    public void updateCollection(Collection c, boolean refreshView) {
        if (stateManagePresenter.getState() == State.NORMAL) {
            collectionAdapter.updateItem(recyclerView, c, refreshView, false);
        } else {
            pagerManageView.onRefresh(index);
        }
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        return stateManagePresenter.setState(state, selected);
    }

    @Override
    public void notifyItemsRefreshed(int count) {
        collectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        collectionAdapter.notifyItemRangeInserted(collectionAdapter.getRealItemCount() - count, count);
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
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
        setPermitLoad(permit);
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
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                || collectionAdapter.getRealItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return collectionAdapter.getRealItemCount();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return collectionAdapter;
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