package com.wangdaye.common.base.application;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.bugly.crashreport.CrashReport;
import com.wangdaye.common.base.activity.LoadableActivity;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.Photo;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash application.
 * */

public abstract class MysplashApplication extends MultiModulesApplication {

    private static MysplashApplication instance;
    public static MysplashApplication getInstance() {
        return instance;
    }

    private List<MysplashActivity> activityList;
    private Rect windowInsets;
    @Nullable private Bundle sharedElementTransitionExtraProperties;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        activityList = new ArrayList<>();
        windowInsets = new Rect(0, 0, 0, 0);

        if (isDebug(this)) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

        CrashReport.initCrashReport(getApplicationContext(), "c8ad99bd5d", false);
    }

    public static boolean isDebug(Context c) {
        try {
            return (c.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {

        }
        return false;
    }

    public void addActivity(@NonNull MysplashActivity a) {
        activityList.add(a);
    }

    public void removeActivity(MysplashActivity a) {
        activityList.remove(a);
    }

    @Nullable
    public MysplashActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    @Nullable
    public MysplashActivity getSecondFloorActivity() {
        if (activityList != null && activityList.size() > 1) {
            return activityList.get(activityList.size() - 2);
        } else {
            return null;
        }
    }

    public int getActivityCount() {
        if (activityList != null) {
            return activityList.size();
        } else {
            return 0;
        }
    }

    public List<Photo> loadMorePhotos(MysplashActivity activity,
                                      List<Photo> list, int headIndex, boolean headDirection) {
        int index = activityList.indexOf(activity) - 1;
        if (index > -1) {
            Activity a = activityList.get(index);
            if (a instanceof LoadableActivity) {
                try {
                    if (((ParameterizedType) a.getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0]
                            .toString()
                            .equals(Photo.class.toString())) {
                        return ((LoadableActivity<Photo>) a).loadMoreData(list, headIndex, headDirection);
                    }
                } catch (Exception ignored) {
                    // do nothing.
                }
            }
        }
        return new ArrayList<>();
    }

    public void dispatchRecreate() {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).recreate();
        }
    }

    public void setWindowInsets(int left, int top, int right, int bottom) {
        if (left != windowInsets.left || top != windowInsets.top
                || right != windowInsets.right || bottom != windowInsets.bottom) {
            windowInsets.set(left, top, right, bottom);
        }
    }

    public Rect getWindowInsets() {
        return windowInsets;
    }

    @Nullable
    public Bundle getSharedElementTransitionExtraProperties() {
        return sharedElementTransitionExtraProperties;
    }

    public void setSharedElementTransitionExtraProperties(@Nullable Bundle b) {
        sharedElementTransitionExtraProperties = b;
    }
}
