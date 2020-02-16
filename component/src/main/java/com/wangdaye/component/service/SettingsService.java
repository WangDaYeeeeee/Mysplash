package com.wangdaye.component.service;

import android.app.Activity;

import androidx.annotation.StringDef;

public interface SettingsService {

    String BACK_TO_TOP_TYPE_ALL = "all";
    String BACK_TO_TOP_TYPE_ONLY_HOME = "home";
    String BACK_TO_TOP_TYPE_NONE = "none";
    @StringDef({BACK_TO_TOP_TYPE_ALL, BACK_TO_TOP_TYPE_ONLY_HOME, BACK_TO_TOP_TYPE_NONE})
    @interface BackToTopTypeRule {}

    String DAY_NIGHT_MODE_CLOSE = "close";
    String DAY_NIGHT_MODE_AUTO = "auto";
    String DAY_NIGHT_MODE_FOLLOW_SYSTEM = "follow_system";
    @StringDef({DAY_NIGHT_MODE_CLOSE, DAY_NIGHT_MODE_AUTO, DAY_NIGHT_MODE_FOLLOW_SYSTEM})
    @interface DayNightModeRule {}

    String DEFAULT_DAY_TIME = "06:00";
    String DEFAULT_NIGHT_TIME = "18:00";

    String LANGUAGE_SYSTEM = "follow_system";
    String LANGUAGE_ENGLISH_US = "english_usa";
    String LANGUAGE_ENGLISH_UK = "english_uk";
    String LANGUAGE_ENGLISH_AU = "english_au";
    String LANGUAGE_CHINESE = "chinese";
    String LANGUAGE_ITALIAN = "italian";
    String LANGUAGE_TURKISH = "turkish";
    String LANGUAGE_GERMAN = "german";
    String LANGUAGE_RUSSIAN = "russian";
    String LANGUAGE_SPANISH = "spanish";
    String LANGUAGE_JAPANESE = "japanese";
    String LANGUAGE_FRENCH = "french";
    String LANGUAGE_PORTUGUESE_BR = "portuguese_brazil";
    @StringDef({
            LANGUAGE_SYSTEM, LANGUAGE_ENGLISH_US, LANGUAGE_ENGLISH_UK, LANGUAGE_ENGLISH_AU,
            LANGUAGE_CHINESE, LANGUAGE_ITALIAN, LANGUAGE_TURKISH, LANGUAGE_GERMAN,
            LANGUAGE_RUSSIAN, LANGUAGE_SPANISH, LANGUAGE_JAPANESE, LANGUAGE_FRENCH,
            LANGUAGE_PORTUGUESE_BR
    }) @interface LanguageRule {}

    String PHOTOS_ORDER_LATEST = "latest";
    String PHOTOS_ORDER_OLDEST = "oldest";
    String PHOTOS_ORDER_POPULAR = "popular";
    String PHOTOS_ORDER_RANDOM = "random";
    @StringDef({
            PHOTOS_ORDER_LATEST, PHOTOS_ORDER_OLDEST, PHOTOS_ORDER_POPULAR, PHOTOS_ORDER_RANDOM
    }) @interface PhotosOrderRule {}

    String DOWNLOADER_MYSPLASH = DownloaderService.DOWNLOADER_MYSPLASH;
    String DOWNLOADER_SYSTEM = DownloaderService.DOWNLOADER_SYSTEM;
    @StringDef({DOWNLOADER_MYSPLASH, DOWNLOADER_SYSTEM})
    @interface DownloaderRule {}

    String DOWNLOAD_SCALE_TINY = "tiny";
    String DOWNLOAD_SCALE_COMPACT = "compact";
    String DOWNLOAD_SCALE_RAW = "raw";
    @StringDef({DOWNLOAD_SCALE_TINY, DOWNLOAD_SCALE_COMPACT, DOWNLOAD_SCALE_RAW})
    @interface DownloadeScaleRule {}

    String SATURATION_ANIM_DURATION_SHORT = "300";
    String SATURATION_ANIM_DURATION_MIDDLE = "1000";
    String SATURATION_ANIM_DURATION_NORMAL = "2000";
    @StringDef({
            SATURATION_ANIM_DURATION_SHORT,
            SATURATION_ANIM_DURATION_MIDDLE,
            SATURATION_ANIM_DURATION_NORMAL
    }) @interface SaturationAnimDurationRule {}

    @BackToTopTypeRule
    String getBackToTopType();

    boolean notifySetBackToTop(Activity activity);

    @DayNightModeRule
    String getAutoNightMode();

    boolean isSaturationAnimationEnabled();

    @SaturationAnimDurationRule
    String getSaturationAnimationDuration();

    @LanguageRule
    String getLanguage();

    @PhotosOrderRule
    String getDefaultPhotoOrder();

    @DownloaderRule
    String getDownloader();

    @DownloadeScaleRule
    String getDownloadScale();

    boolean isShowGridInPort();

    boolean isShowGridInLand();

    void startSettingsActivity(Activity a);
}
