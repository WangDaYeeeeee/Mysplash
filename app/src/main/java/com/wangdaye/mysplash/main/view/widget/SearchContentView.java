package com.wangdaye.mysplash.main.view.widget;

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
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.data.Photo;
import com.wangdaye.mysplash.common.utils.ModeUtils;
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
import com.wangdaye.mysplash.common.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.main.view.widget.i.ContentView;
import com.wangdaye.mysplash.main.view.widget.i.LoadingView;
import com.wangdaye.mysplash.main.view.widget.i.PhotosView;

import java.util.ArrayList;

/**
 * Search content view.
 * */

public class SearchContentView extends FrameLayout
        implements ContentView, LoadingView, PhotosView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // widget
    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private RelativeLayout searchingView;
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;
    private Button retryButton;

    // mvp.
    private PhotoStateModel photoStateModel;
    private DisplayStateModel displayStateModel;

    private OptionPresenter optionPresenter;

    /** <br> life cycle. */

    public SearchContentView(Context context) {
        super(context);
        this.initialize();
    }

    public SearchContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SearchContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View searchingView = LayoutInflater.from(getContext()).inflate(R.layout.container_searching_view_large, null);
        addView(searchingView);

        initModel();
        initView();
        initPresenter();
    }

    public void setActivity(Activity a) {
        photoStateModel.getAdapter().setActivity(a);
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
                OptionImp.SEARCH_LOAD_TYPE);
    }

    /** <br> view. */

    private void initView() {
        initContentView();
        initSearchingView();
        setAlpha(0.9F);
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

    private void initSearchingView() {
        this.searchingView = (RelativeLayout) findViewById(R.id.container_searching_view_large);

        this.progressView = (CircularProgressView) findViewById(R.id.container_searching_view_large_progressView);
        progressView.setVisibility(GONE);

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_searching_view_large_feedbackContainer);

        ImageView feedbackImage = (ImageView) findViewById(R.id.container_searching_view_large_feedbackImg);
        Glide.with(getContext())
                .load(R.drawable.feedback_search_photo)
                .dontAnimate()
                .into(feedbackImage);

        this.feedbackText = (TextView) findViewById(R.id.container_searching_view_large_feedbackTxt);
        feedbackText.setText(getContext().getString(R.string.feedback_search_tv));

        this.retryButton = (Button) findViewById(R.id.container_searching_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
        retryButton.setVisibility(GONE);
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.photoStateModel = new PhotoStateObject();
        photoStateModel.setAdapter(new PhotoDesignAdapter(getContext(), new ArrayList<Photo>()));
        photoStateModel.setNormalMode(true);

        this.displayStateModel = new DisplayStateObject(DisplayStateObject.INIT_LOAD_FAILED_STATE);
    }

    // interface.

    public void cancelRequest() {
        optionPresenter.cancelRequest();
    }

    public void doSearch(String query, String orientation) {
        optionPresenter.doSearch(getContext(), query, orientation);
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_searching_view_large_feedbackBtn:
                optionPresenter.initRefresh(getContext());
                break;
        }
    }

    // on refresh and load listener.

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
        setAlpha(1);
    }

    // loading view.

    @Override
    public View getLoadingView() {
        return searchingView;
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
        retryButton.setEnabled(true);
        retryButton.setVisibility(VISIBLE);
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
        refreshLayout.setEnabled(true);
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
