package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.model.SelectedModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.presenter.SelectedPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.ui.adapter.SelectedAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.model.widget.SelectedObject;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.main.presenter.widget.SelectedImplementor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Selected view.
 * 
 * This view is used to show collections for 
 * {@link com.wangdaye.mysplash.main.view.fragment.SelectedFragment}.
 * 
 * */

@SuppressLint("ViewConstructor")
public class SelectedView extends BothWaySwipeRefreshLayout
        implements com.wangdaye.mysplash.common.i.view.SelectedView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, LargeErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    private SelectedModel selectedModel;
    private SelectedPresenter selectedPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    public SelectedView(Context context) {
        super(context);
        this.initialize();
    }

    public SelectedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel();
        initPresenter();
        initView();
    }

    private void initModel() {
        this.selectedModel = new SelectedObject(
                new SelectedAdapter(getContext(), new ArrayList<Collection>(Mysplash.DEFAULT_PER_PAGE)));
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.selectedPresenter = new SelectedImplementor(selectedModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
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

        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
        recyclerView.setAdapter(selectedPresenter.getAdapter());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.normal_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 56),
                MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 56,
                        R.drawable.feedback_no_photos,
                        getContext().getString(R.string.feedback_load_failed_tv),
                        getContext().getString(R.string.feedback_click_retry),
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(onScrollListener);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(MysplashActivity a) {
        selectedPresenter.setActivityForAdapter(a);
        loadPresenter.bindActivity(a);
    }

    // collection.

    public void updateCollection(Collection collection, boolean refreshView) {
        selectedPresenter.getAdapter()
                .updateCollection(recyclerView, collection, refreshView, false);
    }

    /**
     * Get the collections from the adapter in this view.
     *
     * @return Collections in adapter.
     * */
    public List<Collection> getCollections() {
        return selectedPresenter.getAdapter().getCollectionData();
    }

    /**
     * Set collections to the adapter in this view.
     *
     * @param list Collections that will be set to the adapter.
     * */
    public void setCollections(List<Collection> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        selectedPresenter.getAdapter().setCollectionData(list);
        if (list.size() == 0) {
            selectedPresenter.initRefresh(getContext());
        } else {
            loadPresenter.setNormalState();
        }
    }

    // HTTP request.

    public boolean isNormalState() {
        return loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }

    public void initRefresh() {
        selectedPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        selectedPresenter.cancelRequest();
    }

    // interface.

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        selectedPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        selectedPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        selectedPresenter.initRefresh(getContext());
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
    public void setRefreshingCollection(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setLoadingCollection(boolean loading) {
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
    public void requestCollectionsSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestCollectionsFailed(String feedback) {
        if (selectedPresenter.getAdapter().getRealItemCount() > 0) {
            loadPresenter.setNormalState();
        } else {
            loadPresenter.setFailedState();
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
    public void setLoadingState(@Nullable MysplashActivity activity, int old) {
        if (activity != null) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        setPermitRefresh(false);
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        setPermitRefresh(false);
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        if (activity != null) {
            DisplayUtils.setNavigationBarStyle(
                    activity, true, activity.hasTranslucentNavigationBar());
        }
        setPermitRefresh(true);
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
            int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPositions(null);
            int totalItemCount = selectedPresenter.getAdapter().getRealItemCount();
            if (selectedPresenter.canLoadMore()
                    && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                    && totalItemCount > 0
                    && dy > 0) {
                selectedPresenter.loadMore(getContext(), false);
            }
            if (!recyclerView.canScrollVertically(-1)) {
                scrollPresenter.setToTop(true);
            } else {
                scrollPresenter.setToTop(false);
            }
            if (!recyclerView.canScrollVertically(1) && selectedPresenter.isLoading()) {
                setLoading(true);
            }
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }
}