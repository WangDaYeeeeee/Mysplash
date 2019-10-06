package com.wangdaye.db;

import android.content.Context;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.component.ComponentFactory;

public class DatabaseApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setDatabaseService(DatabaseServiceIMP.getInstance(context));
    }
}
