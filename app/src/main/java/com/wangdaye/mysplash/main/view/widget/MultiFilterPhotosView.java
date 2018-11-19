package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.MultiFilterModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.MultiFilterPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.MultiFilterView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.MultiFilterObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.MultiFilterImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Multi-filter photos view.
 *
 * This view is used to search photos by multiple parameters for
 * {@link com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment}.
 *
 * */

public class MultiFilterPhotosView extends BothWaySwipeRefreshLayout
        implements MultiFilterView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, LargeErrorStateAdapter.OnRetryListener,
        SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.container_photo_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    private OnClickListener hideKeyboardListener;

    private MultiFilterModel multiFilterModel;
    private MultiFilterPresenter multiFilterPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    private OnMultiFilterDataInputInterface inputInterface;

    private static class SavedState extends BaseSavedState {

        String query;
        String user;
        int category;
        String orientation;
        boolean featured;
        boolean over;

        SavedState(MultiFilterPhotosView view, Parcelable superState) {
            super(superState);
            this.query = view.multiFilterModel.getQuery();
            this.user = view.multiFilterModel.getUsername();
            this.category = view.multiFilterModel.getCategory();
            this.orientation = view.multiFilterModel.getOrientation();
            this.featured = view.multiFilterModel.isFeatured();
            this.over = view.multiFilterModel.isOver();
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.user = in.readString();
            this.category = in.readInt();
            this.orientation = in.readString();
            this.featured = in.readByte() != 0;
            this.over = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.query);
            out.writeString(this.user);
            out.writeInt(this.category);
            out.writeString(this.orientation);
            out.writeByte(this.featured ? (byte) 1 : (byte) 0);
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

    public MultiFilterPhotosView(Context context) {
        super(context);
        this.initialize();
    }

    public MultiFilterPhotosView(Context context, AttributeSet attrs) {
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
        this.multiFilterModel = new MultiFilterObject(
                new PhotoAdapter(
                        getContext(),
                        new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                        this,
                        null));
        this.loadModel = new LoadObject(LoadModel.FAILED_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.multiFilterPresenter = new MultiFilterImplementor(multiFilterModel, this);
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
        recyclerView.setAdapter(multiFilterPresenter.getAdapter());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.little_margin);
            recyclerView.setPadding(margin, margin, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 160, onFeedbackViewClickListener),
                MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 160,
                        R.drawable.feedback_search,
                        getContext().getString(R.string.feedback_search_photos_tv),
                        getContext().getString(R.string.search),
                        false,
                        true,
                        onFeedbackViewClickListener,
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);

        multiFilterPresenter.getAdapter().setRecyclerView(recyclerView);
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

        multiFilterPresenter.setQuery(ss.query);
        multiFilterPresenter.setUsername(ss.user);
        multiFilterPresenter.setCategory(ss.category);
        multiFilterPresenter.setOrientation(ss.orientation);
        multiFilterPresenter.setFeatured(ss.featured);
        multiFilterPresenter.setOver(ss.over);
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(MainActivity a) {
        multiFilterPresenter.setActivityForAdapter(a);
        multiFilterPresenter.getAdapter().setOnDownloadPhotoListener(a);
        loadPresenter.bindActivity(a);
    }

    public void setClickListenerForFeedbackView(OnClickListener l) {
        hideKeyboardListener = l;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection) {
        if ((headDirection && multiFilterPresenter.getAdapter().getRealItemCount() < headIndex)
                || (!headDirection && multiFilterPresenter.getAdapter().getRealItemCount() < headIndex + list.size())) {
            return new ArrayList<>();
        }

        if (!headDirection && multiFilterPresenter.canLoadMore()) {
            multiFilterPresenter.loadMore(getContext(), false);
        }
        if (!recyclerView.canScrollVertically(1) && multiFilterPresenter.isLoading()) {
            setLoadingPhoto(true);
        }

        if (headDirection) {
            if (headIndex == 0) {
                return new ArrayList<>();
            } else {
                return multiFilterPresenter.getAdapter().getPhotoData().subList(0, headIndex - 1);
            }
        } else {
            if (multiFilterPresenter.getAdapter().getRealItemCount() == headIndex + list.size()) {
                return new ArrayList<>();
            } else {
                return multiFilterPresenter.getAdapter()
                        .getPhotoData()
                        .subList(
                                headIndex + list.size(),
                                multiFilterPresenter.getAdapter().getRealItemCount() - 1);
            }
        }
    }

    public boolean isNormalState() {
        return loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }

    // photo.

    public void updatePhoto(Photo photo, boolean refreshView) {
        multiFilterPresenter.getAdapter().updatePhoto(recyclerView, photo, refreshView, true);
    }

    /**
     * Get the photos from the adapter in this view.
     *
     * @return Photos in adapter.
     * */
    public List<Photo> getPhotos() {
        return multiFilterPresenter.getAdapter().getPhotoData();
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
        multiFilterPresenter.getAdapter().setPhotoData(list);
        if (list.size() != 0) {
            loadPresenter.setNormalState();
        }
    }

    // query.

    public String getQuery() {
        return multiFilterPresenter.getQuery();
    }

    public String getUsername() {
        return multiFilterPresenter.getUsername();
    }

    public int getCategory() {
        return multiFilterPresenter.getCategory();
    }

    public String getOrientation() {
        return multiFilterPresenter.getOrientation();
    }

    public boolean isFeatured() {
        return multiFilterPresenter.isFeatured();
    }

    // HTTP request.

    public void doSearch(int categoryId, boolean featured,
                         String username, String query,
                         String orientation) {
        multiFilterPresenter.setCategory(categoryId);
        multiFilterPresenter.setFeatured(featured);
        multiFilterPresenter.setUsername(username);
        multiFilterPresenter.setQuery(query);
        multiFilterPresenter.setOrientation(orientation);
        multiFilterPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        multiFilterPresenter.cancelRequest();
    }

    // back to top.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    // interface.

    // on multi-filter data input interface.

    public interface OnMultiFilterDataInputInterface {
        String onQueryInput();
        String onUsernameInput();
        int onCategoryInput();
        String onOrientationInput();
        boolean onFeaturedInput();
    }

    public void setOnMultiFilterDataInputInterface(OnMultiFilterDataInputInterface i) {
        inputInterface = i;
    }

    // on click listener.

    private OnClickListener onFeedbackViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (hideKeyboardListener != null) {
                hideKeyboardListener.onClick(v);
            }
        }
    };

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        multiFilterPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        multiFilterPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        if (inputInterface != null) {
            multiFilterPresenter.setQuery(inputInterface.onQueryInput());
            multiFilterPresenter.setUsername(inputInterface.onUsernameInput());
            multiFilterPresenter.setCategory(inputInterface.onCategoryInput());
            multiFilterPresenter.setOrientation(inputInterface.onOrientationInput());
            multiFilterPresenter.setFeatured(inputInterface.onFeaturedInput());
            multiFilterPresenter.initRefresh(getContext());
        }
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // on collections changed listener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        multiFilterPresenter.getAdapter().updatePhoto(recyclerView, p, true, true);
    }

    // view.

    // multi-filter view.

    @Override
    public void setRefreshingPhoto(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setLoadingPhoto(boolean loading) {
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
    public void requestPhotosSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestPhotosFailed(String feedback) {
        if (multiFilterPresenter.getAdapter().getRealItemCount() > 0) {
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
        if (activity != null && old == LoadModel.NORMAL_STATE) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        setPermitRefresh(false);
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        if (activity != null && old == LoadModel.NORMAL_STATE) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        setPermitRefresh(false);
        setPermitLoad(false);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 160,
                        R.drawable.feedback_search,
                        getContext().getString(R.string.feedback_search_failed_tv),
                        getContext().getString(R.string.feedback_click_retry),
                        true,
                        true,
                        onFeedbackViewClickListener,
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        if (activity != null && old == LoadModel.LOADING_STATE) {
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
            int totalItemCount = multiFilterPresenter.getAdapter().getRealItemCount();
            if (multiFilterPresenter.canLoadMore()
                    && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                    && totalItemCount > 0
                    && dy > 0) {
                multiFilterPresenter.loadMore(getContext(), false);
            }
            if (!recyclerView.canScrollVertically(-1)) {
                scrollPresenter.setToTop(true);
            } else {
                scrollPresenter.setToTop(false);
            }
            if (!recyclerView.canScrollVertically(1) && multiFilterPresenter.isLoading()) {
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
