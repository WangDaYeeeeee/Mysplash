package com.wangdaye.mysplash.ui.widget.customWidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.data.unslpash.model.Photo;
import com.wangdaye.mysplash.data.unslpash.model.SimplifiedPhoto;
import com.wangdaye.mysplash.data.unslpash.service.PhotoService;
import com.wangdaye.mysplash.ui.activity.PhotoActivity;
import com.wangdaye.mysplash.ui.adapter.PhotosAdapter;
import com.wangdaye.mysplash.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Home page view. (for home fragment's view pager)
 * */

@SuppressLint("ViewConstructor")
public class HomePageView extends FrameLayout
        implements View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        PhotosAdapter.OnItemClickListener, PhotoService.OnRequestPhotosListener {
    // widget
    private RelativeLayout loadingView;
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private OnStartActivityCallback callback;

    // data
    private PhotoService service;

    private PhotosAdapter adapter;
    private boolean normalMode = true;
    private boolean loadingData = false;
    private boolean loadFinish = false;
    private int photoPage = 0;
    private List<Integer> pageList;
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

    public HomePageView(Context context, int type, String order, boolean normalMode) {
        super(context);
        this.initialize(type, order, normalMode);
    }

    @SuppressLint("InflateParams")
    private void initialize(int type, String order, boolean normalMode) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_view_large, null);
        addView(loadingView);

        setType(type);
        setOrder(order);
        setMode(normalMode);
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

        this.feedbackText = (TextView) findViewById(R.id.container_loading_view_large_feedbackTxt);

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
        requestPhotos(1, true);
        adapter.cancelService();
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

        this.service = PhotoService.getService()
                .buildClient();
    }

    public void setMode(boolean normalMode) {
        this.normalMode = normalMode;
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

    public void cancelRequest() {
        if (service != null) {
            service.cancel();
        }
        adapter.cancelService();
    }

    // request data.

    private void requestPhotos(int page, boolean refresh) {
        if (!loadingData) {
            loadingData = true;
            if (normalMode) {
                requestPhotosOrder(page, refresh);
            } else {
                requestPhotosRandom(page, refresh);
            }
        }
    }

    private void requestPhotosOrder(int page, boolean refresh) {
        switch (type) {
            case NEW_TYPE:
                service.requestPhotos(
                        page,
                        PhotoApi.DEFAULT_PER_PAGE,
                        photoOrder,
                        refresh,
                        this);
                break;

            case FEATURED_TYPE:
                service.requestCuratePhotos(
                        page,
                        PhotoApi.DEFAULT_PER_PAGE,
                        photoOrder,
                        refresh,
                        this);
                break;

            case COLLECTIONS_TYPE:
                break;
        }
    }

    private void requestPhotosRandom(int page, boolean refresh) {
        if (refresh) {
            switch (type) {
                case NEW_TYPE:
                    this.pageList = MathUtils.getPositionList(
                            Mysplash.TOTAL_NEW_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);
                    break;

                case FEATURED_TYPE:
                    this.pageList = MathUtils.getPositionList(
                            Mysplash.TOTAL_FEATURED_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);
                    break;
            }
        }
        switch (type) {
            case NEW_TYPE:
                service.requestPhotos(
                        pageList.get(page - 1),
                        PhotoApi.DEFAULT_PER_PAGE,
                        photoOrder,
                        refresh,
                        this);
                break;

            case FEATURED_TYPE:
                service.requestCuratePhotos(
                        pageList.get(page - 1),
                        PhotoApi.DEFAULT_PER_PAGE,
                        photoOrder,
                        refresh,
                        this);
                break;

            case COLLECTIONS_TYPE:
                break;
        }
    }

    /** <br> interface. */

    // on start activity callback.

    public interface OnStartActivityCallback {
        void startActivity(Intent intent, View view);
    }

    public void setOnStartActivityCallback(OnStartActivityCallback c) {
        this.callback = c;
    }

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
                if (callback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(
                            getContext().getString(R.string.intent_key_photo),
                            new SimplifiedPhoto(adapter.getItemList().get(position)));

                    Intent intent = new Intent(getContext(), PhotoActivity.class);
                    intent.putExtras(bundle);

                    callback.startActivity(intent, v);
                }
                break;
        }
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        requestPhotos(1, true);
    }

    @Override
    public void onLoad() {
        requestPhotos(photoPage + 1, false);
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
            if (!loadingData && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0 && !loadFinish) {
                requestPhotos(photoPage + 1, false);
            }

            if (loadingData && totalItemCount > 0 && ViewCompat.canScrollVertically(recyclerView, 1)) {
                refreshLayout.setLoading(true);
            }
        }
    };

    // on request photos listener.

    @Override
    public void onRequestPhotosSuccess(Call<List<Photo>> call, Response<List<Photo>> response,
                                       int page, boolean refresh) {
        if (normalMode) {
            onRequestPhotosOrderSuccess(response, page, refresh);
        } else {
            onRequestPhotosRandomSuccess(response, refresh);
        }
    }

    private void onRequestPhotosOrderSuccess(Response<List<Photo>> response, int page, boolean refresh) {
        this.loadingData = false;
        if (refresh) {
            loadFinish = false;
            refreshLayout.setPermitLoad(true);
            refreshLayout.setRefreshing(false);
            adapter.clearItem();
        } else {
            refreshLayout.setLoading(false);
        }
        if (response.isSuccessful() && adapter.getItemCount() + response.body().size() > 0) {
            photoPage = page;
            for (int i = 0; i < response.body().size(); i ++) {
                adapter.insertItem(response.body().get(i));
            }
            setState(NORMAL_DISPLAY_STATE);
            if (response.body().size() < PhotoApi.DEFAULT_PER_PAGE) {
                loadFinish = true;
                refreshLayout.setPermitLoad(false);
                if (response.body().size() == 0) {
                    Toast.makeText(
                            getContext(),
                            getContext().getString(R.string.feedback_is_over) + "\n" + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            feedbackText.setText(R.string.feedback_load_nothing_tv);
            setState(INIT_LOAD_FAILED_STATE);
        }
    }

    private void onRequestPhotosRandomSuccess(Response<List<Photo>> response, boolean refresh) {
        this.loadingData = false;
        if (refresh) {
            loadFinish = false;
            refreshLayout.setPermitLoad(true);
            refreshLayout.setRefreshing(false);
            adapter.clearItem();
        } else {
            refreshLayout.setLoading(false);
        }
        if (response.isSuccessful() && adapter.getItemCount() + response.body().size() > 0) {
            if (refresh) {
                photoPage = 1;
            } else {
                photoPage ++;
            }
            for (int i = 0; i < response.body().size(); i ++) {
                adapter.insertItem(response.body().get(i));
            }
            setState(NORMAL_DISPLAY_STATE);
            if (photoPage >= pageList.size()) {
                loadFinish = true;
                refreshLayout.setPermitLoad(false);
                if (response.body().size() == 0) {
                    Toast.makeText(
                            getContext(),
                            getContext().getString(R.string.feedback_is_over) + "\n" + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            feedbackText.setText(R.string.feedback_load_nothing_tv);
            setState(INIT_LOAD_FAILED_STATE);
        }
    }

    @Override
    public void onRequestPhotosFailed(Call<List<Photo>> call, Throwable t, boolean refresh) {
        this.loadingData = false;
        feedbackText.setText(R.string.feedback_load_failed_tv);
        setState(INIT_LOAD_FAILED_STATE);
        if (refresh) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setLoading(false);
        }
        if (state == NORMAL_DISPLAY_STATE) {
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.feedback_load_failed_toast) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
