package com.wangdaye.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.module.MeModule;
import com.wangdaye.me.base.RoutingHelper;

public class MeApplication extends MultiModulesApplication {

    private class MeModuleIMP implements MeModule {

        @Override
        public void startMeActivity(Activity a, View avatar, View background,
                                    @ProfilePager.ProfilePagerRule int page) {
            RoutingHelper.startMeActivity(a, avatar, background, page);
        }

        @Override
        public Intent getMeActivityIntentForShortcut() {
            return RoutingHelper.getMeActivityIntent();
        }

        @Override
        public void startLoginActivity(Activity a) {
            RoutingHelper.startLoginActivity(a);
        }

        @Override
        public void startMyFollowActivity(Activity a) {
            RoutingHelper.startMyFollowActivity(a);
        }

        @Override
        public void startUpdateMeActivity(Activity a) {
            RoutingHelper.startUpdateMeActivity(a);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setMeModule(new MeModuleIMP());
    }
}
