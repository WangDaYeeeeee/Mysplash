package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
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
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
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
import butterknife.OnClick;

/**
 * Multi-filter photos view.
 *
 * This view is used to search photos by multiple parameters for
 * {@link com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment}.
 *
 * */

public class MultiFilterPhotosView extends NestedScrollFrameLayout
        implements MultiFilterView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        SelectCollectionDialog.OnCollectionsChangedListener {

    @BindView(R.id.container_filtering_view_large_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_filtering_view_large_feedbackContainer)
    RelativeLayout feedbackContainer;

    @BindView(R.id.container_filtering_view_large_feedbackTxt)
    TextView feedbackText;

    @BindView(R.id.container_filtering_view_large_feedbackBtn)
    Button feedbackButton;

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

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

    public MultiFilterPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View searchingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_filtering_view_large, this, false);
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
        this.multiFilterModel = new MultiFilterObject(
                new PhotoAdapter(
                        getContext(),
                        new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                        this,
                        null));
        this.loadModel = new LoadObject(LoadObject.FAILED_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.multiFilterPresenter = new MultiFilterImplementor(multiFilterModel, this);
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

        recyclerView.setAdapter(multiFilterPresenter.getAdapter());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        multiFilterPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    private void initLoadingView() {
        progressView.setVisibility(GONE);

        ImageView feedbackImg = ButterKnife.findById(
                this, R.id.container_filtering_view_large_feedbackImg);
        ImageHelper.loadIcon(getContext(), feedbackImg, R.drawable.feedback_search);

        feedbackText.setText(R.string.feedback_search_photos_tv);
        feedbackText.setVisibility(GONE);

        feedbackButton.setText(getContext().getString(R.string.search));
        feedbackButton.setVisibility(VISIBLE);
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
    }

    @Override
    public boolean isParentOffset() {
        return false;
    }

    public void setClickListenerForFeedbackView(OnClickListener l) {
        findViewById(R.id.container_filtering_view_large).setOnClickListener(l);
    }

    // photo.

    public void updatePhoto(Photo photo) {
        multiFilterPresenter.getAdapter().updatePhoto(photo, true, false);
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
            animShow(refreshLayout);
            animHide(feedbackContainer);
        }
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

    @OnClick(R.id.container_filtering_view_large_feedbackBtn) void retrySearch() {
        if (inputInterface != null) {
            multiFilterPresenter.setQuery(inputInterface.onQueryInput());
            multiFilterPresenter.setUsername(inputInterface.onUsernameInput());
            multiFilterPresenter.setCategory(inputInterface.onCategoryInput());
            multiFilterPresenter.setOrientation(inputInterface.onOrientationInput());
            multiFilterPresenter.setFeatured(inputInterface.onFeaturedInput());
            multiFilterPresenter.initRefresh(getContext());
        }
    }

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        multiFilterPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        multiFilterPresenter.loadMore(getContext(), false);
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
        multiFilterPresenter.getAdapter().updatePhoto(p, true, true);
    }

    // view.

    // multi-filter view.

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
        feedbackText.setVisibility(VISIBLE);
        feedbackButton.setText(getContext().getText(R.string.feedback_click_retry));
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
        int totalItemCount = multiFilterPresenter.getAdapter().getRealItemCount();
        if (multiFilterPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            multiFilterPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && multiFilterPresenter.isLoading()) {
            refreshLayout.setLoading(true);
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }
}
