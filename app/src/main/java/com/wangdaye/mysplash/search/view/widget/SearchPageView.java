package com.wangdaye.mysplash.search.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.FooterAdapter;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.PagerModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.model.SearchModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.PagerView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.i.view.SearchView;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;
import com.wangdaye.mysplash.common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.adapter.UserAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.search.model.widget.LoadObject;
import com.wangdaye.mysplash.search.model.widget.PagerObject;
import com.wangdaye.mysplash.search.model.widget.ScrollObject;
import com.wangdaye.mysplash.search.model.widget.SearchCollectionsObject;
import com.wangdaye.mysplash.search.model.widget.SearchPhotosObject;
import com.wangdaye.mysplash.search.model.widget.SearchUsersObject;
import com.wangdaye.mysplash.search.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.search.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.search.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.search.presenter.widget.SearchCollectionsImplementor;
import com.wangdaye.mysplash.search.presenter.widget.SearchPhotosImplementor;
import com.wangdaye.mysplash.search.presenter.widget.SearchUsersImplementor;
import com.wangdaye.mysplash.search.presenter.widget.SwipeBackImplementor;
import com.wangdaye.mysplash.search.view.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Search page view.
 *
 * */

@SuppressLint("ViewConstructor")
public class SearchPageView extends NestedScrollFrameLayout
        implements SearchView, PagerView, LoadView, ScrollView, SwipeBackView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, 
        SelectCollectionDialog.OnCollectionsChangedListener{

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.container_searching_view_large_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_searching_view_large_feedbackContainer)
    RelativeLayout feedbackContainer;

    @BindView(R.id.container_searching_view_large_feedbackTxt)
    TextView feedbackText;

    @BindView(R.id.container_searching_view_large_feedbackBtn)
    Button feedbackButton;

    private SearchModel searchModel;
    private SearchPresenter searchPresenter;

    private PagerModel pagerModel;
    private PagerPresenter pagerPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private SwipeBackPresenter swipeBackPresenter;

    public static final int SEARCH_PHOTOS_TYPE = 0;
    public static final int SEARCH_COLLECTIONS_TYPE = 1;
    public static final int SEARCH_USERS_TYPE = 2;
    @IntDef({SEARCH_PHOTOS_TYPE, SEARCH_COLLECTIONS_TYPE, SEARCH_USERS_TYPE})
    private @interface TypeRule {}

    private static class SavedState implements Parcelable {

        String query;
        int page;
        boolean over;

        SavedState(SearchPageView view) {
            this.query = view.searchModel.getSearchQuery();
            this.page = view.searchModel.getPhotosPage();
            this.over = view.searchModel.isOver();
        }

        private SavedState(Parcel in) {
            this.query = in.readString();
            this.page = in.readInt();
            this.over = in.readByte() != 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.query);
            out.writeInt(this.page);
            out.writeByte(this.over ? (byte) 1 : (byte) 0);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SearchPageView.SavedState>() {
            public SearchPageView.SavedState createFromParcel(Parcel in) {
                return new SearchPageView.SavedState(in);
            }

            public SearchPageView.SavedState[] newArray(int size) {
                return new SearchPageView.SavedState[size];
            }
        };
    }

    public SearchPageView(SearchActivity a, @TypeRule int type, int id,
                          int index, boolean selected) {
        super(a);
        this.setId(id);
        this.initialize(a, type, index, selected);
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize(SearchActivity a, @TypeRule int type,
                            int index, boolean selected) {
        View searchingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_searching_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initModel(a, type, index, selected);
        initPresenter(a, type);
        initView(type);

        if (loadPresenter.getLoadState() == LoadModel.LOADING_STATE) {
            searchPresenter.initRefresh(getContext());
        }
    }

    private void initModel(SearchActivity a, @TypeRule int type,
                           int index, boolean selected) {
        this.scrollModel = new ScrollObject(true);
        this.pagerModel = new PagerObject(index, selected);
        this.loadModel = new LoadObject(LoadModel.FAILED_STATE);
        switch (type) {
            case SEARCH_PHOTOS_TYPE:
                this.searchModel = new SearchPhotosObject(
                        new PhotoAdapter(
                                getContext(),
                                new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                                this,
                                a));
                break;

            case SEARCH_COLLECTIONS_TYPE:
                this.searchModel = new SearchCollectionsObject(
                        new CollectionAdapter(getContext(), new ArrayList<Collection>()));
                break;

            case SEARCH_USERS_TYPE:
                this.searchModel = new SearchUsersObject(
                        new UserAdapter(getContext(), new ArrayList<User>()));
                break;
        }
    }

    private void initPresenter(MysplashActivity a, @TypeRule int type) {
        switch (type) {
            case SEARCH_PHOTOS_TYPE:
                this.searchPresenter = new SearchPhotosImplementor(searchModel, this);
                break;

            case SEARCH_COLLECTIONS_TYPE:
                this.searchPresenter = new SearchCollectionsImplementor(searchModel, this);
                break;

            case SEARCH_USERS_TYPE:
                this.searchPresenter = new SearchUsersImplementor(searchModel, this);
                break;
        }
        this.pagerPresenter = new PagerImplementor(pagerModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
        this.swipeBackPresenter = new SwipeBackImplementor(this);

        loadPresenter.bindActivity(a);
    }

    private void initView(int type) {
        initContentView(type);
        initSearchingView(type);
    }

    private void initContentView(int type) {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                navigationBarHeight + getResources().getDimensionPixelSize(R.dimen.normal_margin));

        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
        recyclerView.setAdapter(searchPresenter.getAdapter());
        if (type == SEARCH_USERS_TYPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
        } else {
            if (columnCount > 1) {
                int margin = getResources().getDimensionPixelSize(R.dimen.little_margin);
                recyclerView.setPadding(margin, margin, 0, 0);
            } else {
                recyclerView.setPadding(0, 0, 0, 0);
            }
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        }
        recyclerView.addOnScrollListener(scrollListener);

        if (searchPresenter.getAdapter() instanceof PhotoAdapter) {
            ((PhotoAdapter) searchPresenter.getAdapter()).setRecyclerView(recyclerView);
        }
    }

    private void initSearchingView(@TypeRule int type) {
        setForceScrolling(true);

        if (loadPresenter.getLoadState() == LoadModel.FAILED_STATE) {
            progressView.setVisibility(GONE);
        } else {
            progressView.setVisibility(VISIBLE);
        }

        if (loadPresenter.getLoadState() == LoadModel.FAILED_STATE) {
            feedbackContainer.setVisibility(VISIBLE);
        } else {
            feedbackContainer.setVisibility(GONE);
        }

        ImageView feedbackImg = ButterKnife.findById(
                this, R.id.container_searching_view_large_feedbackImg);
        ImageHelper.loadResourceImage(getContext(), feedbackImg, R.drawable.feedback_search);

        switch (type) {
            case SEARCH_PHOTOS_TYPE:
                feedbackText.setText(getContext().getString(R.string.feedback_search_photos_tv));
                break;

            case SEARCH_COLLECTIONS_TYPE:
                feedbackText.setText(getContext().getString(R.string.feedback_search_collections_tv));
                break;

            case SEARCH_USERS_TYPE:
                feedbackText.setText(getContext().getString(R.string.feedback_search_users_tv));
                break;
        }

        feedbackButton.setVisibility(GONE);
    }

    // control.

    @Override
    public boolean isParentOffset() {
        return true;
    }

    public void clearAdapter() {
        RecyclerView.Adapter adapter = searchPresenter.getAdapter();
        if (adapter instanceof PhotoAdapter) {
            ((PhotoAdapter) adapter).clearItem();
        } else if (adapter instanceof CollectionAdapter) {
            ((CollectionAdapter) adapter).clearItem();
        } else {
            ((UserAdapter) adapter).clearItem();
        }
    }

    public SearchPageView setOnClickListenerForFeedbackView(OnClickListener l) {
        findViewById(R.id.container_searching_view_large).setOnClickListener(l);
        return this;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection) {
        if (searchPresenter.getAdapter() instanceof PhotoAdapter) {
            PhotoAdapter a = (PhotoAdapter) searchPresenter.getAdapter();
            if ((headDirection && a.getRealItemCount() < headIndex)
                    || (!headDirection && a.getRealItemCount() < headIndex + list.size())) {
                return new ArrayList<>();
            }

            if (!headDirection && searchPresenter.canLoadMore()) {
                searchPresenter.loadMore(getContext(), false);
            }
            if (!ViewCompat.canScrollVertically(recyclerView, 1) && searchPresenter.isLoading()) {
                refreshLayout.setLoading(true);
            }

            if (headDirection) {
                if (headIndex == 0) {
                    return new ArrayList<>();
                } else {
                    return a.getPhotoData().subList(0, headIndex - 1);
                }
            } else {
                if (a.getRealItemCount() == headIndex + list.size()) {
                    return new ArrayList<>();
                } else {
                    return a.getPhotoData().subList(headIndex + list.size(), a.getRealItemCount() - 1);
                }
            }
        }
        return new ArrayList<>();
    }

    public String getQuery() {
        return searchPresenter.getQuery();
    }

    // photo.

    public void updatePhoto(Photo photo, boolean refreshView) {
        if (searchPresenter.getAdapter() instanceof PhotoAdapter) {
            ((PhotoAdapter) searchPresenter.getAdapter()).updatePhoto(recyclerView, photo, refreshView, true);
        }
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
            refreshPager();
        } else {
            loadPresenter.setNormalState();
        }
    }

    // collection.

    public void updateCollection(Collection collection, boolean refreshView) {
        if (searchPresenter.getAdapter() instanceof CollectionAdapter) {
            ((CollectionAdapter) searchPresenter.getAdapter())
                    .updateCollection(recyclerView, collection, refreshView, true);
        }
    }

    /**
     * Get the collections from the adapter in this view.
     *
     * @return Collections in adapter.
     * */
    public List<Collection> getCollections() {
        return ((CollectionAdapter) searchPresenter.getAdapter()).getCollectionData();
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
        ((CollectionAdapter) searchPresenter.getAdapter()).setCollectionData(list);
        if (list.size() == 0 && !TextUtils.isEmpty(searchPresenter.getQuery())) {
            refreshPager();
        } else {
            loadPresenter.setNormalState();
        }
    }

    // user.

    public void updateUser(User user, boolean refreshView) {
        if (searchPresenter.getAdapter() instanceof UserAdapter) {
            ((UserAdapter) searchPresenter.getAdapter()).updateUser(user, refreshView, true);
        }
    }

    /**
     * Get the users from the adapter in this view.
     *
     * @return Users in adapter.
     * */
    public List<User> getUsers() {
        return ((UserAdapter) searchPresenter.getAdapter()).getUserData();
    }

    /**
     * Set users to the adapter in this view.
     *
     * @param list Users that will be set to the adapter.
     * */
    public void setUsers(List<User> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        ((UserAdapter) searchPresenter.getAdapter()).setUserData(list);
        if (list.size() == 0 && !TextUtils.isEmpty(searchPresenter.getQuery())) {
            refreshPager();
        } else {
            loadPresenter.setNormalState();
        }
    }

    // back to top.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    // interface.

    // on click listener.

    @OnClick(R.id.container_searching_view_large_feedbackBtn) void retrySearch() {
        searchPresenter.initRefresh(getContext());
    }

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        searchPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        searchPresenter.loadMore(getContext(), false);
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // on change collections listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        if (searchPresenter.getAdapter() instanceof PhotoAdapter) {
            ((PhotoAdapter) searchPresenter.getAdapter()).updatePhoto(recyclerView, p, true, true);
        }
    }

    // view.

    // search view.

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
    public void searchSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void searchFailed(String feedback) {
        if (searchPresenter.getAdapter() instanceof FooterAdapter
                && ((FooterAdapter) searchPresenter.getAdapter()).getRealItemCount() > 0) {
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
            searchPresenter.setQuery(ss.query);
            searchPresenter.setPage(ss.page);
            searchPresenter.setOver(ss.over);
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
        return searchPresenter.getAdapterItemCount() <= 0
                && !searchPresenter.getQuery().equals("")
                && !searchPresenter.isRefreshing() && !searchPresenter.isLoading();
    }

    @Override
    public boolean checkNeedBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    @Override
    public void refreshPager() {
        searchPresenter.initRefresh(getContext());
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
        searchPresenter.cancelRequest();
    }

    @Override
    public void setKey(String key) {
        searchPresenter.setQuery(key);
    }

    @Override
    public String getKey() {
        return searchPresenter.getQuery();
    }

    @Override
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadModel.NORMAL_STATE) {
            return 0;
        } else {
            return searchPresenter.getAdapterItemCount();
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
        setForceScrolling(true);
        if (activity != null && old == LoadModel.NORMAL_STATE && pagerPresenter.isSelected()) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        animShow(progressView);
        animHide(feedbackContainer);
        animHide(refreshLayout);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        setForceScrolling(true);
        if (activity != null && old == LoadModel.NORMAL_STATE && pagerPresenter.isSelected()) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        animShow(feedbackContainer);
        animHide(progressView);
        animHide(refreshLayout);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        setForceScrolling(false);
        if (activity != null && old == LoadModel.LOADING_STATE && pagerPresenter.isSelected()) {
            DisplayUtils.setNavigationBarStyle(
                    activity, true, activity.hasTranslucentNavigationBar());
        }
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
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int lastVisibleItem;
        if (manager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItems = ((StaggeredGridLayoutManager) manager)
                    .findLastVisibleItemPositions(null);
            lastVisibleItem = lastVisibleItems[lastVisibleItems.length - 1];
        } else {
            lastVisibleItem = ((GridLayoutManager) manager).findLastVisibleItemPosition();
        }

        int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;
        if (searchPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10
                && totalItemCount > 0
                && dy > 0) {
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
                && loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }

    // swipe back view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        switch (loadPresenter.getLoadState()) {
            case LoadModel.NORMAL_STATE:
                return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                        || ((FooterAdapter) searchPresenter.getAdapter()).getRealItemCount() <= 0;

            default:
                return true;
        }
    }
}
