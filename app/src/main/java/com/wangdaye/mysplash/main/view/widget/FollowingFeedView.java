package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
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
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
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
        BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {

    @BindView(R.id.container_loading_in_following_view_large_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_loading_in_following_view_large_feedbackContainer)
    RelativeLayout feedbackContainer;

    @BindView(R.id.container_loading_in_following_view_large_feedbackTxt)
    TextView feedbackText;

    @BindView(R.id.container_photo_list_swipeRefreshLayout)
    BothWaySwipeRefreshLayout refreshLayout;

    @BindView(R.id.container_photo_list_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.container_following_avatar_avatarContainer)
    RelativeLayout avatarContainer;

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
        View searchingView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_loading_in_following_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list, null);
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
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
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
        this.initLoadingView();
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
        refreshLayout.setVisibility(GONE);

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
        recyclerView.addOnScrollListener(scrollListener);
        avatarScrollListener = new AvatarScrollListener();
        recyclerView.addOnScrollListener(avatarScrollListener);

        followingPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    private void initLoadingView() {
        progressView.setVisibility(VISIBLE);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = ButterKnife.findById(
                this, R.id.container_loading_in_following_view_large_feedbackImg);
        ImageHelper.loadResourceImage(getContext(), feedbackImg, R.drawable.feedback_no_photos);
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
        followingPresenter.getAdapter().updatePhoto(p, refreshView, true);
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
        int adapterPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPositions(null)[0];
        IntentHelper.startUserActivity(
                Mysplash.getInstance().getTopActivity(),
                avatar,
                followingPresenter.getAdapter().getActor(adapterPosition),
                UserActivity.PAGE_PHOTO);
    }

    @OnClick(R.id.container_loading_in_following_view_large_feedbackBtn) void retryRefresh() {
        followingPresenter.initRefresh(getContext());
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

    // on scroll listener.

    /**
     * This listener is used to control the automatic load of {@link RecyclerView}.
     * */
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
        private User lastActor;
        private String lastVerb;

        // life cycle.

        AvatarScrollListener() {
            this.manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            this.lastActor = null;
            this.lastVerb = null;
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
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
                        User user = followingPresenter.getAdapter().getActor(firstVisibleItemPosition);
                        if (lastActor == null || !lastActor.username.equals(user.username)) {
                            setAvatarImage(firstVisibleItemPosition);
                        }
                        setAvatarVerb(firstVisibleItemPosition);
                    } else {
                        // the footer item is moving out of the screen, and the header item has
                        // already reached the trigger position.
                        // --> the avatar needs to move with header item.
                        avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
                        User user = followingPresenter.getAdapter()
                                .getActor(firstVisibleItemPosition + (headerTop <= getRealOffset() ? 1 : 0));
                        if (lastActor == null || !lastActor.username.equals(user.username)) {
                            setAvatarImage(firstVisibleItemPosition + (headerTop <= getRealOffset() ? 1 : 0));
                        }
                        setAvatarVerb(firstVisibleItemPosition + (headerTop <= getRealOffset() ? 1 : 0));
                    }
                }
            } else {
                // the first item is not a footer item.
                // --> avatar needs to stay on the trigger position.
                avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
                User user = followingPresenter.getAdapter().getActor(firstVisibleItemPosition);
                if (lastActor == null || !lastActor.username.equals(user.username)) {
                    setAvatarImage(firstVisibleItemPosition);
                }
                setAvatarVerb(firstVisibleItemPosition);
            }
        }

        private void setAvatarImage(int position) {
            lastActor = followingPresenter.getAdapter().getActor(position);
            ImageHelper.loadAvatar(getContext(), avatar, lastActor, null);
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
            feedbackText.setText(feedback);
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
    public void setLoadingState() {
        animShow(progressView);
        animHide(feedbackContainer);
        animHide(refreshLayout);
    }

    @Override
    public void setFailedState() {
        animShow(feedbackContainer);
        animHide(progressView);
        animHide(refreshLayout);
    }

    @Override
    public void setNormalState() {
        animShow(refreshLayout);
        animHide(progressView);
        animHide(feedbackContainer);
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
        int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPositions(null);
        int totalItemCount = followingPresenter.getAdapter().getRealItemCount();
        if (followingPresenter.canLoadMore()
                && lastVisibleItems[lastVisibleItems.length - 1] >= totalItemCount - 10
                && totalItemCount > 0
                && dy > 0) {
            followingPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && followingPresenter.isLoading()) {
            refreshLayout.setLoading(true);
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }
}