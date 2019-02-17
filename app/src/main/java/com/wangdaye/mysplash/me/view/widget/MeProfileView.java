package com.wangdaye.mysplash.me.view.widget;

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
import com.wangdaye.mysplash.common.data.entity.unsplash.Tag;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.ui.adapter.MiniTagAdapter;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.model.LoadModel;
import com.wangdaye.mysplash.common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash.common.i.view.LoadView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.me.model.widget.LoadObject;
import com.wangdaye.mysplash.me.presenter.widget.LoadImplementor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Me profile view.
 *
 * This view is used to show application's profile.
 *
 * */

public class MeProfileView  extends FrameLayout
        implements LoadView,
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

    private List<Tag> tags;

    private LoadModel loadModel;
    private LoadPresenter loadPresenter;

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
        this.loadModel = new LoadObject(LoadModel.LOADING_STATE);
    }

    private void initPresenter() {
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    private void initView() {
        progressView.setVisibility(VISIBLE);
        profileContainer.setVisibility(GONE);

        tagList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rippleButton.setDontAnimate(true);
        rippleButton.setOnSwitchListener(this);
    }

    // control.

    @SuppressLint("SetTextI18n")
    public void drawMeProfile(User u) {
        ViewParent parent = getAppBarParent();
        if (parent instanceof NestedScrollAppBarLayout) {
            TransitionManager.beginDelayedTransition((ViewGroup) parent);
        }

        if (tags == null
                || (u.tags != null && !isSameTags(u.tags.custom, tags))) {
            if (u.tags == null || u.tags.custom == null || u.tags.custom.size() == 0) {
                tagList.setVisibility(GONE);
            } else {
                tags = u.tags.custom;
                tagList.setAdapter(new MiniTagAdapter(u.tags.custom));
            }
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

        rippleButton.forceSwitch(true);
        rippleButton.setButtonTitles(
                new String[] {
                        getContext().getString(R.string.my_follow).toUpperCase(),
                        getContext().getString(R.string.my_follow).toUpperCase()});

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

    private boolean isSameTags(List<Tag> a, List<Tag> b) {
        if ((a == null || a.size() == 0) && (b == null || b.size() == 0)) {
            return true;
        } else if (a != null && a.size() != 0 && b != null && b.size() != 0 && a.size() == b.size()) {
            for (int i = 0; i < a.size(); i ++) {
                if (!TextUtils.equals(a.get(i).getTitle(), b.get(i).getTitle())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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

    // on switch listener.

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
