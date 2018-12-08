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

    private String backToTopType;
    private String autoNightMode;
    private boolean notifiedSetBackToTop;
    private String language;
    private String defaultPhotoOrder;
    private String downloader;
    private String downloadScale;
    private int saturationAnimationDuration;
    private boolean showGridInPort;
    private boolean showGridInLand;

    private SettingsOptionManager(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.backToTopType = sharedPreferences.getString(
                context.getString(R.string.key_back_to_top),
                "all");
        this.autoNightMode = sharedPreferences.getString(
                context.getString(R.string.key_auto_night_mode),
                "follow_system");
        this.notifiedSetBackToTop = sharedPreferences.getBoolean(
                context.getString(R.string.key_notified_set_back_to_top),
                false);
        this.language = sharedPreferences.getString(
                context.getString(R.string.key_language),
                "follow_system");
        this.defaultPhotoOrder = sharedPreferences.getString(
                context.getString(R.string.key_default_photo_order),
                "latest");
        this.downloader = sharedPreferences.getString(
                context.getString(R.string.key_downloader),
                "mysplash");
        this.downloadScale = sharedPreferences.getString(
                context.getString(R.string.key_download_scale),
                "compact");
        this.saturationAnimationDuration = Integer.parseInt(
                sharedPreferences.getString(
                        context.getString(R.string.key_saturation_animation_duration), "2000"));
        this.showGridInPort = sharedPreferences.getBoolean(
                context.getString(R.string.key_grid_list_in_port),
                true);
        this.showGridInLand = sharedPreferences.getBoolean(
                context.getString(R.string.key_grid_list_in_land),
                true);
    }

    public String getBackToTopType() {
        return backToTopType;
    }

    public void setBackToTopType(String backToTopType) {
        this.backToTopType = backToTopType;
    }

    public String getAutoNightMode() {
        return autoNightMode;
    }

    public void setAutoNightMode(String autoNightMode) {
        this.autoNightMode = autoNightMode;
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

    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }

    public String getDownloadScale() {
        return downloadScale;
    }

    public void setDownloadScale(String downloadScale) {
        this.downloadScale = downloadScale;
    }

    public boolean isShowGridInPort() {
        return showGridInPort;
    }

    public boolean isShowGridInLand() {
        return showGridInLand;
    }
}
