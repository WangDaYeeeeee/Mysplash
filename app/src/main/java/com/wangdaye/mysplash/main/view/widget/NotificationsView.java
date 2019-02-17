package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.NotificationsModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.NotificationsPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.ui.adapter.NotificationAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.decotarion.ListDecoration;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.manager.UserNotificationManager;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.NotificationsObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.NotificationsImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Notifications view.
 *
 * This view is used to show user's notifications for
 * {@link com.wangdaye.mysplash.main.view.activity.NotificationActivity}.
 *
 * */

public class NotificationsView extends BothWaySwipeRefreshLayout
        implements com.wangdaye.mysplash.common.i.view.NotificationsView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, LargeErrorStateAdapter.OnRetryListener {
    @BindView(R.id.container_photo_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    private NotificationsModel notificationsModel;
    private NotificationsPresenter notificationsPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    public NotificationsView(Context context) {
        super(context);
        this.initialize();
    }

    public NotificationsView(Context context, AttributeSet attrs) {
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
        this.notificationsModel = new NotificationsObject(getContext());
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    private void initPresenter() {
        this.notificationsPresenter = new NotificationsImplementor(
                getContext(), notificationsModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
    }

    private void initView() {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setPermitRefresh(false);
        setPermitLoad(false);

        recyclerView.setAdapter(notificationsPresenter.getAdapter());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 56),
                MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 56,
                        R.drawable.feedback_no_photos,
                        getContext().getString(R.string.feedback_load_failed_tv),
                        getContext().getString(R.string.feedback_click_retry),
                        this),
                MultipleStateRecyclerView.STATE_ERROR);
        recyclerView.addItemDecoration(new ListDecoration(getContext()));
        recyclerView.addOnScrollListener(scrollListener);
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(MysplashActivity a) {
        notificationsPresenter.setActivityForAdapter(a);
        loadPresenter.bindActivity(a);
    }

    public NotificationAdapter getAdapter() {
        return notificationsPresenter.getAdapter();
    }

    public UserNotificationManager.OnUpdateNotificationListener getOnUpdateNotificationListener() {
        return (NotificationsImplementor) notificationsPresenter;
    }

    // HTTP request.

    public void initRefresh() {
        notificationsPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        notificationsPresenter.cancelRequest();
    }

    // back to top.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    // swipe back.

    public boolean canSwipeBack(int dir) {
        switch (loadPresenter.getLoadState()) {
            case com.wangdaye.mysplash.collection.model.widget.LoadObject.NORMAL_STATE:
                return SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir)
                        || notificationsPresenter.getAdapter().getItemCount() <= 0;

            default:
                return true;
        }
    }

    // interface.

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        // do nothing.
    }

    @Override
    public void onLoad() {
        notificationsPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        notificationsPresenter.initRefresh(getContext());
    }

    // on scroll listener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // view.

    // notifications view.

    @Override
    public void setRefreshingNotification(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setLoadingNotification(boolean loading) {
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
    public void requestNotificationsSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestNotificationsFailed(String feedback) {
        if (notificationsPresenter.getAdapter().getItemCount() > 0) {
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
        if (activity != null) {
            DisplayUtils.setNavigationBarStyle(
                    activity, false, activity.hasTranslucentNavigationBar());
        }
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        if (activity != null) {
            DisplayUtils.setNavigationBarStyle(
                    activity, true, activity.hasTranslucentNavigationBar());
        }
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
            int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            int totalItemCount = notificationsPresenter.getAdapter().getItemCount();
            if (notificationsPresenter.canLoadMore()
                    && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
                notificationsPresenter.loadMore(getContext(), false);
            }
            if (!recyclerView.canScrollVertically(-1)) {
                scrollPresenter.setToTop(true);
            } else {
                scrollPresenter.setToTop(false);
            }
            if (!recyclerView.canScrollVertically(1) && notificationsPresenter.isLoading()) {
                setLoading(true);
            }
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }
}
