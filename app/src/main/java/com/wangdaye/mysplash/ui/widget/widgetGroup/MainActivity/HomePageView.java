package com.wangdaye.mysplash.ui.widget.widgetGroup.MainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.data.unslpash.model.Photo;
import com.wangdaye.mysplash.data.unslpash.service.PhotoService;
import com.wangdaye.mysplash.ui.adapter.PhotosAdapter;
import com.wangdaye.mysplash.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Home page view. (for home fragment's view pager)
 * */

public class HomePageView extends FrameLayout
        implements View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        PhotosAdapter.OnItemClickListener, PhotoService.OnRequestPhotosListener {
    // widget
    private RelativeLayout loadingView;
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // data
    private PhotosAdapter adapter;
    private boolean loadingData = false;
    private int photoPage = 0;
    private String photoOrder = PhotoApi.ORDER_BY_LATEST;

    private int type = NEW_TYPE;
    public static final int NEW_TYPE = 1;
    public static final int FEATURED_TYPE = 2;
    public static final int COLLECTIONS_TYPE = 3;

    private int state = INIT_LOADING_STATE;
    public static final int INIT_LOADING_STATE = 0;
    public static final int INIT_LOAD_FAILED_STATE = -1;
    public static final int NORMAL_DISPLAY_STATE = 1;
    public static final int RESET_STATE = 7;

    /** <br> life cycle. */

    public HomePageView(Context context, int type, String order) {
        super(context);
        this.initialize(type, order);
    }

    public HomePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(NEW_TYPE, PhotoApi.ORDER_BY_LATEST);
    }

    public HomePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(NEW_TYPE, PhotoApi.ORDER_BY_LATEST);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomePageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize(NEW_TYPE, PhotoApi.ORDER_BY_LATEST);
    }

    @SuppressLint("InflateParams")
    private void initialize(int type, String order) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_large, null);
        addView(loadingView);

        setType(type);
        setOrder(order);
        initData();
        initWidget();
    }

    /** <br> UI. */

    // init.

    private void initWidget() {
        this.initContentView();
        this.initLoadingView();
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setVisibility(GONE);

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(adapter);
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

        Button retryButton = (Button) findViewById(R.id.container_loading_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
    }

    // change state.

    public void setState(int newState) {
        switch (newState) {
            case INIT_LOADING_STATE:
                if (state == INIT_LOAD_FAILED_STATE) {
                    animHide(feedbackContainer);
                    animShow(progressView);
                }
                break;

            case INIT_LOAD_FAILED_STATE:
                if (state == INIT_LOADING_STATE) {
                    animHide(progressView);
                    animShow(feedbackContainer);
                }
                break;

            case NORMAL_DISPLAY_STATE:
                if (state == INIT_LOADING_STATE) {
                    animHide(loadingView);
                    animShow(refreshLayout);
                }
                break;

            case RESET_STATE:
                switch (state) {
                    case INIT_LOAD_FAILED_STATE:
                        animHide(feedbackContainer);
                        animShow(progressView);
                        newState = INIT_LOADING_STATE;
                        break;

                    case NORMAL_DISPLAY_STATE:
                        animHide(refreshLayout);
                        animShow(loadingView);
                        adapter.clearItem();
                        newState = INIT_LOADING_STATE;
                        break;
                }
                break;
        }
        state = newState;
    }

    // refresh & loading.

    public void initRefresh() {
        setState(INIT_LOADING_STATE);
        refreshNew();
    }

    private void refreshNew() {
        refreshLayout.setRefreshing(true);
        requestPhotos(1);
    }

    // anim.

    private void animShow(final View v) {
        if (v.getVisibility() == GONE) {
            v.setVisibility(VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    private void animHide(final View v) {
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

    // option.

    public void scrollToTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    /** <br> data. */

    // init

    private void initData() {
        this.adapter = new PhotosAdapter(getContext(), new ArrayList<Photo>());
        adapter.setOnItemClickListener(this);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOrder(String order) {
        this.photoOrder = order;
    }

    // check

    public boolean cheekNeedRefresh() {
        return adapter.getItemCount() <= 0 && !loadingData;
    }

    public boolean cheekNeedChangOrder(String order) {
        return !photoOrder.equals(order);
    }

    // request data.

    private void requestPhotos(int page) {
        if (!loadingData) {
            loadingData = true;
            switch (type) {
                case NEW_TYPE:
                    PhotoService.getService()
                            .buildClient()
                            .requestPhotos(
                                    page,
                                    PhotoApi.DEFAULT_PER_PAGE,
                                    photoOrder,
                                    this);
                    break;

                case FEATURED_TYPE:
                    PhotoService.getService()
                            .buildClient()
                            .requestCuratePhotos(
                                    page,
                                    PhotoApi.DEFAULT_PER_PAGE,
                                    photoOrder,
                                    this);
                    break;

                case COLLECTIONS_TYPE:
                    break;
            }
        }
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

    // on item click listener.

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.item_photo_card:
                break;
        }
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        requestPhotos(1);
    }

    @Override
    public void onLoad() {
        requestPhotos(photoPage + 1);
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
            int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            if (!loadingData && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
                requestPhotos(photoPage + 1);
            }

            if (loadingData && totalItemCount > 0 && ViewCompat.canScrollVertically(recyclerView, 1)) {
                refreshLayout.setLoading(true);
            }
        }
    };

    // on request photos listener.

    @Override
    public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response, int page) {
        this.loadingData = false;
        this.photoPage = page;
        setState(NORMAL_DISPLAY_STATE);
        if (page == 1) {
            refreshLayout.setRefreshing(false);
            adapter.clearItem();
        } else {
            refreshLayout.setLoading(false);
        }
        if (response.isSuccessful()) {
            for (int i = 0; i < response.body().size(); i ++) {
                adapter.insertItem(response.body().get(i));
            }
        }
    }

    @Override
    public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, int page) {
        this.loadingData = false;
        setState(INIT_LOAD_FAILED_STATE);
        if (page == 1) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setLoading(false);
        }
        if (state == NORMAL_DISPLAY_STATE) {
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.feedback_loading_failed_toast) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
