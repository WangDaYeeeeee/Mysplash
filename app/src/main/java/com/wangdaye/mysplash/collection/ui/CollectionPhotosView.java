package com.wangdaye.mysplash.collection.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.utils.presenter.PagerScrollablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerStateManagePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection photos view.
 *
 * This view is used to show the photos in a collection.
 *
 * */

public class CollectionPhotosView extends BothWaySwipeRefreshLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        MiniErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private PagerStateManagePresenter stateManagePresenter;

    private PagerManageView pagerManageView;

    public CollectionPhotosView(Context context) {
        super(context);
        this.init();
    }

    public CollectionPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    // init.

    @SuppressLint("InflateParams")
    private void init() {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);
        ButterKnife.bind(this, this);
        initView();
    }

    private void initView() {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setPermitRefresh(false);
        setPermitLoad(false);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                (int) (navigationBarHeight + new DisplayUtils(getContext()).dpToPx(16)));

        photoAdapter = new PhotoAdapter(
                getContext(), new ArrayList<>(), DisplayUtils.getGirdColumnCount(getContext()));
        recyclerView.setAdapter(photoAdapter);
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
                        CollectionPhotosView.this, recyclerView,
                        photoAdapter.getRealItemCount(), pagerManageView, 0, dy);
            }
        });

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // control.

    public void setItemEventCallback(PhotoAdapter.ItemEventCallback callback) {
        photoAdapter.setItemEventCallback(callback);
    }

    public void setShowDeleteButton(Collection c) {
        photoAdapter.setShowDeleteButton(
                !TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                        && AuthManager.getInstance().getUsername().equals(c.user.username));
    }

    public void setPhotoList(List<Photo> list) {
        photoAdapter.setPhotoData(list);
    }

    public void setPagerManageView(PagerManageView view) {
        pagerManageView = view;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection) {
        if ((headDirection && photoAdapter.getRealItemCount() < headIndex)
                || (!headDirection && photoAdapter.getRealItemCount() < headIndex + list.size())) {
            return new ArrayList<>();
        }

        if (!headDirection && pagerManageView.canLoadMore(0)) {
            pagerManageView.onLoad(0);
        }
        if (!recyclerView.canScrollVertically(1) && pagerManageView.isLoading(0)) {
            setLoading(true);
        }

        if (headDirection) {
            if (headIndex == 0) {
                return new ArrayList<>();
            } else {
                return photoAdapter.getPhotoData().subList(0, headIndex - 1);
            }
        } else {
            if (photoAdapter.getRealItemCount() == headIndex + list.size()) {
                return new ArrayList<>();
            } else {
                return photoAdapter.getPhotoData()
                        .subList(headIndex + list.size(), photoAdapter.getRealItemCount() - 1);
            }
        }
    }

    public void updatePhoto(Photo photo) {
        photoAdapter.updatePhoto(photo, true, false);
    }

    // interface.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        return stateManagePresenter.setState(state, true);
    }

    @Override
    public void notifyItemsRefreshed(int count) {
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemsLoaded(int count) {
        photoAdapter.notifyItemRangeInserted(photoAdapter.getRealItemCount() - count, count);
    }

    @Override
    public void setSelected(boolean selected) {
        // do nothing.
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
                || photoAdapter.getRealItemCount() <= 0;
    }

    @Override
    public int getItemCount() {
        if (stateManagePresenter.getState() != State.NORMAL) {
            return 0;
        } else {
            return photoAdapter.getRealItemCount();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getRecyclerViewAdapter() {
        return photoAdapter;
    }

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        pagerManageView.onRefresh(0);
    }

    @Override
    public void onLoad() {
        pagerManageView.onLoad(0);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        pagerManageView.onRefresh(0);
    }
}
