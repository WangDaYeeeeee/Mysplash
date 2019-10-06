package com.wangdaye.muzei.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import androidx.annotation.StringDef;
import androidx.preference.PreferenceManager;

import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.MuzeiService;
import com.wangdaye.muzei.R;

import java.util.List;

/**
 * Muzei option manager.
 *
 * A manager that is used to manage muzei configurations.
 *
 * */

public class MuzeiOptionManager {

    private volatile static MuzeiOptionManager instance;

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

    private String source;
    public static final String SOURCE_ALL_PHOTOS = MuzeiService.SOURCE_ALL;
    public static final String SOURCE_FEATURED_PHOTOS = MuzeiService.SOURCE_FEATURED;
    public static final String SOURCE_COLLECTIONS = MuzeiService.SOURCE_COLLECTION;
    @StringDef({SOURCE_ALL_PHOTOS, SOURCE_FEATURED_PHOTOS, SOURCE_COLLECTIONS})
    public @interface MuzeiSourceRule {}

    private boolean screenSizeImage;

    private String cacheMode;
    public static final String CACHE_MODE_KEEP = "keep";
    public static final String CACHE_MODE_DELETE = "delete";
    @StringDef({CACHE_MODE_KEEP, CACHE_MODE_DELETE})
    public @interface MuzeiCacheMode {}

    private List<MuzeiWallpaperSource> collectionSourceList;

    private String query;

    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_UPDATE_ONLY_IN_WIFI = true;

    private MuzeiOptionManager(Context context) {
        this.updateInterval = DEFAULT_INTERVAL;
        this.updateOnlyInWifi = DEFAULT_UPDATE_ONLY_IN_WIFI;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        source = sharedPreferences.getString(
                context.getString(R.string.key_muzei_source), SOURCE_COLLECTIONS);

        screenSizeImage = sharedPreferences.getBoolean(
                context.getString(R.string.key_muzei_screen_size_image), false);

        cacheMode = sharedPreferences.getString(
                context.getString(R.string.key_muzei_cache_mode), CACHE_MODE_KEEP);

        collectionSourceList = ComponentFactory.getDatabaseService().readMuzeiWallpaperSourceList();

        query = sharedPreferences.getString(context.getString(R.string.key_muzei_query), "");
    }

    public static void updateCollectionSource(Context context, List<MuzeiWallpaperSource> sourceList) {
        ComponentFactory.getDatabaseService().writeMuzeiWallpaperSource(sourceList);
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

    @MuzeiSourceRule
    public String getSource() {
        return source;
    }

    public void setSource(@MuzeiSourceRule String source) {
        this.source = source;
    }

    @MuzeiCacheMode
    public String getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(@MuzeiCacheMode String cacheMode) {
        this.cacheMode = cacheMode;
    }

    public List<MuzeiWallpaperSource> getCollectionSourceList() {
        return collectionSourceList;
    }

    private void setCollectionSourceList(List<MuzeiWallpaperSource> list) {
        collectionSourceList = list;
    }

    public String getQuery() {
        return query;
    }

    private void setQuery(String q) {
        query = q;
    }
}
