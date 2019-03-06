package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.db.WallpaperSource;
import com.wangdaye.mysplash.common.db.DatabaseHelper;

import java.util.ArrayList;
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
    private String source;
    private String cacheMode;
    private List<WallpaperSource> collectionSourceList;

    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_UPDATE_ONLY_IN_WIFI = true;

    private MuzeiOptionManager(Context context) {
        this.updateInterval = DEFAULT_INTERVAL;
        this.updateOnlyInWifi = DEFAULT_UPDATE_ONLY_IN_WIFI;
        setSource(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.key_muzei_source), "collection"));
        setCacheMode(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.key_muzei_cache_mode), "keep"));
        setCollectionSourceList(DatabaseHelper.getInstance(context).readWallpaperSourceList());
    }

    public static void updateCollectionSource(Context context, List<WallpaperSource> sourceList) {
        DatabaseHelper.getInstance(context).writeWallpaperSource(sourceList);
        getInstance(context).setCollectionSourceList(sourceList);
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

    public boolean isUpdateOnlyInWifi() {
        return updateOnlyInWifi;
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
        if (collectionSourceList == null) {
            collectionSourceList = new ArrayList<>();
        } else {
            collectionSourceList.clear();
        }
        collectionSourceList.addAll(list);
    }
}
