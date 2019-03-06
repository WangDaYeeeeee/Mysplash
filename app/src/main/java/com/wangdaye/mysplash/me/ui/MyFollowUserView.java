package com.wangdaye.mysplash.me.ui;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.presenter.PagerScrollablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerStateManagePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.List;

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
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        LargeErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private MyFollowAdapter myFollowAdapter;

    private PagerStateManagePresenter stateManagePresenter;

    private boolean selected;
    private int index;
    private int userDeltaCount;
    private PagerManageView pagerManageView;

    public MyFollowUserView(MyFollowActivity a, int id, List<MyFollowAdapter.MyFollowUser> list,
                            boolean selected, int index,
                            PagerManageView v, MyFollowAdapter.ItemEventCallback callback) {
        super(a);
        this.setId(id);
        this.init(list, selected, index, v, callback);
    }

    // init.

    @SuppressLint("InflateParams")
    private void init(List<MyFollowAdapter.MyFollowUser> list, boolean selected, int index,
                      PagerManageView v, MyFollowAdapter.ItemEventCallback callback) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);
        ButterKnife.bind(this, this);
        initData(selected, index, v);
        initView(list, callback);
    }

    private void initData(boolean selected, int page, PagerManageView v) {
        this.selected = selected;
        this.index = page;
        this.userDeltaCount = 0;
        this.pagerManageView = v;
    }

    private void initView(List<MyFollowAdapter.MyFollowUser> list, MyFollowAdapter.ItemEventCallback callback) {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setPermitRefresh(false);
        setPermitLoad(false);

        myFollowAdapter = new MyFollowAdapter(list);
        myFollowAdapter.setItemEventCallback(callback);
        recyclerView.setAdapter(myFollowAdapter);
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        MyFollowUserView.this, recyclerView,
                        myFollowAdapter.getItemCount(), pagerManageView, index, dy);
            }
        });

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    public int getUserDeltaCount() {
        return userDeltaCount;
    }

    public void updateMyFollowUser(User user) {
        myFollowAdapter.updateItem(user, true, false);
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
        myFollowAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        myFollowAdapter.notifyItemRangeInserted(myFollowAdapter.getItemCount() - count, count);
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
                || myFollowAdapter.getItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return myFollowAdapter.getItemCount();
        }
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

    // item event callback.

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return myFollowAdapter;
    }
}
