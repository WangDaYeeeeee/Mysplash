package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.model.CollectionsModel;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.CollectionsPresenter;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.widget.CollectionsObject;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.CollectionsImplementor;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Home collections view.
 * 
 * This view is used to show collections for 
 * {@link com.wangdaye.mysplash.main.view.fragment.HomeFragment}.
 * 
 * */

@SuppressLint("ViewConstructor")
public class CollectionsView extends NestedScrollFrameLayout
        implements com.wangdaye.mysplash.common.i.view.CollectionsView, PagerView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {

    @BindView(R.id.container_loading_view_large_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_loading_view_large_feedbackContainer)
    RelativeLayout feedbackContainer;

    @BindView(R.id.container_loading_view_large_feedbackTxt)
    TextView feedbackText;

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

    private CollectionsModel collectionsModel;
    private CollectionsPresenter collectionsPresenter;

    private PagerPresenter pagerPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private static class SavedState implements Parcelable {

        int type;
        int page;
        boolean over;

        SavedState(CollectionsView view) {
            this.type = view.collectionsModel.getCollectionsType();
            this.page = view.collectionsModel.getCollectionsPage();
            this.over = view.collectionsModel.isOver();
        }

        private SavedState(Parcel in) {
            this.type = in.readInt();
            this.page = in.readInt();
            this.over = in.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.type);
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

    public CollectionsView(MysplashActivity a, @Mysplash.CollectionTypeRule int type, int id) {
        super(a);
        this.setId(id);
        this.initialize(a, type);
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize(MysplashActivity a, @Mysplash.CollectionTypeRule int type) {
        View loadingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_loading_view_large, this, false);
        addView(loadingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel(a, type);
        initPresenter();
        initView();
    }

    private void initModel(MysplashActivity a, @Mysplash.CollectionTypeRule int type) {
        this.collectionsModel = new CollectionsObject(
                new CollectionAdapter(
                        a,
                        new ArrayList<Collection>(Mysplash.DEFAULT_PER_PAGE)),
                type);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.collectionsPresenter = new CollectionsImplementor(collectionsModel, this);
        this.pagerPresenter = new PagerImplementor(this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
    }

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setVisibility(GONE);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
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
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initLoadingView() {
        progressView.setVisibility(VISIBLE);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = (ImageView) findViewById(R.id.container_loading_view_large_feedbackImg);
        ImageHelper.loadResourceImage(getContext(), feedbackImg, R.drawable.feedback_no_photos);
    }

    // control.

    @Override
    public boolean isParentOffset() {
        return true;
    }

    // collection.

    public void updateCollection(Collection collection, boolean refreshView) {
        collectionsPresenter.getAdapter().updateCollection(collection, refreshView, false);
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

    // on click listener.

    @OnClick(R.id.container_loading_view_large_feedbackBtn) void retryRefresh() {
        collectionsPresenter.initRefresh(getContext());
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        collectionsPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        collectionsPresenter.loadMore(getContext(), false);
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
    public void requestCollectionsSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestCollectionsFailed(String feedback) {
        if (collectionsPresenter.getAdapter().getRealItemCount() > 0) {
            loadPresenter.setNormalState();
        } else {
            feedbackText.setText(feedback);
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
            collectionsPresenter.setType(ss.type);
            collectionsPresenter.setPage(ss.page);
            collectionsPresenter.setOver(ss.over);
        } else {
            refreshPager();
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
        return collectionsPresenter.getAdapter().getRealItemCount() <= 0
                && !collectionsPresenter.isRefreshing() && !collectionsPresenter.isLoading();
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
        return false;
    }

    @Override
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadObject.NORMAL_STATE) {
            return 0;
        } else {
            return collectionsPresenter.getAdapter().getRealItemCount();
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
        animHide(refreshLayout);
    }

    @Override
    public void setFailedState() {
        animShow(feedbackContainer);
        animHide(progressView);
        animHide(refreshLayout);
    }

    @Override
    public void setNormalState() {
        animShow(refreshLayout);
        animHide(progressView);
        animHide(feedbackContainer);
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPositions(null);
        int totalItemCount = collectionsPresenter.getAdapter().getRealItemCount();
        if (collectionsPresenter.canLoadMore()
                && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                && totalItemCount > 0
                && dy > 0) {
            collectionsPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && collectionsPresenter.isLoading()) {
            refreshLayout.setLoading(true);
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }
}