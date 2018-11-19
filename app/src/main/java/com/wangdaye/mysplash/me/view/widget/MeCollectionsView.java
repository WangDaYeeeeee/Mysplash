package com.wangdaye.mysplash.me.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.model.CollectionsModel;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.PagerModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.CollectionsView;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.MiniLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.me.model.widget.CollectionsObject;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.model.widget.PagerObject;
import com.wangdaye.mysplash.me.model.widget.ScrollObject;
import com.wangdaye.mysplash.me.presenter.widget.CollectionsImplementor;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.me.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.me.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.me.presenter.widget.SwipeBackImplementor;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Me collection view.
 *
 * This view is used to show application's collections.
 *
 * */

@SuppressLint("ViewConstructor")
public class MeCollectionsView extends BothWaySwipeRefreshLayout
        implements CollectionsView, PagerView, LoadView, ScrollView, SwipeBackView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, MiniErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_photo_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    private CollectionsModel collectionsModel;
    private CollectionsPresenter collectionsPresenter;

    private PagerModel pagerModel;
    private PagerPresenter pagerPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private SwipeBackPresenter swipeBackPresenter;

    private static class SavedState implements Parcelable {

        int page;
        boolean over;

        SavedState(MeCollectionsView view) {
            this.page = view.collectionsModel.getCollectionsPage();
            this.over = view.collectionsModel.isOver();
        }

        private SavedState(Parcel in) {
            this.page = in.readInt();
            this.over = in.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.page);
            out.writeByte(this.over ? (byte) 1 : (byte) 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public MeCollectionsView(MeActivity a, int id,
                             int index, boolean selected) {
        super(a);
        this.setId(id);
        this.initialize(a, index, selected);
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize(MeActivity a,
                            int index, boolean selected) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel(a, index, selected);
        initPresenter(a);
        initView();
    }

    private void initModel(MeActivity a,
                           int index, boolean selected) {
        this.collectionsModel = new CollectionsObject(a);
        this.pagerModel = new PagerObject(index, selected);
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    private void initPresenter(MysplashActivity a) {
        this.collectionsPresenter = new CollectionsImplementor(collectionsModel, this);
        this.pagerPresenter = new PagerImplementor(pagerModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);

        loadPresenter.bindActivity(a);
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
        recyclerView.setAdapter(collectionsPresenter.getAdapter());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.little_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MiniLoadingStateAdapter(), MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(new MiniErrorStateAdapter(this), MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(scrollListener);
    }

    // collection.

    public void addCollection(Collection c) {
        if (loadPresenter.getLoadState() == LoadModel.LOADING_STATE) {
            collectionsPresenter.initRefresh(getContext());
        } else {
            collectionsPresenter.getAdapter().insertItem(c, 0);
        }
    }

    public void removeCollection(Collection c) {
        switch (loadPresenter.getLoadState()) {
            case LoadModel.LOADING_STATE:
                collectionsPresenter.initRefresh(getContext());
                break;

            case LoadModel.NORMAL_STATE:
                collectionsPresenter.getAdapter().removeItem(c);
                break;
        }
    }

    public void updateCollection(Collection c, boolean refreshView) {
        switch (loadPresenter.getLoadState()) {
            case LoadModel.LOADING_STATE:
                collectionsPresenter.initRefresh(getContext());
                break;

            case LoadModel.NORMAL_STATE:
                collectionsPresenter.getAdapter()
                        .updateCollection(recyclerView, c, refreshView, false);
                break;
        }
    }

    /**
     * Get the collections from the adapter in this view.
     *
     * @return Collections in adapter.
     * */
    public List<Collection> getCollections() {
        return collectionsPresenter.getAdapter().getCollectionData();
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
        collectionsPresenter.getAdapter().setCollectionData(list);
        if (list.size() == 0) {
            refreshPager();
        } else {
            loadPresenter.setNormalState();
        }
    }

    // interface.

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        collectionsPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        collectionsPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        collectionsPresenter.initRefresh(getContext());
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // view.

    // collections view.

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
        if (collectionsPresenter.getAdapter().getRealItemCount() > 0) {
            loadPresenter.setNormalState();
        } else {
            loadPresenter.setFailedState();
        }
    }

    // pager view.

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(String.valueOf(getId()), new SavedState(this));
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        SavedState ss = bundle.getParcelable(String.valueOf(getId()));
        if (ss != null) {
            collectionsPresenter.setPage(ss.page);
            collectionsPresenter.setOver(ss.over);
        }
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
    public void setSelected(boolean selected) {
        pagerPresenter.setSelected(selected);
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
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadModel.NORMAL_STATE) {
            return 0;
        } else {
            return collectionsPresenter.getAdapter().getRealItemCount();
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
        if (activity != null && pagerPresenter.isSelected()) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        if (activity != null && pagerPresenter.isSelected()) {
            DisplayUtils.setNavigationBarStyle(
                    activity, true, activity.hasTranslucentNavigationBar());
        }
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
            int totalItemCount = collectionsPresenter.getAdapter().getRealItemCount();
            if (collectionsPresenter.canLoadMore()
                    && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                    && totalItemCount > 0
                    && dy > 0) {
                collectionsPresenter.loadMore(getContext(), false);
            }
            if (!recyclerView.canScrollVertically(-1)) {
                scrollPresenter.setToTop(true);
            } else {
                scrollPresenter.setToTop(false);
            }
            if (!recyclerView.canScrollVertically(1) && collectionsPresenter.isLoading()) {
                setLoading(true);
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
                        || collectionsPresenter.getAdapter().getRealItemCount() <= 0;

            default:
                return true;
        }
    }
}