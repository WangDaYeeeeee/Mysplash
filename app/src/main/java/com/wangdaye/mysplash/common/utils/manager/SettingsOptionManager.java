package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;

/**
 * Settings option manager.
 *
 * A manager that is used to manage setting options.
 *
 * */

public class SettingsOptionManager {
    // data
    private String backToTopType;
    private boolean notifiedSetBackToTop;
    private int saturationAnimationDuration;
    private String language;
    private String defaultPhotoOrder;
    private String defaultCollectionType;
    private String downloadScale;

    /** <br> singleton. */

    private static SettingsOptionManager instance;

    public static SettingsOptionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsOptionManager.class) {
                if (instance == null) {
                    instance = new SettingsOptionManager(context);
                }
            }
        }
        return instance;
    }

    /** <br> life cycle. */

    private SettingsOptionManager(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.backToTopType = sharedPreferences.getString(
                context.getString(R.string.key_back_to_top),
                "all");
        this.notifiedSetBackToTop = sharedPreferences.getBoolean(
                context.getString(R.string.key_notified_set_back_to_top),
                false);
        this.saturationAnimationDuration = Integer.parseInt(
                sharedPreferences.getString(
                        context.getString(R.string.key_saturation_animation_duration), "2000"));
        this.language = sharedPreferences.getString(
                context.getString(R.string.key_language),
                "follow_system");
        this.defaultPhotoOrder = sharedPreferences.getString(
                context.getString(R.string.key_default_photo_order),
                "latest");
        this.defaultCollectionType = sharedPreferences.getString(
                context.getString(R.string.key_default_collection_type),
                "featured");
        this.downloadScale = sharedPreferences.getString(
                context.getString(R.string.key_download_scale),
                "compact");
    }

    /** <br> data. */

    public String getBackToTopType() {
        return backToTopType;
    }

    public void setBackToTopType(String backToTopType) {
        this.backToTopType = backToTopType;
    }

    public boolean isNotifiedSetBackToTop() {
        return notifiedSetBackToTop;
    }

    public void setNotifiedSetBackToTop(Context context, boolean notifiedSetBackToTop) {
        this.notifiedSetBackToTop = notifiedSetBackToTop;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.key_notified_set_back_to_top), notifiedSetBackToTop);
        editor.apply();
    }

    public int getSaturationAnimationDuration() {
        return saturationAnimationDuration;
    }

    public void setSaturationAnimationDuration(String duration) {
        this.saturationAnimationDuration = Integer.parseInt(duration);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultPhotoOrder() {
        return defaultPhotoOrder;
    }

    public void setDefaultPhotoOrder(String defaultPhotoOrder) {
        this.defaultPhotoOrder = defaultPhotoOrder;
    }

    public String getDefaultCollectionType() {
        return defaultCollectionType;
    }

    public void setDefaultCollectionType(String defaultCollectionType) {
        this.defaultCollectionType = defaultCollectionType;
    }

    public String getDownloadScale() {
        return downloadScale;
    }

    public void setDownloadScale(String downloadScale) {
        this.downloadScale = downloadScale;
    }
}
