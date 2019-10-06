package com.wangdaye.common.base.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public abstract class MultiModulesApplication extends MultiDexApplication {

    public abstract void initModuleComponent(Context context);
}
