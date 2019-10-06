package com.wangdaye.user.ui;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.adapter.tag.MiniTagAdapter;
import com.wangdaye.common.ui.adapter.PagerAdapter;
import com.wangdaye.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.user.R;
import com.wangdaye.user.R2;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User profile view.
 *
 * This view is used to show user's profile.
 *
 * */

public class UserProfileView extends FrameLayout {

    @BindView(R2.id.container_user_profile_progressView) CircularProgressView progressView;
    @BindView(R2.id.container_user_profile_profileContainer) LinearLayout profileContainer;

    @BindView(R2.id.container_user_profile_tagList) RecyclerView tagList;
    @BindView(R2.id.container_user_profile_bio) TextView bioTxt;

    @BindView(R2.id.container_user_profile_locationTxt) TextView locationTxt;
    @OnClick(R2.id.container_user_profile_locationTxt) void clickLocation() {
        if (!TextUtils.isEmpty(locationTxt.getText())) {
                ComponentFactory.getSearchModule().startSearchActivity(
                    MysplashApplication.getInstance().getTopActivity(),
                    locationTxt.getText().toString());
        }
    }

    @BindView(R2.id.container_user_profile_followBtn) RippleButton rippleButton;

    private PagerAdapter adapter;

    private OnRippleButtonSwitchedListener listener;

    @ProfileViewStateRule private int state;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_LOADING = -1;
    @IntDef({STATE_NORMAL, STATE_LOADING})
    private @interface ProfileViewStateRule {}

    public UserProfileView(Context context) {
        super(context);
        this.init();
    }

    public UserProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    // init.

    @SuppressLint("InflateParams")
    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_user_profile, null);
        addView(v);
        ButterKnife.bind(this, this);

        state = STATE_LOADING;

        progressView.setVisibility(VISIBLE);
        profileContainer.setVisibility(GONE);

        tagList.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        if (AuthManager.getInstance().isAuthorized() && UrlCollection.hasNode()) {
            rippleButton.setOnSwitchListener(current -> {
                if (listener != null) {
                    listener.onRippleButtonSwitched(current != RippleButton.State.ON);
                    rippleButton.setState(current == RippleButton.State.ON
                            ? RippleButton.State.TRANSFORM_TO_OFF : RippleButton.State.TRANSFORM_TO_ON);
                }
            });
        } else {
            rippleButton.setVisibility(GONE);
        }
    }

    // control.

    public void setAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
    }

    public void setRippleButtonState(User user) {
        if (user.settingFollow) {
            rippleButton.setState(user.followed_by_user
                    ? RippleButton.State.TRANSFORM_TO_OFF : RippleButton.State.TRANSFORM_TO_ON);
        } else {
            rippleButton.setState(user.followed_by_user
                    ? RippleButton.State.ON : RippleButton.State.OFF);
        }
    }

    @SuppressLint("SetTextI18n")
    public void drawUserInfo(MysplashActivity activity, User u) {
        if (u.tags == null || u.tags.custom == null || u.tags.custom.size() == 0) {
            tagList.setVisibility(GONE);
        } else {
            tagList.setAdapter(new MiniTagAdapter(
                    u.tags.custom,
                    (view, tag) -> ComponentFactory.getSearchModule().startSearchActivity(activity, view, tag)
            ));
        }

        if (!TextUtils.isEmpty(u.bio)) {
            bioTxt.setText(u.bio);
        } else {
            bioTxt.setVisibility(GONE);
        }

        if (TextUtils.isEmpty(u.location)) {
            locationTxt.setVisibility(GONE);
        } else {
            locationTxt.setText(u.location);
        }

        setRippleButtonState(u);

        adapter.titleList = new ArrayList<>(
                Arrays.asList(
                        DisplayUtils.abridgeNumber(u.total_photos)
                                + " " + getResources().getStringArray(R.array.user_tabs)[0],
                        DisplayUtils.abridgeNumber(u.total_likes)
                                + " " + getResources().getStringArray(R.array.user_tabs)[1],
                        DisplayUtils.abridgeNumber(u.total_collections)
                                + " " + getResources().getStringArray(R.array.user_tabs)[2]
                )
        );
        adapter.notifyDataSetChanged();

        setState(STATE_NORMAL);
    }

    @ProfileViewStateRule
    public int getState() {
        return state;
    }

    public void setState(@ProfileViewStateRule int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case STATE_LOADING:
                    AnimUtils.animShow(progressView);
                    AnimUtils.animHide(profileContainer);
                    break;

                case STATE_NORMAL:
                    AnimUtils.animShow(profileContainer);
                    AnimUtils.animHide(progressView);
                    break;
            }
        }
    }

    // interface.

    // on request user listener.

    public interface OnRippleButtonSwitchedListener {
        void onRippleButtonSwitched(boolean switchTo);
    }

    public void setOnRippleButtonSwitchedListener(OnRippleButtonSwitchedListener l) {
        this.listener = l;
    }
}
