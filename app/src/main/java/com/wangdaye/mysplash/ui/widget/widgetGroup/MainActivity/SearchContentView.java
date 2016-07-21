package com.wangdaye.mysplash.ui.widget.widgetGroup.MainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;
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
 * Search content view.
 * */

public class SearchContentView extends FrameLayout
        implements PhotosAdapter.OnItemClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        View.OnClickListener, PhotoService.OnRequestPhotosListener {
    // widget
    private BothWaySwipeRefreshLayout refreshLayout;

    private RelativeLayout searchingView;
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;
    private Button retryButton;

    // data
    private PhotoService service;

    private PhotosAdapter adapter;
    private boolean loadingData = false;
    private int photoPage = 0;
    private String searchQuery = "";
    private String searchOrientation = PhotoApi.LANDSCAPE_ORIENTATION;

    private int state = INIT_HINT_STATE;
    public static final int INIT_HINT_STATE = 0;
    public static final int INIT_SEARCHING_STATE = 1;
    public static final int INIT_SEARCH_FAILED_STATE = -1;
    public static final int NORMAL_DISPLAY_STATE = 7;

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

        initData();
        initWidget();
    }

    /** <br> UI. */

    // init.

    private void initWidget() {
        initContentView();
        initSearchingView();
        setBackgroundColor(Color.argb((int) (255 * 0.92), 250, 250, 250));
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setVisibility(GONE);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(adapter);
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

    // change state.

    public void setState(int newState) {
        switch (newState) {
            case INIT_SEARCHING_STATE:
                switch (state) {
                    case INIT_HINT_STATE:
                        animHide(feedbackContainer);
                        animShow(progressView);
                        break;

                    case INIT_SEARCH_FAILED_STATE:
                        animHide(feedbackContainer);
                        animShow(searchingView);
                        break;

                    case NORMAL_DISPLAY_STATE:
                        animHide(refreshLayout);
                        animShow(searchingView);
                        break;
                }
                break;

            case INIT_SEARCH_FAILED_STATE:
                if (state == INIT_SEARCH_FAILED_STATE) {
                    feedbackText.setText(getContext().getString(R.string.feedback_search_failed_tv));
                    retryButton.setVisibility(VISIBLE);
                    animHide(progressView);
                    animShow(feedbackContainer);
                }
                break;

            case NORMAL_DISPLAY_STATE:
                if (state == INIT_SEARCHING_STATE) {
                    animHide(searchingView);
                    animShow(refreshLayout);
                }
                break;
        }
        state = newState;
    }

    // search.

    public void doSearch(String query, String orientation) {
        this.searchQuery = query;
        this.searchOrientation = orientation;
        if (service != null) {
            service.cancel();
        }
        setBackgroundColor(Color.rgb(250, 250, 250));
        initRefresh();
    }

    // refresh & loading.

    public void initRefresh() {
        setState(INIT_SEARCHING_STATE);
        refreshNew();
    }

    private void refreshNew() {
        refreshLayout.setRefreshing(true);
        searchPhotos(1);
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

    /** <br> data. */

    // init.

    private void initData() {
        this.adapter = new PhotosAdapter(getContext(), new ArrayList<Photo>());
        adapter.setOnItemClickListener(this);
    }

    // search data.

    private void searchPhotos(int page) {
        if (!loadingData) {
            loadingData = true;
            this.service = PhotoService.getService()
                    .buildClient();
            service.searchPhotos(
                    searchQuery,
                    searchOrientation,
                    page,
                    PhotoApi.DEFAULT_PER_PAGE,
                    this);
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_searching_view_large_feedbackBtn:
                initRefresh();
                break;
        }
    }

    // on item click listener.

    @Override
    public void onItemClick(View v, int position) {

    }

    // on refresh and load listener.

    @Override
    public void onRefresh() {
        searchPhotos(1);
    }

    @Override
    public void onLoad() {
        searchPhotos(photoPage + 1);
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
                searchPhotos(photoPage + 1);
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
        setState(INIT_SEARCH_FAILED_STATE);
        if (page == 1) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setLoading(false);
        }
        if (state == NORMAL_DISPLAY_STATE) {
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.feedback_search_failed_toast) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
