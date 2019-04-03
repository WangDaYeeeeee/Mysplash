package com.wangdaye.mysplash.main.following.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerScrollablePresenter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.presenter.pager.PagerStateManagePresenter;
import com.wangdaye.mysplash.main.following.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash.user.ui.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Following feed view.
 *
 * This view is used to show following feeds for
 * {@link FollowingFeedFragment}.
 *
 * */

public class FollowingFeedView extends FrameLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        LargeErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_following_list_swipeRefreshLayout) BothWaySwipeRefreshLayout refreshLayout;
    @BindView(R.id.container_following_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private FollowingAdapter followingAdapter;

    @BindView(R.id.container_following_avatar_avatarContainer) RelativeLayout avatarContainer;
    @BindView(R.id.container_following_avatar_background) FrameLayout avatarBackground;
    @BindView(R.id.container_following_avatar_avatar) CircleImageView avatar;
    @OnClick(R.id.container_following_avatar_avatar) void clickAvatar() {
        if (recyclerView.getLayoutManager() != null) {
            int adapterPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPositions(null)[0];
            User user = followingAdapter.getUser(adapterPosition);
            if (user != null) {
                IntentHelper.startUserActivity(
                        Mysplash.getInstance().getTopActivity(),
                        avatar,
                        avatarBackground,
                        user,
                        UserActivity.PAGE_PHOTO
                );
            }
        }
    }

    private AvatarScrollListener avatarScrollListener;
    private PagerStateManagePresenter stateManagePresenter;

    private PagerManageView pagerManageView;

    // the offset of this view that was caused by nested scrolling. The avatar should ensure the
    // location by this value.
    private float offsetY = 0;
    private float avatarSize;
    private float statusBarHeight;

    public FollowingFeedView(Context context) {
        super(context);
        this.init();
    }

    public FollowingFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FollowingFeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    // init.

    @SuppressLint("InflateParams")
    private void init() {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_list_2, null);
        addView(contentView);

        View avatarView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_avatar, null);
        addView(avatarView);

        ButterKnife.bind(this, this);
        initData();
        initView();
    }

    private void initData() {
        this.avatarSize = new DisplayUtils(getContext()).dpToPx(56);
        this.statusBarHeight = DisplayUtils.getStatusBarHeight(getResources());
    }

    private void initView() {
        this.initAvatarView();
        this.initContentView();
    }

    private void initAvatarView() {
        FrameLayout.LayoutParams containerParams = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
        int size = getResources().getDimensionPixelSize(R.dimen.large_icon_size);
        containerParams.width = size;
        containerParams.height = size + DisplayUtils.getStatusBarHeight(getResources());
        avatarContainer.setLayoutParams(containerParams);
        avatarContainer.setOnClickListener(v -> {});

        if (DisplayUtils.getGirdColumnCount(getContext()) > 1) {
            avatarContainer.setVisibility(GONE);
        } else {
            avatarContainer.setVisibility(VISIBLE);
        }
    }

    private void initContentView() {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setRefreshEnabled(false);
        refreshLayout.setLoadEnabled(false);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                navigationBarHeight + getResources().getDimensionPixelSize(R.dimen.normal_margin)
        );

        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.normal_margin);
            recyclerView.setPadding(margin, 0, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL)
        );
        recyclerView.setAdapter(
                new LargeLoadingStateAdapter(getContext(), 56),
                MultipleStateRecyclerView.STATE_LOADING
        );
        recyclerView.setAdapter(
                new LargeErrorStateAdapter(
                        getContext(), 56,
                        R.drawable.feedback_no_photos,
                        getContext().getString(R.string.feedback_load_failed_tv),
                        getContext().getString(R.string.feedback_click_retry),
                        this
                ), MultipleStateRecyclerView.STATE_ERROR
        );
        avatarScrollListener = new AvatarScrollListener(columnCount);
        recyclerView.addOnScrollListener(avatarScrollListener);

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // control.

    public void setAdapterAndMangeView(@NonNull FollowingAdapter adapter, PagerManageView view) {
        followingAdapter = adapter;
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        refreshLayout, recyclerView,
                        followingAdapter.getRealItemCount(), pagerManageView, 0, dy
                );
            }
        });
        pagerManageView = view;
    }

    /**
     * Set the offset of this view that was caused by nested scrolling. The offset value will
     * effect the position of avatar.
     *
     * @param offsetY The offset value.
     * */
    public void setOffsetY(float offsetY) {
        if (this.offsetY != offsetY) {
            this.offsetY = offsetY;
            if (avatarScrollListener != null && followingAdapter.getRealItemCount() > 0) {
                avatarScrollListener.onScrolled(recyclerView, 0, 0);
            }
        }
    }

    /**
     * Get the effective offset value. --> top of this view - bottom of status bar.
     *
     * @return The effective offset value.
     * */
    private float getAvatarFitStatusBarMarginTop() {
        return Math.max(-offsetY - avatarSize, 0);
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        boolean stateChanged = stateManagePresenter.setState(state, true);
        if (stateChanged) {
            MysplashActivity activity = Mysplash.getInstance().getTopActivity();
            if (activity != null && DisplayUtils.getGirdColumnCount(getContext()) == 1) {
                if (state == State.ERROR || state == State.LOADING) {
                    AnimUtils.animHide(avatarContainer);
                } else {
                    AnimUtils.animShow(avatarContainer);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        // do nothing.
    }

    @Override
    public void setSwipeRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setSwipeLoading(boolean loading) {
        refreshLayout.setLoading(loading);
    }

    @Override
    public void setPermitSwipeRefreshing(boolean permit) {
        refreshLayout.setRefreshEnabled(permit);
    }

    @Override
    public void setPermitSwipeLoading(boolean permit) {
        refreshLayout.setLoadEnabled(permit);
    }

    @Override
    public boolean checkNeedBackToTop() {
        return recyclerView.canScrollVertically(-1)
                && stateManagePresenter.getState() == State.NORMAL;
    }

    @Override
    public void scrollToPageTop() {
        avatarScrollListener.setAvatarImage(0);
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return false;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        pagerManageView.onRefresh(0);
    }

    @Override
    public void onLoad() {
        pagerManageView.onLoad(0);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        pagerManageView.onRefresh(0);
    }

    // on scroll listener.

    /**
     * This listener is used to control the avatar's position.
     * */
    private class AvatarScrollListener extends RecyclerView.OnScrollListener {

        private StaggeredGridLayoutManager manager;

        private int column;

        @Nullable private User lastActor;

        private int avatarPosition;
        private int lastAvatarPosition;

        AvatarScrollListener(int column) {
            this.column = column;

            this.lastActor = null;

            this.avatarPosition = 0;
            this.lastAvatarPosition = 0;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (column != 1) {
                return;
            }

            bindLayoutManager();
            if (manager == null) {
                return;
            }

            int firstVisibleItemPosition = manager.findFirstVisibleItemPositions(null)[0];
            if (followingAdapter.isFooterView(firstVisibleItemPosition)) {
                // the first visible item is a footer item.
                // --> the second visible item is a header item (title item).
                View firstVisibleView = manager.findViewByPosition(firstVisibleItemPosition);
                View secondVisibleView = manager.findViewByPosition(firstVisibleItemPosition + 1);
                if (firstVisibleView != null && secondVisibleView != null) {
                    float footerBottom = firstVisibleView.getY() + firstVisibleView.getMeasuredHeight();
                    float headerTop = secondVisibleView.getY();

                    if (footerBottom < avatarSize + getAvatarFitStatusBarMarginTop()
                            && headerTop > getAvatarFitStatusBarMarginTop()) {
                        // the footer item is moving out of the screen, and the header item has
                        // not yet reached the trigger position.
                        // --> the avatar needs to move with footer item.
                        avatarContainer.setTranslationY(footerBottom - avatarSize - statusBarHeight);
                        lastAvatarPosition = avatarPosition;
                        avatarPosition = firstVisibleItemPosition;
                        setAvatarAppearance(recyclerView);
                    } else {
                        // the footer item is moving out of the screen, and the header item has
                        // already reached the trigger position.
                        // --> the avatar needs to move with header item.
                        avatarContainer.setTranslationY(-statusBarHeight + getAvatarFitStatusBarMarginTop());
                        lastAvatarPosition = avatarPosition;
                        avatarPosition = firstVisibleItemPosition + (headerTop <= getAvatarFitStatusBarMarginTop() ? 1 : 0);
                        setAvatarAppearance(recyclerView);
                    }
                }
            } else {
                // the first item is not a footer item.
                // --> avatar needs to stay on the trigger position.
                avatarContainer.setTranslationY(-statusBarHeight + getAvatarFitStatusBarMarginTop());
                lastAvatarPosition = avatarPosition;
                avatarPosition = firstVisibleItemPosition;
                setAvatarAppearance(recyclerView);
            }
        }

        private void bindLayoutManager() {
            if (recyclerView.getLayoutManager() != null
                    && recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                this.manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            }
        }

        private void setAvatarAppearance(RecyclerView recyclerView) {
            User user = followingAdapter.getUser(avatarPosition);
            if (user == null) {
                return;
            }

            if (lastActor == null || !lastActor.username.equals(user.username)) {
                setAvatarImage(avatarPosition);
            }

            followingAdapter.setTitleAvatarVisibility(
                    recyclerView.findViewHolderForAdapterPosition(lastAvatarPosition),
                    recyclerView.findViewHolderForAdapterPosition(avatarPosition)
            );
        }

        private void setAvatarImage(int position) {
            lastActor = followingAdapter.getUser(position);
            if (lastActor != null) {
                ImageHelper.loadAvatar(getContext(), avatar, lastActor, null);
            }
        }
    }
}