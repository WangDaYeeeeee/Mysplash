package com.wangdaye.mysplash.collection.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.collection.model.LoadObject;
import com.wangdaye.mysplash.collection.model.PhotosObject;
import com.wangdaye.mysplash.collection.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.PhotosImplementor;
import com.wangdaye.mysplash.collection.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.PhotosModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.PhotosPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.PhotosView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash._common.utils.ThemeUtils;

/**
 * Collection photos view.
 * */

public class CollectionPhotosView extends FrameLayout
        implements PhotosView, LoadView, ScrollView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // model.
    private PhotosModel photosModel;
    private LoadModel loadModel;

    // view.
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // presenter.
    private PhotosPresenter photosPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;

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
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_large, null);
        addView(loadingView);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        initModel();
        initView();
        initPresenter();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.photosPresenter = new PhotosImplementor(Mysplash.getInstance().getCollection(), photosModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(this);
    }

    /** <br> view. */

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setVisibility(GONE);
        if (ThemeUtils.getInstance(getContext()).isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(photosModel.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initLoadingView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_view_large_progressView);

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_loading_view_large_feedbackContainer);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = (ImageView) findViewById(R.id.container_loading_view_large_feedbackImg);
        Glide.with(getContext())
                .load(R.drawable.feedback_load_failed)
                .dontAnimate()
                .into(feedbackImg);

        this.feedbackText = (TextView) findViewById(R.id.container_loading_view_large_feedbackTxt);

        Button retryButton = (Button) findViewById(R.id.container_loading_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
    }

    /** <br> model. */

    // init.

    private void initModel() {
        if (Mysplash.getInstance().getCollection().curated) {
            this.photosModel = new PhotosObject(getContext(), PhotosObject.PHOTOS_TYPE_CURATED);
        } else {
            this.photosModel = new PhotosObject(getContext(), PhotosObject.PHOTOS_TYPE_NORMAL);
        }
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
    }

    // interface.

    public void setActivity(Activity a) {
        photosModel.getAdapter().setActivity(a);
    }

    public void initRefresh() {
        photosPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        photosPresenter.cancelRequest();
    }

    public Collection getCollection() {
        return ((PhotosImplementor) photosPresenter).getRequestKey();
    }

    public boolean canSwipeBack(int dir) {
        return SwipeBackLayout.canSwipeBack(recyclerView, dir)
                || photosModel.getAdapter().getRealItemCount() <= 0;
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_view_large_feedbackBtn:
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
        feedbackText.setText(feedback);
        loadPresenter.setFailedState();
    }

    // load view.

    @Override
    public void animShow(View v) {
        if (v.getVisibility() == GONE) {
            v.setVisibility(VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    @Override
    public void animHide(final View v) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 1, 0)
                .setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(GONE);
            }
        });
        anim.start();
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
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        if (photosPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            photosPresenter.loadMore(getContext(), true);
        }
    }
}
