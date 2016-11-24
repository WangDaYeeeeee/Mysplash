package com.wangdaye.mysplash.collection.view.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash._common.i.view.SwipeBackView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash.collection.model.widget.LoadObject;
import com.wangdaye.mysplash.collection.model.widget.PhotosObject;
import com.wangdaye.mysplash.collection.model.widget.ScrollObject;
import com.wangdaye.mysplash.collection.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.PhotosImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.PhotosModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotosPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.PhotosView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.collection.presenter.widget.SwipeBackImplementor;

/**
 * Collection photos view.
 * */

public class CollectionPhotosView extends FrameLayout
        implements PhotosView, LoadView, ScrollView, SwipeBackView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // model.
    private PhotosModel photosModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    private CircularProgressView progressView;
    private Button retryButton;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // presenter.
    private PhotosPresenter photosPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;
    private SwipeBackPresenter swipeBackPresenter;

    /** <br> life cycle. */

    public CollectionPhotosView(Context context) {
        super(context);
        this.initialize();
    }

    public CollectionPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CollectionPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CollectionPhotosView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_mini, null);
        addView(loadingView);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        initView();
    }

    public void initMP(MysplashActivity a, Collection c) {
        initModel(a, c);
        initPresenter();
        recyclerView.setAdapter(photosModel.getAdapter());
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photosPresenter = new PhotosImplementor(photosModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);
        if (Mysplash.getInstance().isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initLoadingView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_view_mini_progressView);

        this.retryButton = (Button) findViewById(R.id.container_loading_view_mini_retryButton);
        retryButton.setVisibility(GONE);
        retryButton.setOnClickListener(this);
    }

    // interface.

    public void pagerBackToTop() {
        scrollPresenter.scrollToTop();
    }

    /** <br> model. */

    // init.

    private void initModel(MysplashActivity a, Collection c) {
        this.photosModel = new PhotosObject(
                a,
                c,
                c.curated ? PhotosObject.PHOTOS_TYPE_CURATED : PhotosObject.PHOTOS_TYPE_NORMAL);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    // interface.

    public void setActivity(MysplashActivity a) {
        photosPresenter.setActivityForAdapter(a);
    }

    public void initRefresh() {
        photosPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        photosPresenter.cancelRequest();
    }

    public Collection getCollection() {
        return (Collection) photosPresenter.getRequestKey();
    }

    public boolean canSwipeBack(int dir) {
        return swipeBackPresenter.checkCanSwipeBack(dir);
    }

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_view_mini_retryButton:
                photosPresenter.initRefresh(getContext());
                break;
        }
    }

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        photosPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        photosPresenter.loadMore(getContext(), false);
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
    public void requestPhotosSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestPhotosFailed(String feedback) {
        loadPresenter.setFailedState();
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
        animHide(retryButton);
    }

    @Override
    public void setFailedState() {
        animShow(retryButton);
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
        if (photosPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            photosPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && photosPresenter.isLoading()) {
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
                        || photosPresenter.getAdapter().getRealItemCount() <= 0;

            default:
                return true;
        }
    }
}
