package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.wangdaye.mysplash.common.data.entity.table.WallpaperSource;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Muzei option manager.
 *
 * A manager that is used to manage muzei configurations.
 *
 * */

public class MuzeiOptionManager {

    public static MuzeiOptionManager getInstance(Context context) {
        return new MuzeiOptionManager(context);
    }

    private int updateInterval;
    private boolean updateOnlyInWifi;
    private List<WallpaperSource> sourceList;
    private long lastUpdateTime;

    private static final String PREFERENCE_NAME = "mysplash_muzei_options";
    private static final String KEY_UPDATE_INTERVAL_HOUR = "update_interval_hour";
    private static final String KEY_UPDATE_ONLY_IN_WIFI = "update_only_in_wifi";
    private static final String KEY_LAST_UPDATE_TIME = "last_update_time";

    public static final int DEFAULT_INTERVAL = 8;
    public static final int DEFAULT_COLLECTION_ID = 864380;

    private MuzeiOptionManager(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.updateInterval = sharedPreferences.getInt(KEY_UPDATE_INTERVAL_HOUR, DEFAULT_INTERVAL);
        this.updateOnlyInWifi = sharedPreferences.getBoolean(KEY_UPDATE_ONLY_IN_WIFI, false);
        setSourceList(DatabaseHelper.getInstance(context).readWallpaperSourceList());
        this.lastUpdateTime = sharedPreferences.getLong(KEY_LAST_UPDATE_TIME, -1);
    }

    public static void update(Context context, @Nullable MuzeiOptionManager manager,
                              int interval, boolean onlyInWifi, List<WallpaperSource> sourceList) {
        if (manager != null) {
            manager.setUpdateInterval(interval);
            manager.setUpdateOnlyInWifi(onlyInWifi);
            manager.setSourceList(sourceList);
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_UPDATE_INTERVAL_HOUR, interval);
        editor.putBoolean(KEY_UPDATE_ONLY_IN_WIFI, onlyInWifi);
        DatabaseHelper.getInstance(context).writeWallpaperSource(sourceList);
        editor.apply();
    }

    public static void writeUpdateTime(Context context, @Nullable MuzeiOptionManager manager) {
        long time = System.currentTimeMillis();
        if (manager != null) {
            manager.setLastUpdateTime(time);
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(KEY_LAST_UPDATE_TIME, time);
        editor.apply();
    }

    public static boolean isInstalledMuzei(Context c) {
        PackageInfo packageInfo;
        try {
            packageInfo = c.getPackageManager().getPackageInfo("net.nurik.roman.muzei", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    private void setUpdateInterval(int interval) {
        this.updateInterval = interval;
    }

    public boolean isUpdateOnlyInWifi() {
        return updateOnlyInWifi;
    }

    private void setUpdateOnlyInWifi(boolean onlyInWifi) {
        this.updateOnlyInWifi = onlyInWifi;
    }

    public List<WallpaperSource> getSourceList() {
        return sourceList;
    }

    private void setSourceList(List<WallpaperSource> list) {
        if (sourceList == null) {
            sourceList = new ArrayList<>();
        } else {
            sourceList.clear();
        }
        sourceList.addAll(list);
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    private void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
