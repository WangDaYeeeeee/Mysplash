package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.model.SearchModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.presenter.SearchPresenter;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.ui.adapter.CollectionAdapter;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.adapter.UserAdapter;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.i.view.SearchView;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.model.widget.SearchCollectionsObject;
import com.wangdaye.mysplash.main.model.widget.SearchPhotosObject;
import com.wangdaye.mysplash.main.model.widget.SearchUsersObject;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.PagerImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.main.presenter.widget.SearchCollectionsImplementor;
import com.wangdaye.mysplash.main.presenter.widget.SearchPhotosImplementor;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.main.presenter.widget.SearchUsersImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;

/**
 * Search content view.
 * */

@SuppressLint("ViewConstructor")
public class HomeSearchView extends NestedScrollFrameLayout
        implements SearchView, PagerView, LoadView, ScrollView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        SelectCollectionDialog.OnCollectionsChangedListener{
    // model.
    private SearchModel searchModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;
    private Button feedbackButton;

    // presenter.
    private SearchPresenter searchPresenter;
    private PagerPresenter pagerPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;

    // data.
    public static final int SEARCH_PHOTOS_TYPE = 0;
    public static final int SEARCH_COLLECTIONS_TYPE = 1;
    public static final int SEARCH_USERS_TYPE = 2;

    private final String KEY_HOME_SEARCH_VIEW_SEARCHING_PHOTO = "home_search_view_searching_photo";
    private final String KEY_HOME_SEARCH_VIEW_SEARCHING_COLLECTION = "home_search_view_searching_collection";
    private final String KEY_HOME_SEARCH_VIEW_SEARCHING_USER = "home_search_view_searching_user";
    private final String KEY_HOME_SEARCH_VIEW_QUERY = "home_search_view_query";

    /** <br> life cycle. */

    public HomeSearchView(MainActivity a, @Nullable Bundle bundle, int type) {
        super(a);
        this.initialize(a, bundle, type);
    }

    @SuppressLint("InflateParams")
    private void initialize(MainActivity a, @Nullable Bundle bundle, int type) {
        View searchingView = LayoutInflater.from(getContext()).inflate(R.layout.container_searching_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        initModel(a, bundle, type);
        initPresenter(type);
        initView(type);

        if (loadPresenter.getLoadState() == LoadObject.LOADING_STATE) {
            searchPresenter.initRefresh(getContext());
        }
    }

    @Override
    public boolean isParentOffset() {
        return true;
    }

    /** <br> presenter. */

    private void initPresenter(int type) {
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
        this.pagerPresenter = new PagerImplementor(this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
    }

    /** <br> view. */

    // init.

    private void initView(int type) {
        initContentView();
        initSearchingView(type);
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        if (Mysplash.getInstance().isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }
        refreshLayout.setVisibility(GONE);

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(searchPresenter.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        if (searchPresenter.getAdapter() instanceof PhotoAdapter) {
            ((PhotoAdapter) searchPresenter.getAdapter()).setRecyclerView(recyclerView);
        }
    }

    private void initSearchingView(int type) {
        this.progressView = (CircularProgressView) findViewById(R.id.container_searching_view_large_progressView);
        if (loadPresenter.getLoadState() == LoadObject.FAILED_STATE) {
            progressView.setVisibility(GONE);
        } else {
            progressView.setVisibility(VISIBLE);
        }

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_searching_view_large_feedbackContainer);
        if (loadPresenter.getLoadState() == LoadObject.FAILED_STATE) {
            feedbackContainer.setVisibility(VISIBLE);
        } else {
            feedbackContainer.setVisibility(GONE);
        }

        ImageView feedbackImage = (ImageView) findViewById(R.id.container_searching_view_large_feedbackImg);
        Glide.with(getContext())
                .load(R.drawable.feedback_search)
                .dontAnimate()
                .into(feedbackImage);

        this.feedbackText = (TextView) findViewById(R.id.container_searching_view_large_feedbackTxt);
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

        this.feedbackButton = (Button) findViewById(R.id.container_searching_view_large_feedbackBtn);
        feedbackButton.setVisibility(GONE);
        feedbackButton.setOnClickListener(this);
    }

    // interface.

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
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

    /** <br> model. */

    // init.

    private void initModel(MainActivity a, @Nullable Bundle bundle, int type) {
        String query = "";
        if (bundle != null) {
            query = bundle.getString(KEY_HOME_SEARCH_VIEW_QUERY, query);
        }
        this.scrollModel = new ScrollObject(true);

        switch (type) {
            case SEARCH_PHOTOS_TYPE:
                if (bundle != null && bundle.getBoolean(KEY_HOME_SEARCH_VIEW_SEARCHING_PHOTO, false)) {
                    this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
                } else {
                    this.loadModel = new LoadObject(LoadObject.FAILED_STATE);
                }
                this.searchModel = new SearchPhotosObject(
                        new PhotoAdapter(
                                getContext(),
                                new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                                this,
                                a),
                        query);
                break;

            case SEARCH_COLLECTIONS_TYPE:
                if (bundle != null && bundle.getBoolean(KEY_HOME_SEARCH_VIEW_SEARCHING_COLLECTION, false)) {
                    this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
                } else {
                    this.loadModel = new LoadObject(LoadObject.FAILED_STATE);
                }
                this.searchModel = new SearchCollectionsObject(
                        new CollectionAdapter(getContext(), new ArrayList<Collection>()),
                        query);
                break;

            case SEARCH_USERS_TYPE:
                if (bundle != null && bundle.getBoolean(KEY_HOME_SEARCH_VIEW_SEARCHING_USER, false)) {
                    this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
                } else {
                    this.loadModel = new LoadObject(LoadObject.FAILED_STATE);
                }
                this.searchModel = new SearchUsersObject(
                        new UserAdapter(getContext(), new ArrayList<User>()),
                        query);
                break;
        }
    }

    // interface.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_searching_view_large_feedbackBtn:
                searchPresenter.initRefresh(getContext());
                break;
        }
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
            ((PhotoAdapter) searchPresenter.getAdapter()).updatePhoto(p, true);
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
    public void requestPhotosSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestPhotosFailed(String feedback) {
        feedbackText.setText(feedback);
        loadPresenter.setFailedState();
    }

    // pager view.

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
    public boolean canSwipeBack(int dir) {
        return false;
    }

    @Override
    public int getItemCount() {
        if (loadPresenter.getLoadState() != LoadObject.NORMAL_STATE) {
            return 0;
        } else {
            return searchPresenter.getAdapterItemCount();
        }
    }

    @Override
    public void writeBundle(Bundle outState) {
        outState.putString(
                KEY_HOME_SEARCH_VIEW_QUERY,
                searchPresenter.getQuery());
        if (searchModel instanceof SearchPhotosObject) {
            outState.putBoolean(
                    KEY_HOME_SEARCH_VIEW_SEARCHING_PHOTO,
                    loadPresenter.getLoadState() != LoadObject.FAILED_STATE);
        } else if (searchModel instanceof SearchCollectionsObject) {
            outState.putBoolean(
                    KEY_HOME_SEARCH_VIEW_SEARCHING_COLLECTION,
                    loadPresenter.getLoadState() != LoadObject.FAILED_STATE);
        } else if (searchModel instanceof SearchUsersObject) {
            outState.putBoolean(
                    KEY_HOME_SEARCH_VIEW_SEARCHING_USER,
                    loadPresenter.getLoadState() != LoadObject.FAILED_STATE);
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
    }

    @Override
    public void setFailedState() {
        feedbackButton.setVisibility(VISIBLE);
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
        int totalItemCount = recyclerView.getAdapter().getItemCount();
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
}
