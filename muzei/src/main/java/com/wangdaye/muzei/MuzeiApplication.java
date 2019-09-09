package com.wangdaye.muzei;

import android.content.Context;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.component.ComponentFactory;

public class MuzeiApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setMuzeiService(new MuzeiServiceIMP());
    }
}
