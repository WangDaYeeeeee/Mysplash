package com.wangdaye.mysplash.main.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
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
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.model.Photo;
import com.wangdaye.mysplash.main.model.widget.DisplayStateObject;
import com.wangdaye.mysplash.main.model.widget.PhotoStateObject;
import com.wangdaye.mysplash.main.model.widget.i.DisplayStateModel;
import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;
import com.wangdaye.mysplash.main.presenter.widget.DisplayStateImp;
import com.wangdaye.mysplash.main.presenter.widget.OptionImp;
import com.wangdaye.mysplash.main.presenter.widget.RequestDataImp;
import com.wangdaye.mysplash.main.presenter.widget.i.DisplayStatePresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.OptionPresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.RequestDataPresenter;
import com.wangdaye.mysplash.main.view.widget.i.ContentView;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;
import com.wangdaye.mysplash.main.adapter.PhotosAdapter;
import com.wangdaye.mysplash.common.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;

import java.util.ArrayList;

/**
 * Category photos view.
 * */

public class CategoryPhotosView extends FrameLayout
        implements ContentView, LoadingView, PhotosView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // widget
    private RelativeLayout loadingView;
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // mvp.
    private PhotoStateModel photoStateModel;
    private DisplayStateModel displayStateModel;

    private OptionPresenter optionPresenter;

    /** <br> life cycle. */

    public CategoryPhotosView(Context context) {
        super(context);
        this.initialize();
    }

    public CategoryPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CategoryPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CategoryPhotosView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View searchingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_in_category_view_large, null);
        addView(searchingView);

        initModel();
        initView();
        initPresenter();
    }

    public void setActivity(Activity a) {
        photoStateModel.getAdapter().setActivity(a);
    }

    public void setNormalMode(boolean b) {
        photoStateModel.setNormalMode(b);
    }

    public void setCategoryId(int id) {
        photoStateModel.setCategoryId(id);
    }

    /** <br> presenter. */

    private void initPresenter() {
        DisplayStatePresenter displayStatePresenter = new DisplayStateImp(
                photoStateModel, displayStateModel,
                this, this, this);
        RequestDataPresenter requestDataPresenter = new RequestDataImp(
                photoStateModel, null, displayStateModel,
                this, this,
                displayStatePresenter);
        this.optionPresenter = new OptionImp(
                photoStateModel,
                this, this, this,
                requestDataPresenter, displayStatePresenter,
                OptionImp.CATEGORY_LOAD_TYPE);
    }

    /** <br> view. */

    private void initView() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setVisibility(GONE);

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(photoStateModel.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initLoadingView() {
        this.loadingView = (RelativeLayout) findViewById(R.id.container_loading_in_category_view_large);
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_in_category_view_large_progressView);

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_loading_in_category_view_large_feedbackContainer);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = (ImageView) findViewById(R.id.container_loading_in_category_view_large_feedbackImg);
        Glide.with(getContext())
                .load(R.drawable.feedback_category_photo)
                .dontAnimate()
                .into(feedbackImg);

        this.feedbackText = (TextView) findViewById(R.id.container_loading_in_category_view_large_feedbackTxt);

        Button retryButton = (Button) findViewById(R.id.container_loading_in_category_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
    }

    /** <br> model. */

    // init

    private void initModel() {
        this.photoStateModel = new PhotoStateObject();
        photoStateModel.setAdapter(new PhotosAdapter(getContext(), new ArrayList<Photo>()));
        photoStateModel.setNormalMode(true);

        this.displayStateModel = new DisplayStateObject(DisplayStateObject.INIT_LOADING_STATE);
    }

    // interface.

    public void cancelRequest() {
        optionPresenter.cancelRequest();
    }

    public void initRefresh() {
        optionPresenter.initRefresh(getContext());
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_in_category_view_large_feedbackBtn:
                initRefresh();
                break;
        }
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        optionPresenter.refreshNew(getContext());
    }

    @Override
    public void onLoad() {
        optionPresenter.loadMore(getContext());
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            optionPresenter.autoLoad(recyclerView, dy);
        }
    };

    // view.

    // content view.

    @Override
    public void animShow(final View v) {
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
    public void setBackgroundOpacity() {
        // do nothing.
    }

    // loading view.

    @Override
    public View getLoadingView() {
        return loadingView;
    }

    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public View getFeedbackContainer() {
        return feedbackContainer;
    }

    @Override
    public void setFeedbackText(String text) {
        feedbackText.setText(text);
    }

    @Override
    public void showButton() {
        // do nothing.
    }

    // photos view.

    @Override
    public View getPhotosView() {
        return refreshLayout;
    }

    @Override
    public void scrollToTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setLoading(boolean loading) {
        refreshLayout.setLoading(loading);
    }

    @Override
    public void setPermitLoad(boolean permit) {
        refreshLayout.setPermitLoad(permit);
    }

    @Override
    public void resetRefreshLayout() {
        // do nothing.
    }

    @Override
    public boolean checkNeedRefresh() {
        return false;
    }

    @Override
    public boolean checkNeedChangOrder(String order) {
        return false;
    }
}
