package com.wangdaye.mysplash.collection.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewCompat;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.collection.model.widget.LoadObject;
import com.wangdaye.mysplash.collection.model.widget.PhotosObject;
import com.wangdaye.mysplash.collection.model.widget.ScrollObject;
import com.wangdaye.mysplash.collection.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.PhotosImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.PhotosModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PhotosPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.PhotosView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.collection.presenter.widget.SwipeBackImplementor;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

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

public class CollectionPhotosView extends NestedScrollFrameLayout
        implements PhotosView, LoadView, ScrollView, SwipeBackView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.container_loading_view_mini_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_loading_view_mini_retryButton)
    Button retryButton;

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

    private PhotosModel photosModel;
    private PhotosPresenter photosPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private SwipeBackPresenter swipeBackPresenter;

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

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View loadingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_loading_view_mini, this, false);
        addView(loadingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initView();
    }

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                (int) (navigationBarHeight + new DisplayUtils(getContext()).dpToPx(16)));

        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
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
        retryButton.setVisibility(GONE);
        retryButton.setOnClickListener(this);
    }

    public void initMP(CollectionActivity a, Collection c) {
        initModel(a, c);
        initPresenter();
        recyclerView.setAdapter(photosPresenter.getAdapter());
        photosPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    private void initModel(CollectionActivity a, Collection c) {
        PhotoAdapter adapter = new PhotoAdapter(
                a, new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE), this, a);
        adapter.setInMyCollection(
                AuthManager.getInstance().getUsername() != null
                        && AuthManager.getInstance().getUsername().equals(c.user.username));

        this.photosModel = new PhotosObject(
                adapter,
                c,
                c.curated ? PhotosObject.PHOTOS_TYPE_CURATED : PhotosObject.PHOTOS_TYPE_NORMAL);
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject();
    }

    private void initPresenter() {
        this.photosPresenter = new PhotosImplementor(photosModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
    }

    // control nested scroll.

    @Override
    public boolean isParentOffset() {
        return false;
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(MysplashActivity a) {
        photosPresenter.setActivityForAdapter(a);
    }

    /**
     * Execute the initialize animation.
     * */
    public void initAnimShow() {
        AnimUtils.animInitShow(progressView, 400);
    }

    public void initRefresh() {
        photosPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        photosPresenter.cancelRequest();
    }

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    /**
     * Control the photo list in this view to scroll to the top.
     * */
    public void pagerBackToTop() {
        scrollPresenter.scrollToTop();
    }

    public boolean canSwipeBack(int dir) {
        return swipeBackPresenter.checkCanSwipeBack(dir);
    }

    public Collection getCollection() {
        return (Collection) photosPresenter.getRequestKey();
    }

    public void updatePhoto(Photo photo) {
        photosPresenter.getAdapter().updatePhoto(photo, false, false);
    }

    /**
     * Get the photos from the adapter in this view.
     *
     * @return Photos in adapter.
     * */
    public List<Photo> getPhotos() {
        return photosPresenter.getAdapter().getPhotoData();
    }

    /**
     * Set photos to the adapter in this view.
     *
     * @param list Photos that will be set to the adapter.
     * */
    public void setPhotos(List<Photo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        photosPresenter.getAdapter().setPhotoData(list);
        photosPresenter.setPage(list.size() / Mysplash.DEFAULT_PER_PAGE + 1);
        if (list.size() == 0) {
            initRefresh();
        } else {
            setNormalState();
        }
    }

    // interface.

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

    // on collections change listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        photosPresenter.getAdapter().updatePhoto(p, false, true);
        if (((Collection) photosPresenter.getRequestKey()).id == c.id) {
            for (int i = 0; i < p.current_user_collections.size(); i ++) {
                if (p.current_user_collections.get(i).id == c.id) {
                    photosPresenter.getAdapter().insertItemToFirst(p);
                    return;
                }
            }
            photosPresenter.getAdapter().removeItem(p);
        }
    }

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
        int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPositions(null);
        int totalItemCount = photosPresenter.getAdapter().getRealItemCount();
        if (photosPresenter.canLoadMore()
                && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                && totalItemCount > 0
                && dy > 0) {
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
                return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                        || photosPresenter.getAdapter().getRealItemCount() <= 0;

            default:
                return true;
        }
    }
}
