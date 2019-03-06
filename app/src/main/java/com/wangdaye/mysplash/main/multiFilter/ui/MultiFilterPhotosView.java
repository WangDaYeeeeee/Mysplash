package com.wangdaye.mysplash.main.multiFilter.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.presenter.PagerScrollablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerLoadablePresenter;
import com.wangdaye.mysplash.common.utils.presenter.PagerStateManagePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Multi-filter photos view.
 *
 * This view is used to search photos by multiple parameters for
 * {@link MultiFilterFragment}.
 *
 * */

public class MultiFilterPhotosView extends BothWaySwipeRefreshLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        LargeErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private OnClickListener hideKeyboardListener;
    private PagerLoadablePresenter loadMorePresenter;
    private PagerStateManagePresenter stateManagePresenter;

    private PagerManageView pagerManageView;

    public MultiFilterPhotosView(Context context) {
        super(context);
        this.init();
    }

    public MultiFilterPhotosView(Context context, AttributeSet attrs) {
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
                navigationBarHeight + getResources().getDimensionPixelSize(R.dimen.normal_margin));

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
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 160,
                        v -> {if (hideKeyboardListener != null) hideKeyboardListener.onClick(v);}),
                MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 160,
                        R.drawable.feedback_search,
                        getContext().getString(R.string.feedback_search_photos_tv),
                        getContext().getString(R.string.search),
                        false,
                        true,
                        v -> {if (hideKeyboardListener != null) hideKeyboardListener.onClick(v);},
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        MultiFilterPhotosView.this, recyclerView,
                        photoAdapter.getRealItemCount(), pagerManageView, 0, dy);
            }
        });
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);

        loadMorePresenter = new PagerLoadablePresenter(
                this, recyclerView, photoAdapter, pagerManageView) {
            @Override
            public List<Photo> subList(int fromIndex, int toIndex) {
                return photoAdapter.getPhotoData().subList(fromIndex, toIndex);
            }
        };
        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // control.

    public void setItemEventCallback(PhotoAdapter.ItemEventCallback callback) {
        photoAdapter.setItemEventCallback(callback);
    }

    public void setPhotoList(List<Photo> list) {
        photoAdapter.setPhotoData(list);
    }

    public void setPagerManageView(PagerManageView view) {
        pagerManageView = view;
    }

    public void setClickListenerForFeedbackView(OnClickListener l) {
        hideKeyboardListener = l;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection) {
        return loadMorePresenter.loadMore(list, headIndex, headDirection, 0);
    }

    public void updatePhoto(Photo photo, boolean refreshView) {
        photoAdapter.updatePhoto(photo, refreshView, true);
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        boolean stateChanged = stateManagePresenter.setState(state, true);
        if (stateChanged && state == State.ERROR) {
            recyclerView.setAdapter(
                    new LargeErrorStateAdapter(
                            getContext(), 160,
                            R.drawable.feedback_search,
                            getContext().getString(R.string.feedback_search_failed_tv),
                            getContext().getString(R.string.feedback_click_retry),
                            true,
                            true,
                            v -> {if (hideKeyboardListener != null) hideKeyboardListener.onClick(v);},
                            this),
                    MultipleStateRecyclerView.STATE_ERROR);
            return true;
        }
        return stateChanged;
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
        setPermitRefresh(permit);
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
        return false;
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
