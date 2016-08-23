package com.wangdaye.mysplash.main.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wangdaye.mysplash.common.data.data.Photo;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.common.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.main.adapter.PhotosAdapter;
import com.wangdaye.mysplash.main.model.widget.DisplayStateObject;
import com.wangdaye.mysplash.main.model.widget.PhotoStateObject;
import com.wangdaye.mysplash.main.model.widget.TypeStateObject;
import com.wangdaye.mysplash.main.model.widget.i.DisplayStateModel;
import com.wangdaye.mysplash.main.model.widget.i.PhotoStateModel;
import com.wangdaye.mysplash.main.model.widget.i.TypeStateModel;
import com.wangdaye.mysplash.main.presenter.widget.DisplayStateImp;
import com.wangdaye.mysplash.main.presenter.widget.OptionImp;
import com.wangdaye.mysplash.main.presenter.widget.RequestDataImp;
import com.wangdaye.mysplash.main.presenter.widget.i.DisplayStatePresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.OptionPresenter;
import com.wangdaye.mysplash.main.presenter.widget.i.RequestDataPresenter;
import com.wangdaye.mysplash.main.view.widget.i.ContentView;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;

import java.util.ArrayList;

/**
 * Home page view.
 * */

@SuppressLint("ViewConstructor")
public class HomePageView extends FrameLayout
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
    private TypeStateModel typeStateModel;

    private OptionPresenter optionPresenter;

    /** <br> life cycle. */

    public HomePageView(Activity a, int type, String order, boolean normalMode) {
        super(a);
        this.initialize(a, type, order, normalMode);
    }

    @SuppressLint("InflateParams")
    private void initialize(Activity a, int type, String order, boolean normalMode) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_large, null);
        addView(loadingView);

        initModel(a, type, order, normalMode);
        initView();
        initPresenter();
    }

    /** <br> presenter. */

    private void initPresenter() {
        DisplayStatePresenter displayStatePresenter = new DisplayStateImp(
                photoStateModel, displayStateModel,
                this, this, this);
        RequestDataPresenter requestDataPresenter = new RequestDataImp(
                photoStateModel, typeStateModel, displayStateModel,
                this, this,
                displayStatePresenter);
        this.optionPresenter = new OptionImp(
                photoStateModel,
                this, this, this,
                requestDataPresenter, displayStatePresenter,
                OptionImp.NORMAL_LOAD_TYPE);
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
        if (ModeUtils.getInstance(getContext()).isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(photoStateModel.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initLoadingView() {
        this.loadingView = (RelativeLayout) findViewById(R.id.container_loading_view_large);
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

    private void initModel(Activity a, int type, String order, boolean normalMode) {
        this.photoStateModel = new PhotoStateObject();
        photoStateModel.setAdapter(new PhotosAdapter(a, new ArrayList<Photo>()));
        photoStateModel.setOrder(order);
        photoStateModel.setNormalMode(normalMode);

        this.displayStateModel = new DisplayStateObject(DisplayStateObject.INIT_LOADING_STATE);
        this.typeStateModel = new TypeStateObject(type);
    }

    // interface.

    public boolean isNeedRefresh() {
        return optionPresenter.checkNeedRefresh();
    }

    public boolean isNeedChangeOrder(String order) {
        return optionPresenter.checkNeedChangOrder(order);
    }

    public void cancelRequest() {
        optionPresenter.cancelRequest();
    }

    public void initRefresh() {
        optionPresenter.initRefresh(getContext());
    }

    public void setOrder(String order) {
        photoStateModel.setOrder(order);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_view_large_feedbackBtn:
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
        return photoStateModel.getAdapter().getItemCount() <= 0
                && !photoStateModel.isLoadingData();
    }

    @Override
    public boolean checkNeedChangOrder(String order) {
        return !photoStateModel.getOrder().equals(order);
    }
}
