package com.wangdaye.component.service;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import com.wangdaye.base.MuzeiWallpaperSource;
import com.wangdaye.base.unsplash.Collection;

public interface MuzeiService {

    String SOURCE_ALL = "all";
    String SOURCE_FEATURED = "featured";
    String SOURCE_COLLECTION = "collection";
    @StringDef({SOURCE_ALL, SOURCE_FEATURED, SOURCE_COLLECTION})
    @interface MuzeiSourceRule {}

    void startMuzeiSettingsActivity(Activity a);

    void startMuzeiCollectionSourceConfigActivity(Activity a);

    boolean isMuzeiInstalled(Context context);

    @MuzeiSourceRule
    String getSource(Context context);

    void setAsMuzeiSource(Context context, Collection collection);

    void removeFromMuzeiSource(Context context, Collection collection);

    @Nullable
    MuzeiWallpaperSource getMuzeiWallpaperSource(Context context, Collection collection);
}
