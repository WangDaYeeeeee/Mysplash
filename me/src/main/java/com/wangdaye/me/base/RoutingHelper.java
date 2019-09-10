package com.wangdaye.me.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.common.ui.transition.sharedElement.RoundCornerTransition;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.R;
import com.wangdaye.me.activity.LoginActivity;
import com.wangdaye.me.activity.MeActivity;
import com.wangdaye.me.activity.MyFollowActivity;
import com.wangdaye.me.activity.UpdateMeActivity;

public class RoutingHelper extends com.wangdaye.common.utils.helper.RoutingHelper {

    public static void startMeActivity(Activity a, View avatar, View background,
                                       @ProfilePager.ProfilePagerRule int page) {
        if (!AuthManager.getInstance().isAuthorized()) {
            startLoginActivity(a);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle b = new Bundle();
            RoundCornerTransition.addExtraProperties(background, b);

            ARouter.getInstance()
                    .build(MeActivity.ME_ACTIVITY)
                    .withInt(MeActivity.KEY_ME_ACTIVITY_PAGE_POSITION, page)
                    .withBundle(a.getString(R.string.transition_me_background), b)
                    .withOptionsCompat(
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    a,
                                    Pair.create(avatar, a.getString(R.string.transition_me_avatar)),
                                    Pair.create(background, a.getString(R.string.transition_me_background))
                            )
                    ).navigation(a);
        } else {
            ARouter.getInstance()
                    .build(MeActivity.ME_ACTIVITY)
                    .withInt(MeActivity.KEY_ME_ACTIVITY_PAGE_POSITION, page)
                    .withTransition(R.anim.activity_slide_in, R.anim.none)
                    .navigation(a);
        }
    }

    public static Intent getMeActivityIntent() {
        return new Intent(MeActivity.ACTION_ME_ACTIVITY)
                .putExtra(MeActivity.KEY_ME_ACTIVITY_BROWSABLE, true);
    }

    public static void startLoginActivity(Activity a) {
        ARouter.getInstance()
                .build(LoginActivity.LOGIN_ACTIVITY)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startMyFollowActivity(Activity a) {
        if (!AuthManager.getInstance().isAuthorized()) {
            startLoginActivity(a);
        } else {
            ARouter.getInstance()
                    .build(MyFollowActivity.MY_FOLLOW_ACTIVITY)
                    .withTransition(R.anim.activity_slide_in, R.anim.none)
                    .navigation(a);
        }
    }

    public static void startUpdateMeActivity(Activity a) {
        ARouter.getInstance()
                .build(UpdateMeActivity.UPDATE_ME_ACTIVITY)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
