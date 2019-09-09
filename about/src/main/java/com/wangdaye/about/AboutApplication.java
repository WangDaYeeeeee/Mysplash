package com.wangdaye.about;

import android.content.Context;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.component.ComponentFactory;

public class AboutApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setAboutModule(new AboutModuleIMP());
    }
}
