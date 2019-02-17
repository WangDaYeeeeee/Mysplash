package com.wangdaye.mysplash.user.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.transition.TransitionManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.model.UserModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.presenter.UserPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.i.view.UserView;
import com.wangdaye.mysplash.common.ui.adapter.MiniTagAdapter;
import com.wangdaye.mysplash.common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.user.model.widget.LoadObject;
import com.wangdaye.mysplash.user.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.user.model.widget.UserObject;
import com.wangdaye.mysplash.user.presenter.widget.UserImplementor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User profile view.
 *
 * This view is used to show user's profile.
 *
 * */

public class UserProfileView extends FrameLayout
        implements UserView, LoadView,
        RippleButton.OnSwitchListener {

    @BindView(R.id.container_user_profile_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_user_profile_profileContainer)
    LinearLayout profileContainer;

    @BindView(R.id.container_user_profile_tagList)
    RecyclerView tagList;

    @BindView(R.id.container_user_profile_bio)
    TextView bioTxt;

    @BindView(R.id.container_user_profile_locationContainer)
    RelativeLayout locationContainer;

    @BindView(R.id.container_user_profile_locationTxt)
    TextView locationTxt;

    @BindView(R.id.container_user_profile_followBtn)
    RippleButton rippleButton;

    private MyPagerAdapter adapter;

    private UserModel userModel;
    private UserPresenter userPresenter;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

    private OnRequestUserListener listener;

    public UserProfileView(Context context) {
        super(context);
        this.initialize();
    }

    public UserProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    // init.

    @SuppressLint("InflateParams")
    private void initialize() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_user_profile, null);
        addView(v);

        ButterKnife.bind(this, this);
        initModel();
        initPresenter();
        initView();
    }

    // init.

    private void initModel() {
        this.userModel = new UserObject();
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
    }

    private void initPresenter() {
        this.userPresenter = new UserImplementor(userModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    private void initView() {
        progressView.setVisibility(VISIBLE);
        profileContainer.setVisibility(GONE);

        tagList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        if (AuthManager.getInstance().isAuthorized() && Mysplash.hasNode()) {
            rippleButton.setOnSwitchListener(this);
        } else {
            rippleButton.setVisibility(GONE);
        }
    }

    // control.

    @Nullable
    private ViewParent getAppBarParent() {
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof NestedScrollAppBarLayout)) {
            parent = parent.getParent();
        }
        return parent;
    }

    public User getUser() {
        return userPresenter.getUser();
    }

    public void setUser(User user, MyPagerAdapter adapter) {
        this.adapter = adapter;
        userPresenter.setUser(user);
    }

    public String getUserPortfolio() {
        return userPresenter.getUser().portfolio_url;
    }

    // HTTP request.

    public void requestUserProfile() {
        userPresenter.requestUser();
    }

    public void cancelRequest() {
        userPresenter.cancelRequest();
    }

    // interface.

    // on click listener.

    @OnClick(R.id.container_user_profile_locationContainer) void clickLocation() {
        if (!TextUtils.isEmpty(locationTxt.getText())) {
            IntentHelper.startSearchActivity(
                    Mysplash.getInstance().getTopActivity(),
                    locationTxt.getText().toString());
        }
    }

    // on request user listener.

    public interface OnRequestUserListener {
        void onRequestUserSucceed(User u);
    }

    public void setOnRequestUserListener(OnRequestUserListener l) {
        this.listener = l;
    }

    // on switch listener.

    @Override
    public void onSwitch(boolean switchTo) {
        if (switchTo) {
            userPresenter.followUser();
        } else {
            userPresenter.cancelFollowUser();
        }
    }

    // view.

    // user data view.

    @SuppressLint("SetTextI18n")
    @Override
    public void drawUserInfo(User u) {
        if (listener != null) {
            listener.onRequestUserSucceed(u);
        }

        ViewParent parent = getAppBarParent();
        if (parent != null) {
            TransitionManager.beginDelayedTransition((ViewGroup) parent);
        }

        if (u.tags == null || u.tags.custom == null || u.tags.custom.size() == 0) {
            tagList.setVisibility(GONE);
        } else {
            tagList.setAdapter(new MiniTagAdapter(u.tags.custom));
        }

        if (!TextUtils.isEmpty(u.bio)) {
            bioTxt.setText(u.bio);
        } else {
            bioTxt.setVisibility(GONE);
        }

        if (TextUtils.isEmpty(u.location)) {
            locationContainer.setVisibility(GONE);
        } else {
            locationTxt.setText(u.location);
        }

        rippleButton.forceSwitch(u.followed_by_user);

        List<String> titleList = new ArrayList<>();
        titleList.add(
                DisplayUtils.abridgeNumber(u.total_photos)
                        + " " + getResources().getStringArray(R.array.user_tabs)[0]);
        titleList.add(
                DisplayUtils.abridgeNumber(u.total_likes)
                        + " " + getResources().getStringArray(R.array.user_tabs)[1]);
        titleList.add(
                DisplayUtils.abridgeNumber(u.total_collections)
                        + " " + getResources().getStringArray(R.array.user_tabs)[2]);
        adapter.titleList = titleList;
        adapter.notifyDataSetChanged();

        loadPresenter.setNormalState();
    }

    @Override
    public void initRefreshStart() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void followRequestSuccess(boolean follow) {
        User user = getUser();
        user.followed_by_user = follow;
        if (follow) {
            user.followers_count ++;
        } else {
            user.followers_count --;
        }
        setUser(user, adapter);
        rippleButton.setSwitchResult(true);
    }

    @Override
    public void followRequestFailed(boolean follow) {
        rippleButton.setSwitchResult(false);
        if (follow) {
            NotificationHelper.showSnackbar(getContext().getString(R.string.feedback_follow_failed));
        } else {
            NotificationHelper.showSnackbar(getContext().getString(R.string.feedback_cancel_follow_failed));
        }
    }

    // load view.

    @Override
    public void animShow(final View v) {
        AnimUtils.animShow(v);
    }

    @Override
    public void animHide(final View v) {
        AnimUtils.animHide(v);
    }

    @Override
    public void setLoadingState(@Nullable MysplashActivity activity, int old) {
        animShow(progressView);
        animHide(profileContainer);
    }

    @Override
    public void setFailedState(@Nullable MysplashActivity activity, int old) {
        // do nothing.
    }

    @Override
    public void setNormalState(@Nullable MysplashActivity activity, int old) {
        animShow(profileContainer);
        animHide(progressView);
    }
}
