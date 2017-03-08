package com.wangdaye.mysplash.user.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.UserModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.UserPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.UserView;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash.user.model.widget.LoadObject;
import com.wangdaye.mysplash.user.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.user.model.widget.UserObject;
import com.wangdaye.mysplash.user.presenter.widget.UserImplementor;

import java.util.ArrayList;
import java.util.List;

/**
 * User profile view.
 * */

public class UserProfileView extends FrameLayout
        implements UserView, LoadView,
        RippleButton.OnSwitchListener {
    // model.
    private UserModel userModel;
    private LoadModel loadModel;

    // view.
    private CircularProgressView progressView;

    private RelativeLayout profileContainer;
    private RippleButton rippleButton;
    private TextView locationTxt;
    private TextView bioTxt;

    private MyPagerAdapter adapter;

    // presenter.
    private UserPresenter userPresenter;
    private LoadPresenter loadPresenter;

    /** <br> life cycle. */

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_user_profile, null);
        addView(v);

        initModel();
        initPresenter();
        initView();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.userPresenter = new UserImplementor(userModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    /** <br> view. */

    private void initView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_user_profile_progressView);
        progressView.setVisibility(VISIBLE);

        this.rippleButton = (RippleButton) findViewById(R.id.container_user_profile_followBtn);
        if (AuthManager.getInstance().isAuthorized()) {
            rippleButton.setOnSwitchListener(this);
        } else {
            rippleButton.setVisibility(GONE);
        }

        this.profileContainer = (RelativeLayout) findViewById(R.id.container_user_profile_profileContainer);
        profileContainer.setVisibility(GONE);

        this.locationTxt = (TextView) findViewById(R.id.container_user_profile_locationTxt);
        DisplayUtils.setTypeface(getContext(), locationTxt);

        this.bioTxt = (TextView) findViewById(R.id.container_user_profile_bio);
        DisplayUtils.setTypeface(getContext(), bioTxt);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) findViewById(R.id.container_user_profile_locationIcon)).setImageResource(R.drawable.ic_location_light);
        } else {
            ((ImageView) findViewById(R.id.container_user_profile_locationIcon)).setImageResource(R.drawable.ic_location_dark);
        }
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.userModel = new UserObject();
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
    }

    // interface.

    public void setUser(User user) {
        userPresenter.setUser(user);
    }

    public void requestUserProfile(MyPagerAdapter adapter) {
        this.adapter = adapter;
        userPresenter.requestUser();
    }

    public void cancelRequest() {
        userPresenter.cancelRequest();
    }

    public User getUser() {
        return userPresenter.getUser();
    }

    public String getUserPortfolio() {
        return userPresenter.getUser().portfolio_url;
    }

    /** <br> interface. */

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
        rippleButton.forceSwitch(u.followed_by_user);

        if (!TextUtils.isEmpty(u.location)) {
            locationTxt.setText(u.location);
        } else {
            locationTxt.setText("Unknown");
        }

        if (!TextUtils.isEmpty(u.bio)) {
            bioTxt.setText(u.bio);
        } else {
            bioTxt.setVisibility(GONE);
        }

        List<String> titleList = new ArrayList<>();
        titleList.add(u.total_photos + " " + getResources().getStringArray(R.array.user_tabs)[0]);
        titleList.add(u.total_collections + " " + getResources().getStringArray(R.array.user_tabs)[1]);
        titleList.add(u.total_likes + " " + getResources().getStringArray(R.array.user_tabs)[2]);
        adapter.titleList = titleList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initRefreshStart() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void requestDetailsSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestDetailsFailed() {
        loadPresenter.setFailedState();
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
        setUser(user);
        rippleButton.setSwitchResult(true);
    }

    @Override
    public void followRequestFailed(boolean follow) {
        rippleButton.setSwitchResult(false);
        if (follow) {
            NotificationHelper.showSnackbar(
                    getContext().getString(R.string.feedback_follow_failed),
                    Snackbar.LENGTH_SHORT);
        } else {
            NotificationHelper.showSnackbar(
                    getContext().getString(R.string.feedback_cancel_follow_failed),
                    Snackbar.LENGTH_SHORT);
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
    public void setLoadingState() {
        animShow(progressView);
        animHide(profileContainer);
    }

    @Override
    public void setFailedState() {
        // do nothing.
    }

    @Override
    public void setNormalState() {
        animShow(profileContainer);
        animHide(progressView);
    }

    @Override
    public void resetLoadingState() {
        // do nothing.
    }
}
