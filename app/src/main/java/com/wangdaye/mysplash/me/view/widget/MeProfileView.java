package com.wangdaye.mysplash.me.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Me;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;

/**
 * Me profile view.
 * */

public class MeProfileView  extends FrameLayout
        implements LoadView,
        RippleButton.OnSwitchListener {
    // model.
    private LoadModel loadModel;

    // view.
    private CircularProgressView progressView;

    private RelativeLayout profileContainer;
    private RippleButton rippleButton;
    private TextView locationTxt;
    private TextView bioTxt;

    // presenter.
    private LoadPresenter loadPresenter;

    /** <br> life cycle. */

    public MeProfileView(Context context) {
        super(context);
        this.initialize();
    }

    public MeProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public MeProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MeProfileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_user_profile_progressView);
        progressView.setVisibility(VISIBLE);

        this.rippleButton = (RippleButton) findViewById(R.id.container_user_profile_followBtn);
        rippleButton.setDontAnimate(true);
        rippleButton.setOnSwitchListener(this);

        this.profileContainer = (RelativeLayout) findViewById(R.id.container_user_profile_profileContainer);
        profileContainer.setVisibility(GONE);

        this.locationTxt = (TextView) findViewById(R.id.container_user_profile_locationTxt);
        DisplayUtils.setTypeface(getContext(), locationTxt);

        this.bioTxt = (TextView) findViewById(R.id.container_user_profile_bio);
        DisplayUtils.setTypeface(getContext(), bioTxt);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) findViewById(R.id.container_user_profile_locationIcon))
                    .setImageResource(R.drawable.ic_location_light);
        } else {
            ((ImageView) findViewById(R.id.container_user_profile_locationIcon))
                    .setImageResource(R.drawable.ic_location_dark);
        }
    }

    // interface.

    @SuppressLint("SetTextI18n")
    public void drawMeProfile(Me me) {
        ViewParent parent = getAppBarParent();
        if (parent != null && parent instanceof NestedScrollAppBarLayout) {
            TransitionManager.beginDelayedTransition((ViewGroup) parent);
        }

        rippleButton.forceSwitch(true);
        rippleButton.setButtonTitles(
                new String[] {
                        getContext().getString(R.string.my_follow).toUpperCase(),
                        getContext().getString(R.string.my_follow).toUpperCase()});

        if (!TextUtils.isEmpty(me.location)) {
            locationTxt.setText(me.location);
        } else {
            locationTxt.setText("Unknown");
        }

        if (!TextUtils.isEmpty(me.bio)) {
            bioTxt.setText(me.bio);
        } else {
            bioTxt.setVisibility(GONE);
        }

        loadPresenter.setNormalState();
    }

    @Nullable
    private ViewParent getAppBarParent() {
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof NestedScrollAppBarLayout)) {
            parent = parent.getParent();
        }
        return parent;
    }

    public void setLoading() {
        loadPresenter.setLoadingState();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
    }

    /** <br> interface. */

    // on switch swipeListener.

    @Override
    public void onSwitch(boolean switchTo) {
        if (AuthManager.getInstance().isAuthorized()) {
            IntentHelper.startMyFollowActivity(Mysplash.getInstance().getTopActivity());
        }
    }

    // view.

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
        // do nothing.
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
