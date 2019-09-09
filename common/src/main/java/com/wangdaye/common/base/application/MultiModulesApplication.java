package com.wangdaye.common.base.application;

import android.app.Application;
import android.content.Context;

public abstract class MultiModulesApplication extends Application {

    public abstract void initModuleComponent(Context context);
}
