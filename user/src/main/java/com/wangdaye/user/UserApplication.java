package com.wangdaye.user;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.module.UserModule;
import com.wangdaye.user.base.RoutingHelper;

public class UserApplication extends MultiModulesApplication {

    private class UserModuleIMP implements UserModule {

        @Override
        public void startUserActivity(Activity a,
                                      View avatar, View background, User u, int page) {
            RoutingHelper.startUserActivity(a, avatar, background, u, page);
        }

        @Override
        public void startUserActivity(Activity a, String username) {
            RoutingHelper.startUserActivity(a, username);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setUserModule(new UserModuleIMP());
    }
}
