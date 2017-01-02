package com.wangdaye.mysplash;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * My application.
 * */

public class Mysplash extends Application {
    // data
    private List<MysplashActivity> activityList;

    private boolean lightTheme;
    private String language;
    private String defaultPhotoOrder;
    private String defaultCollectionType;
    private String downloadScale;
    private String backToTopType;
    private boolean notifiedSetBackToTop;

    // Unsplash data.
    public static final String APPLICATION_ID = "7a96a77d719e9967f935da53784d6a3eb58a4fb174dda25e89ec69059e46c815";
    public static final String SECRET = "dd766f4ee6e01599ca6db2e97c306a883a024f7322f92d4f7ab4aeae3be7924e";

    // Unsplash url.
    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";
    public static final String UNSPLASH_LOGIN_URL = Mysplash.UNSPLASH_URL + "oauth/authorize"
            + "?client_id=" + Mysplash.APPLICATION_ID
            + "&redirect_uri=" + "mysplash%3A%2F%2F" + UNSPLASH_LOGIN_CALLBACK
            + "&response_type=" + "code"
            + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes+read_collections+write_collections";

    // application data.
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DOWNLOAD_PATH = "/Pictures/Mysplash/";
    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_COLLECTION_FORMAT = ".zip";

    public static final int DEFAULT_PER_PAGE = 15;

    public static final int CATEGORY_TOTAL_NEW = 0;
    public static final int CATEGORY_TOTAL_FEATURED = 1;
    public static final int CATEGORY_BUILDINGS_ID = 2;
    public static final int CATEGORY_FOOD_DRINK_ID = 3;
    public static final int CATEGORY_NATURE_ID = 4;
    public static final int CATEGORY_OBJECTS_ID = 8;
    public static final int CATEGORY_PEOPLE_ID = 6;
    public static final int CATEGORY_TECHNOLOGY_ID = 7;

    public static int TOTAL_NEW_PHOTOS_COUNT = 17444;
    public static int TOTAL_FEATURED_PHOTOS_COUNT = 1192;
    public static int BUILDING_PHOTOS_COUNT = 2720;
    public static int FOOD_DRINK_PHOTOS_COUNT = 650;
    public static int NATURE_PHOTOS_COUNT = 54208;
    public static int OBJECTS_PHOTOS_COUNT = 2150;
    public static int PEOPLE_PHOTOS_COUNT = 3410;
    public static int TECHNOLOGY_PHOTOS_COUNT = 350;

    // preference.
    public static final String PREFERENCE_THEME = "theme";
    public static final String PREFERENCE_BACK_TO_TOP = "back_to_top";

    // activity code.
    public static final int ME_ACTIVITY = 1;

    // permission code.
    public static final int WRITE_EXTERNAL_STORAGE = 1;

    /** <br> life cycle. */

    @Override
    public void onCreate() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences themePreferences = getSharedPreferences(PREFERENCE_THEME, MODE_PRIVATE);
        SharedPreferences backToTopPreferences = getSharedPreferences(PREFERENCE_BACK_TO_TOP, MODE_PRIVATE);
        super.onCreate();

        instance = this;
        activityList = new ArrayList<>();

        lightTheme = themePreferences.getBoolean(getString(R.string.key_light_theme), true);
        language = defaultSharedPreferences.getString(getString(R.string.key_language), "follow_system");
        defaultPhotoOrder = defaultSharedPreferences.getString(getString(R.string.key_default_photo_order), "latest");
        defaultCollectionType = defaultSharedPreferences.getString(getString(R.string.key_default_collection_type), "featured");
        downloadScale = defaultSharedPreferences.getString(getString(R.string.key_download_scale), "compact");

        backToTopType = backToTopPreferences.getString(getString(R.string.key_back_to_top), "all");
        notifiedSetBackToTop = backToTopPreferences.getBoolean(getString(R.string.key_notified_set_back_to_top), false);
    }

    /** <br> data. */

    public void addActivity(MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(a);
    }

    public void removeActivity(MysplashActivity a) {
        activityList.remove(a);
    }

    public MysplashActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    public MainActivity getMainActivity() {
        if (activityList != null && activityList.size() > 0
                && activityList.get(0) instanceof MainActivity) {
            return (MainActivity) activityList.get(0);
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

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void changeTheme() {
        this.lightTheme = !lightTheme;
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

    public void setDefaultPhotoOrder(String order) {
        this.defaultPhotoOrder = order;
    }

    public String getDefaultCollectionType() {
        return defaultCollectionType;
    }

    public void setDefaultCollectionType(String type) {
        this.defaultCollectionType = type;
    }

    public String getDownloadScale() {
        return downloadScale;
    }

    public void setDownloadScale(String scale) {
        this.downloadScale = scale;
    }

    public String getBackToTopType() {
        return backToTopType;
    }

    public void setBackToTopType(String type) {
        this.backToTopType = type;
    }

    public boolean isNotifiedSetBackToTop() {
        return notifiedSetBackToTop;
    }

    public void setNotifiedSetBackToTop() {
        this.notifiedSetBackToTop = true;
    }

    /** <br> singleton. */

    private static Mysplash instance;

    public static Mysplash getInstance() {
        return instance;
    }
}
