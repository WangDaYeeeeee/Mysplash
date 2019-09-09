package com.wangdaye.settings;

import android.content.Context;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.component.ComponentFactory;

public class SettingsApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setSettingsService(SettingsServiceIMP.getInstance(context));
    }
}
