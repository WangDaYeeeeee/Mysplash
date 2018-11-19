package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.FollowingResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.FollowingModel;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.FollowingPresenter;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.FollowingView;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.ScrollView;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeErrorStateAdapter;
import com.wangdaye.mysplash.common.ui.adapter.multipleState.LargeLoadingStateAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.model.widget.FollowingObject;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.FollowingImplementor;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Following feed view.
 *
 * This view is used to show following feeds for
 * {@link com.wangdaye.mysplash.main.view.fragment.FollowingFragment}.
 *
 * */

public class FollowingFeedView extends NestedScrollFrameLayout
        implements FollowingView, LoadView, ScrollView,
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener, LargeErrorStateAdapter.OnRetryListener {

    @BindView(R.id.container_following_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_following_list_recyclerView)
    MultipleStateRecyclerView recyclerView;

    @BindView(R.id.container_following_avatar_avatarContainer)
    RelativeLayout avatarContainer;

    @BindView(R.id.container_following_avatar_background)
    FrameLayout avatarBackground;

    @BindView(R.id.container_following_avatar_avatar)
    CircleImageView avatar;

    @BindView(R.id.container_following_avatar_verbIcon)
    ImageView verbIcon;

    private AvatarScrollListener avatarScrollListener;

    private FollowingModel followingModel;
    private FollowingPresenter followingPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private ScrollModel scrollModel;
    private ScrollPresenter scrollPresenter;

    // the offset of this view that was caused by nested scrolling. The avatar should ensure the
    // location by this value.
    private float offsetY = 0;
    private float AVATAR_SIZE;
    private float STATUS_BAR_HEIGHT;

    private static class SavedState extends BaseSavedState {

        String nextPage;
        boolean over;
        float offsetY;

        SavedState(FollowingFeedView view, Parcelable superState) {
            super(superState);
            this.nextPage = view.followingModel.getNextPage();
            this.over = view.followingModel.isOver();
            this.offsetY = view.offsetY;
        }

        private SavedState(Parcel in) {
            super(in);
            this.nextPage = in.readString();
            this.over = in.readByte() != 0;
            this.offsetY = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.nextPage);
            out.writeByte(this.over ? (byte) 1 : (byte) 0);
            out.writeFloat(this.offsetY);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public FollowingFeedView(Context context) {
        super(context);
        this.initialize();
    }

    public FollowingFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public FollowingFeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_list_2, null);
        addView(contentView);

        View avatarView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_following_avatar, null);
        addView(avatarView);

        ButterKnife.bind(this, this);
        initModel();
        initPresenter();
        initView();
    }

    private void initModel() {
        this.followingModel = new FollowingObject(
                new FollowingAdapter(
                        getContext(),
                        new ArrayList<FollowingResult>(Mysplash.DEFAULT_PER_PAGE)));
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);

        this.AVATAR_SIZE = new DisplayUtils(getContext()).dpToPx(56);
        this.STATUS_BAR_HEIGHT = DisplayUtils.getStatusBarHeight(getResources());
    }

    private void initPresenter() {
        this.followingPresenter = new FollowingImplementor(followingModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
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
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setPermitLoad(false);

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                navigationBarHeight + getResources().getDimensionPixelSize(R.dimen.normal_margin));

        int columnCount = DisplayUtils.getGirdColumnCount(getContext());
        recyclerView.setAdapter(followingPresenter.getAdapter());
        if (columnCount > 1) {
            int margin = getResources().getDimensionPixelSize(R.dimen.little_margin);
            recyclerView.setPadding(margin, 0, 0, 0);
        } else {
            recyclerView.setPadding(0, 0, 0, 0);
        }
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
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
        recyclerView.addOnScrollListener(scrollListener);
        avatarScrollListener = new AvatarScrollListener(columnCount);
        recyclerView.addOnScrollListener(avatarScrollListener);

        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        followingPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    // save state.

    @Override
    public Parcelable onSaveInstanceState() {
        return new SavedState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        followingPresenter.setNextPage(ss.nextPage);
        followingPresenter.setOver(ss.over);
        setOffsetY(ss.offsetY);
    }

    // control.

    /**
     * Set activity for the adapter in this view.
     *
     * @param a Container activity.
     * */
    public void setActivity(MysplashActivity a) {
        followingPresenter.setActivityForAdapter(a);
        loadPresenter.bindActivity(a);
    }

    @Override
    public boolean isParentOffset() {
        return false;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection, Bundle bundle) {
        if (headDirection) {
            return followingPresenter.getAdapter().getPhotoListToAnIndex(bundle, headIndex - 1);
        } else {
            return followingPresenter.getAdapter().getPhotoListFromAnIndex(bundle, headIndex + list.size());
        }
    }

    public boolean isNormalState() {
        return loadPresenter.getLoadState() == LoadModel.NORMAL_STATE;
    }

    // following feed.

    /**
     * Get the following feeds from the adapter in this view.
     *
     * @return Following feeds in adapter.
     * */
    public List<FollowingResult> getFeeds() {
        return followingPresenter.getAdapter().getFeeds();
    }

    /**
     * Set Following feeds to the adapter in this view.
     *
     * @param list Following feeds that will be set to the adapter.
     * */
    public void setFeeds(List<FollowingResult> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        followingPresenter.getAdapter().setFeeds(list);
        if (list.size() == 0) {
            initRefresh();
        } else {
            loadPresenter.setNormalState();
        }
    }

    public void updatePhoto(Photo p, boolean refreshView) {
        followingPresenter.getAdapter().updatePhoto(recyclerView, p, refreshView, true);
    }

    // HTTP request.

    public void initRefresh() {
        followingPresenter.initRefresh(getContext());
    }

    public void cancelRequest() {
        followingPresenter.cancelRequest();
    }

    // back to top.

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    // UI.

    /**
     * Set the offset of this view that was caused by nested scrolling. The offset value will
     * effect the position of avatar.
     *
     * @param offsetY The offset value.
     * */
    public void setOffsetY(float offsetY) {
        if (this.offsetY != offsetY) {
            this.offsetY = offsetY;
            if (avatarScrollListener != null && followingPresenter.getAdapter().getRealItemCount() > 0) {
                avatarScrollListener.onScrolled(recyclerView, 0, 0);
            }
        }
    }

    public float getOffsetY() {
        return offsetY;
    }

    /**
     * Get the effective offset value. --> top of this view - bottom of status bar.
     * 
     * @return The effective offset value.
     * */
    private float getRealOffset() {
        return Math.max(getOffsetY() - AVATAR_SIZE, 0);
    }

    // interface.

    // on click listener.

    @OnClick(R.id.container_following_avatar_avatarContainer) void clickAvatarContainer() {
        // do nothing.
    }

    @OnClick(R.id.container_following_avatar_avatar) void clickAvatar() {
        if (recyclerView.getLayoutManager() != null) {
            int adapterPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPositions(null)[0];
            IntentHelper.startUserActivity(
                    Mysplash.getInstance().getTopActivity(),
                    avatar,
                    avatarBackground,
                    followingPresenter.getAdapter().getActor(adapterPosition),
                    UserActivity.PAGE_PHOTO);
        }
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        followingPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        followingPresenter.loadMore(getContext(), false);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        followingPresenter.initRefresh(getContext());
    }

    // on scroll listener.

    /**
     * This listener is used to control the automatic load of {@link RecyclerView}.
     * */
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    /**
     * This listener is used to control the avatar's position.
     * */
    private class AvatarScrollListener extends RecyclerView.OnScrollListener {
        // widget
        private StaggeredGridLayoutManager manager;

        // data
        private int column;

        private User lastActor;
        private String lastVerb;

        private int avatarPosition;
        private int lastAvatarPosition;

        // life cycle.

        AvatarScrollListener(int column) {
            this.column = column;

            this.lastActor = null;
            this.lastVerb = null;

            this.avatarPosition = 0;
            this.lastAvatarPosition = 0;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (column != 1) {
                return;
            }

            bindLayoutManager();

            int firstVisibleItemPosition = manager.findFirstVisibleItemPositions(null)[0];
            if (followingPresenter.getAdapter().isFooterView(firstVisibleItemPosition)) {
                // the first visible item is a footer item.
                // --> the second visible item is a header item (title item).
                View firstVisibleView = manager.findViewByPosition(firstVisibleItemPosition);
                View secondVisibleView = manager.findViewByPosition(firstVisibleItemPosition + 1);
                if (firstVisibleView != null && secondVisibleView != null) {
                    float footerBottom = firstVisibleView.getY() + firstVisibleView.getMeasuredHeight();
                    float headerTop = secondVisibleView.getY();

                    if (footerBottom < AVATAR_SIZE + getRealOffset() && headerTop > getRealOffset()) {
                        // the footer item is moving out of the screen, and the header item has
                        // not yet reached the trigger position.
                        // --> the avatar needs to move with footer item.
                        avatarContainer.setTranslationY(footerBottom - AVATAR_SIZE - STATUS_BAR_HEIGHT);
                        lastAvatarPosition = avatarPosition;
                        avatarPosition = firstVisibleItemPosition;
                        setAvatarAppearance(recyclerView);
                    } else {
                        // the footer item is moving out of the screen, and the header item has
                        // already reached the trigger position.
                        // --> the avatar needs to move with header item.
                        avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
                        lastAvatarPosition = avatarPosition;
                        avatarPosition = firstVisibleItemPosition + (headerTop <= getRealOffset() ? 1 : 0);
                        setAvatarAppearance(recyclerView);
                    }
                }
            } else {
                // the first item is not a footer item.
                // --> avatar needs to stay on the trigger position.
                avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
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
            User user = followingPresenter.getAdapter().getActor(avatarPosition);
            if (lastActor == null || !lastActor.username.equals(user.username)) {
                setAvatarImage(avatarPosition);
            }
            setAvatarVerb(avatarPosition);

            followingPresenter.getAdapter()
                    .setTitleAvatarVisibility(recyclerView, lastAvatarPosition, avatarPosition);
        }

        private void setAvatarImage(int position) {
            lastActor = followingPresenter.getAdapter().getActor(position);
            ImageHelper.loadAvatar(getContext(), avatar, lastActor, 0, null);
        }

        private void setAvatarVerb(int position) {
            String verb = followingPresenter.getAdapter().getVerb(position);
            if (TextUtils.isEmpty(lastVerb) || !lastVerb.equals(verb)) {
                lastVerb = verb;
                switch (verb) {
                    case FollowingResult.VERB_LIKED:
                        verbIcon.setImageResource(R.drawable.ic_verb_liked);
                        break;

                    case FollowingResult.VERB_COLLECTED:
                        verbIcon.setImageResource(R.drawable.ic_verb_collected);
                        break;

                    case FollowingResult.VERB_FOLLOWED:
                        verbIcon.setImageResource(
                                ThemeManager.getInstance(getContext()).isLightTheme() ?
                                        R.drawable.ic_verb_followed_light : R.drawable.ic_verb_followed_dark);
                        break;

                    case FollowingResult.VERB_RELEASE:
                        verbIcon.setImageResource(R.drawable.ic_verb_published);
                        break;

                    case FollowingResult.VERB_PUBLISHED:
                        verbIcon.setImageResource(R.drawable.ic_verb_published);
                        break;

                    case FollowingResult.VERB_CURATED:
                        verbIcon.setImageResource(R.drawable.ic_verb_curated);
                        break;
                }
            }
        }
    }

    // view.

    // category view.

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
    public void requestFollowingFeedSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestFollowingFeedFailed(String feedback) {
        if (followingPresenter.getAdapter().getRealItemCount() > 0) {
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
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
        if (DisplayUtils.getGirdColumnCount(getContext()) == 1) {
            animHide(avatarContainer);
        }
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        refreshLayout.setPermitRefresh(false);
        refreshLayout.setPermitLoad(false);
        recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
        if (DisplayUtils.getGirdColumnCount(getContext()) == 1) {
            animHide(avatarContainer);
        }
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        if (activity != null) {
            DisplayUtils.setNavigationBarStyle(
                    activity, true, activity.hasTranslucentNavigationBar());
        }
        refreshLayout.setPermitRefresh(true);
        refreshLayout.setPermitLoad(true);
        recyclerView.setState(MultipleStateRecyclerView.STATE_NORMALLY);
        if (DisplayUtils.getGirdColumnCount(getContext()) == 1) {
            animShow(avatarContainer);
        }
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        avatarScrollListener.setAvatarImage(0);
        avatarScrollListener.setAvatarVerb(0);
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        if (recyclerView.getLayoutManager() != null) {
            int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPositions(null);
            int totalItemCount = followingPresenter.getAdapter().getRealItemCount();
            if (followingPresenter.canLoadMore()
                    && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                    && totalItemCount > 0
                    && dy > 0) {
                followingPresenter.loadMore(getContext(), false);
            }
            if (!recyclerView.canScrollVertically(-1)) {
                scrollPresenter.setToTop(true);
            } else {
                scrollPresenter.setToTop(false);
            }
            if (!recyclerView.canScrollVertically(1) && followingPresenter.isLoading()) {
                refreshLayout.setLoading(true);
            }
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }
}