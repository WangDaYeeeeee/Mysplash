package com.wangdaye.mysplash;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.activity.LoadableActivity;
import com.wangdaye.mysplash.common.di.component.DaggerApplicationComponent;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.manager.CustomApiManager;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo3.ui.PhotoActivity3;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Mysplash.
 *
 * Application class for Mysplash.
 *
 * */

public class Mysplash extends Application
        implements HasActivityInjector, HasSupportFragmentInjector {

    private static Mysplash instance;
    public static Mysplash getInstance() {
        return instance;
    }

    @Inject DispatchingAndroidInjector<Activity> activityInjector;
    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject OkHttpClient httpClient;
    @Inject GsonConverterFactory gsonConverterFactory;

    private List<MysplashActivity> activityList;

    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String STREAM_API_BASE_URL = "https://api.getstream.io/";
    public static final String UNSPLASH_TREND_FEEDING_URL = "feeds/home";
    public static final String UNSPLASH_FOLLOWING_FEED_URL = "feeds/following";
    public static final String UNSPLASH_NODE_API_URL = "";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_SUBMIT_URL = "https://unsplash.com/submit";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";

    public static final String DOWNLOAD_PATH = "/Pictures/Mysplash/";
    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_COLLECTION_FORMAT = ".zip";

    @StringDef({DOWNLOAD_PHOTO_FORMAT, DOWNLOAD_COLLECTION_FORMAT})
    public @interface DownloadFormatRule {}

    public static final int DEFAULT_PER_PAGE = 10;
    @IntRange(from = 1, to = 30)
    public @interface PerPageRule {}

    @IntRange(from = 1)
    public @interface PageRule {}

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

    public static final int CUSTOM_API_ACTIVITY = 1;

    public enum MessageType {
        CREATE, DELETE, UPDATE
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        DaggerApplicationComponent.create().inject(this);

        activityList = new ArrayList<>();

        DownloadHelper.getInstance(this);

        if (SettingsOptionManager.getInstance(this).getAutoNightMode().equals("follow_system")) {
            ThemeManager.getInstance(this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    ThemeManager.getInstance(this).isLightTheme()
                            ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public GsonConverterFactory getGsonConverterFactory() {
        return gsonConverterFactory;
    }

    public static String getAppId(Context c, boolean auth) {
        if (isDebug(c)) {
            return BuildConfig.APP_ID_BETA;
        } else if (TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiKey())
                || TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiSecret())) {
            if (auth) {
                return BuildConfig.APP_ID_RELEASE;
            } else {
                return BuildConfig.APP_ID_RELEASE_UNAUTH;
            }
        } else {
            return CustomApiManager.getInstance(c).getCustomApiKey();
        }
    }

    public static String getSecret(Context c) {
        if (isDebug(c)) {
            return BuildConfig.SECRET_BETA;
        } else if (TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiKey())
                || TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiSecret())) {
            return BuildConfig.SECRET_RELEASE;
        } else {
            return CustomApiManager.getInstance(c).getCustomApiSecret();
        }
    }

    public static boolean isDebug(Context c) {
        try {
            return (c.getApplicationInfo().flags
                    & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {

        }
        return false;
    }

    public static boolean hasNode() {
        return !TextUtils.isEmpty(UNSPLASH_NODE_API_URL);
    }

    public static String getLoginUrl(Context c) {
        return Mysplash.UNSPLASH_URL + "oauth/authorize"
                + "?client_id=" + getAppId(c, true)
                + "&redirect_uri=" + "mysplash%3A%2F%2F" + UNSPLASH_LOGIN_CALLBACK
                + "&response_type=" + "code"
                + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes+write_followers+read_collections+write_collections";
    }

    public void addActivity(@NonNull MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(a);
    }

    public void addActivityToFirstPosition(@NonNull MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(0, a);
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

    public int getActivityCount() {
        if (activityList != null) {
            return activityList.size();
        } else {
            return 0;
        }
    }

    public List<Photo> loadMorePhotos(PhotoActivity3 activity,
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
                        return ((LoadableActivity) a).loadMoreData(list, headIndex, headDirection);
                    }
                } catch (Exception ignored) {
                    // do nothing.
                }
            }
        }
        return new ArrayList<>();
    }

    public void dispatchPhotoUpdate(@NonNull Photo photo, MessageType type) {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).updatePhoto(photo, type);
        }
    }

    public void dispatchCollectionUpdate(@NonNull Collection collection, MessageType type) {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).updateCollection(collection, type);
        }
    }

    public void dispatchUserUpdate(@NonNull User user, MessageType type) {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).updateUser(user, type);
        }
    }

    public void finishSameActivity(Class c) {
        for (int i = 0; i < activityList.size() - 3; i ++) {
            if (c.isInstance(activityList.get(i))) {
                activityList.get(i).finish();
            }
        }
    }

    public void dispatchRecreate() {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).recreate();
        }
    }

    // interface.

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
