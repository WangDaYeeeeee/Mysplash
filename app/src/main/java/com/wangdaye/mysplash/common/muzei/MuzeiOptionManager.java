package com.wangdaye.mysplash.common.muzei;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import androidx.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.db.DatabaseHelper;

import java.util.List;

/**
 * Muzei option manager.
 *
 * A manager that is used to manage muzei configurations.
 *
 * */

public class MuzeiOptionManager {

    private static MuzeiOptionManager instance;

    public static MuzeiOptionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MuzeiOptionManager.class) {
                if (instance == null) {
                    instance = new MuzeiOptionManager(context);
                }
            }
        }
        return instance;
    }

    private int updateInterval;
    private boolean updateOnlyInWifi;
    private boolean screenSizeImage;
    private String source;
    private String cacheMode;
    private List<WallpaperSource> collectionSourceList;
    private String query;

    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_UPDATE_ONLY_IN_WIFI = true;

    private MuzeiOptionManager(Context context) {
        this.updateInterval = DEFAULT_INTERVAL;
        this.updateOnlyInWifi = DEFAULT_UPDATE_ONLY_IN_WIFI;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setSource(
                sharedPreferences.getString(context.getString(R.string.key_muzei_source), "collection")
        );
        setScreenSizeImage(
                sharedPreferences.getBoolean(context.getString(R.string.key_muzei_screen_size_image), false)
        );
        setCacheMode(
                sharedPreferences.getString(context.getString(R.string.key_muzei_cache_mode), "keep")
        );
        setCollectionSourceList(
                DatabaseHelper.getInstance(context).readWallpaperSourceList()
        );
        setQuery(
                sharedPreferences.getString(context.getString(R.string.key_muzei_query), "")
        );
    }

    public static void updateCollectionSource(Context context, List<WallpaperSource> sourceList) {
        DatabaseHelper.getInstance(context).writeWallpaperSource(sourceList);
        getInstance(context).setCollectionSourceList(sourceList);
    }

    public static void updateQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.key_muzei_query), query)
                .apply();
        getInstance(context).setQuery(query);
    }

    public static boolean isInstalledMuzei(Context c) {
        PackageInfo packageInfo;
        try {
            packageInfo = c.getPackageManager().getPackageInfo("net.nurik.roman.muzei", 0);
        } catch (Exception e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public boolean isUpdateOnlyInWifi() {
        return updateOnlyInWifi;
    }

    public boolean isScreenSizeImage() {
        return screenSizeImage;
    }

    public void setScreenSizeImage(boolean screenSizeImage) {
        this.screenSizeImage = screenSizeImage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(String cacheMode) {
        this.cacheMode = cacheMode;
    }

    public List<WallpaperSource> getCollectionSourceList() {
        return collectionSourceList;
    }

    private void setCollectionSourceList(List<WallpaperSource> list) {
        collectionSourceList = list;
    }

    public String getQuery() {
        return query;
    }

    private void setQuery(String q) {
        query = q;
    }
}
