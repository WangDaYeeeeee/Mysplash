package com.wangdaye.mysplash.tag.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.model.SearchModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.i.view.SearchView;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.tag.model.widget.LoadObject;
import com.wangdaye.mysplash.tag.model.widget.ScrollObject;
import com.wangdaye.mysplash.tag.model.widget.SearchObject;
import com.wangdaye.mysplash.tag.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.tag.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.tag.presenter.widget.SearchImplementor;
import com.wangdaye.mysplash.tag.presenter.widget.SwipeBackImplementor;
import com.wangdaye.mysplash.tag.view.activity.TagActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Category photos view.
 * */

public class TagPhotosView extends NestedScrollFrameLayout
        implements SearchView, LoadView, ScrollView, SwipeBackView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.container_loading_in_category_view_large_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_loading_in_category_view_large_feedbackContainer)
    RelativeLayout feedbackContainer;

    @BindView(R.id.container_loading_in_category_view_large_feedbackTxt)
    TextView feedbackText;

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

    private SearchModel searchModel;
    private SearchPresenter searchPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private SwipeBackPresenter swipeBackPresenter;

    private static class SavedState extends BaseSavedState {

        String query;
        int page;
        boolean over;

        SavedState(TagPhotosView view, Parcelable superState) {
            super(superState);
            this.query = view.searchModel.getSearchQuery();
            this.page = view.searchModel.getPhotosPage();
            this.over = view.searchModel.isOver();
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.page = in.readInt();
            this.over = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.query);
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

    public TagPhotosView(Context context) {
        super(context);
        this.initialize();
    }

    public TagPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public TagPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View searchingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_loading_in_category_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel();
        initPresenter();
        initView();
    }

    private void initModel() {
        this.searchModel = new SearchObject(
                new PhotoAdapter(
                        getContext(),
                        new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                        this,
                        null),
                "");
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.searchPresenter = new SearchImplementor(searchModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);
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

        recyclerView.setAdapter(searchPresenter.getAdapter());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        ((PhotoAdapter) searchPresenter.getAdapter()).setRecyclerView(recyclerView);
    }

    private void initLoadingView() {
        progressView.setVisibility(VISIBLE);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = ButterKnife.findById(
                this, R.id.container_loading_in_category_view_large_feedbackImg);
        Glide.with(getContext())
                .load(R.drawable.feedback_no_photos)
                .dontAnimate()
                .into(feedbackImg);
    }

    // save state.

    @Override
    public Parcelable onSaveInstanceState() {
        return new SavedState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        searchPresenter.setQuery(ss.query);
        searchPresenter.setPage(ss.page);
        searchPresenter.setOver(ss.over);
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(TagActivity a) {
        ((PhotoAdapter) searchPresenter.getAdapter()).setActivity(a);
        ((PhotoAdapter) searchPresenter.getAdapter()).setOnDownloadPhotoListener(a);
    }

    public void setTag(String key) {
        searchPresenter.setQuery(key);
    }

    @Override
    public boolean isParentOffset() {
        return false;
    }

    // photo.

    public void updatePhoto(Photo photo) {
        ((PhotoAdapter) searchPresenter.getAdapter()).updatePhoto(photo, true, false);
    }

    /**
     * Get the photos from the adapter in this view.
     *
     * @return Photos in adapter.
     * */
    public List<Photo> getPhotos() {
        return ((PhotoAdapter) searchPresenter.getAdapter()).getPhotoData();
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
        ((PhotoAdapter) searchPresenter.getAdapter()).setPhotoData(list);
        if (list.size() == 0 && !TextUtils.isEmpty(searchPresenter.getQuery())) {
            initRefresh();
        } else {
            setNormalState();
        }
    }

    // HTTP request.

    public void initRefresh() {
        searchPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        searchPresenter.cancelRequest();
    }

    // back to top.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    // swipe back.

    public boolean canSwipeBack(int dir) {
        return swipeBackPresenter.checkCanSwipeBack(dir);
    }

    // interface.

    // on click swipeListener.

    @OnClick(R.id.container_loading_in_category_view_large_feedbackBtn) void retryRefresh() {
        searchPresenter.initRefresh(getContext());
    }

    // on refresh an load swipeListener.

    @Override
    public void onRefresh() {
        searchPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        searchPresenter.loadMore(getContext(), false);
    }

    // on scroll swipeListener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // on collections changed swipeListener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        ((PhotoAdapter) searchPresenter.getAdapter()).updatePhoto(p, true, true);
    }

    // view.

    // category view.

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
        // do nothing.
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
        feedbackText.setText(feedback);
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
        animHide(feedbackContainer);
    }

    @Override
    public void setFailedState() {
        animShow(feedbackContainer);
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
        int totalItemCount = searchPresenter.getAdapterItemCount();
        if (searchPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            searchPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && searchPresenter.isLoading()) {
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
            case com.wangdaye.mysplash.user.model.widget.LoadObject.NORMAL_STATE:
                return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                        || ((PhotoAdapter) searchPresenter.getAdapter()).getRealItemCount() <= 0;

            default:
                return true;
        }
    }
}