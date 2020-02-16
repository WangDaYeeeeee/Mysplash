package com.wangdaye.main.ui.following;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.presenter.LoadImagePresenter;
import com.wangdaye.common.utils.BackToTopUtils;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.R;
import com.wangdaye.main.R2;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;
import com.wangdaye.main.ui.following.adapter.holder.TitleFeedHolder;
import com.wangdaye.main.ui.following.decoration.FollowingItemDecoration;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.presenter.pager.PagerScrollablePresenter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.helper.RecyclerViewHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.common.presenter.pager.PagerStateManagePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ViewConstructor")
public class FollowingHomePageView extends FrameLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        LargeErrorStateAdapter.OnRetryListener {

    @BindView(R2.id.container_following_list_swipeRefreshLayout) BothWaySwipeRefreshLayout refreshLayout;
    @BindView(R2.id.container_following_list_recyclerView) MultipleStateRecyclerView recyclerView;
    private FollowingAdapter followingAdapter;

    @BindView(R2.id.container_following_avatar_avatarContainer) RelativeLayout avatarContainer;
    @BindView(R2.id.container_following_avatar_background) FrameLayout avatarBackground;
    @BindView(R2.id.container_following_avatar_avatar) CircularImageView avatar;
    @OnClick(R2.id.container_following_avatar_avatar) void clickAvatar() {
        if (recyclerView.getLayoutManager() != null) {
            int adapterPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPositions(null)[0];
            User user = followingAdapter.getUser(adapterPosition);
            if (user != null) {
                ComponentFactory.getUserModule().startUserActivity(
                        MysplashApplication.getInstance().getTopActivity(),
                        avatar,
                        avatarBackground,
                        user,
                        ProfilePager.PAGE_PHOTO
                );
            }
        }
    }

    private AvatarScrollListener avatarScrollListener;

    private PagerStateManagePresenter stateManagePresenter;

    protected boolean selected;
    protected int index;
    protected PagerManageView pagerManageView;

    // the offset of this view that was caused by nested scrolling. The avatar should ensure the
    // location by this value.
    private float offsetY = 0;
    private float appBarHeight = 0;
    private float avatarSize;
    private float statusBarHeight;
    private int columnCount;

    public FollowingHomePageView(MainActivity a, FollowingAdapter adapter,
                                 boolean selected, int index, PagerManageView v) {
        super(a);
        this.init(adapter, selected, index, v);
    }

    // init.

    @SuppressLint("InflateParams")
    private void init(FollowingAdapter adapter, boolean selected, int index, PagerManageView v) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_list_2, null);
        addView(contentView);

        View avatarView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_avatar, null);
        addView(avatarView);

        ButterKnife.bind(this, this);
        initData(selected, index, v);
        initView(adapter);
    }

    private void initData(boolean selected, int index, PagerManageView v) {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.large_icon_size);
        this.statusBarHeight = DisplayUtils.getStatusBarHeight(getResources());
        this.columnCount = RecyclerViewHelper.getGirdColumnCount(getContext());

        this.selected = selected;
        this.index = index;
        this.pagerManageView = v;
    }

    private void initView(FollowingAdapter adapter) {
        this.initAvatarView();
        this.initContentView(adapter);
    }

    private void initAvatarView() {
        FrameLayout.LayoutParams containerParams = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
        containerParams.width = (int) avatarSize;
        containerParams.height = (int) (avatarSize + DisplayUtils.getStatusBarHeight(getResources()));
        avatarContainer.setLayoutParams(containerParams);
        avatarContainer.setOnClickListener(v -> {});

        if (columnCount > 1) {
            avatarContainer.setVisibility(GONE);
        } else {
            avatarContainer.setVisibility(VISIBLE);
        }
    }

    private void initContentView(FollowingAdapter adapter) {
        refreshLayout.setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        refreshLayout.setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setRefreshEnabled(false);
        refreshLayout.setLoadEnabled(false);

        refreshLayout.post(() -> refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                MysplashApplication.getInstance().getWindowInsets().bottom
                        + getResources().getDimensionPixelSize(R.dimen.normal_margin)
        ));

        this.followingAdapter = adapter;

        recyclerView.setAdapter(followingAdapter);
        recyclerView.setLayoutManager(
                RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(columnCount));
        recyclerView.addItemDecoration(new FollowingItemDecoration(getContext()));
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        refreshLayout,
                        recyclerView,
                        followingAdapter.getItemCount(),
                        pagerManageView,
                        index,
                        dy
                );
            }
        });
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // control.

    /**
     * Set the offset of this view that was caused by nested scrolling. The offset value will
     * effect the position of avatar.
     *
     * @param offsetY The offset value.
     * */
    public void setOffsetY(float offsetY, float appBarHeight) {
        if (this.offsetY != offsetY || this.appBarHeight != appBarHeight) {
            this.offsetY = offsetY;
            this.appBarHeight = appBarHeight;
            if (avatarScrollListener != null && followingAdapter.getItemCount() > 0) {
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
        return Math.max(statusBarHeight - offsetY - appBarHeight, 0);
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        boolean stateChanged = stateManagePresenter.setState(state);
        if (stateChanged) {
            MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
            if (activity != null && columnCount == 1) {
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
        pagerManageView.onRefresh(index);
    }

    @Override
    public void onLoad() {
        pagerManageView.onLoad(index);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        pagerManageView.onRefresh(index);
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

            RecyclerView.ViewHolder lastHolder
                    = recyclerView.findViewHolderForAdapterPosition(lastAvatarPosition);
            if (lastHolder instanceof TitleFeedHolder) {
                ((TitleFeedHolder) lastHolder).setAvatarVisibility(true);
            }

            RecyclerView.ViewHolder newHolder
                    = recyclerView.findViewHolderForAdapterPosition(avatarPosition);
            if (newHolder instanceof TitleFeedHolder) {
                ((TitleFeedHolder) newHolder).setAvatarVisibility(false);
            }
        }

        private void setAvatarImage(int position) {
            lastActor = followingAdapter.getUser(position);
            LoadImagePresenter.loadUserAvatar(getContext(), avatar, lastActor, null);
        }
    }
}