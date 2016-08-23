package com.wangdaye.mysplash;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.data.User;
import com.wangdaye.mysplash._common.utils.LanguageUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * My application.
 * */

public class Mysplash extends Application {
    // data
    private List<Activity> activityList;
    private Photo photo;
    private Collection collection;
    private User user;
    private Drawable drawable;
    private boolean myOwnCollection;

    // Unsplash data.
    public static final String APPLICATION_ID = "72bf3302b0fb868d8822332a8dad712341c48a5bec5af94b7beea4d1cc030ee6";
    public static final String SECRET = "da8217d65b3a76ca3c94710a33287dbe2fee53892595917339b9dcef2eaf94e6";

    // Unsplash url.
    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String UNSPLASH_AUTH_BASE_URL = "https://unsplash.com/";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";
    public static final String UNSPLASH_LOGIN_URL = Mysplash.UNSPLASH_AUTH_BASE_URL + "oauth/authorize"
            + "?client_id=" + Mysplash.APPLICATION_ID
            + "&redirect_uri=" + "mysplash%3A%2F%2F" + UNSPLASH_LOGIN_CALLBACK
            + "&response_type=" + "code"
            + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes+read_collections+write_collections";

    // application data.
    public static final String AUTHOR_GITHUB = "https://github.com/WangDaYeeeeee";
    public static final String MYSPLASH_GITHUB = "https://github.com/WangDaYeeeeee/MySplash";

    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash/";
    public static final String DOWNLOAD_FORMAT = ".jpg";
    public static final int DEFAULT_PER_PAGE = 30;

    public static final int CATEGORY_TOTAL_NEW = 0;
    public static final int CATEGORY_TOTAL_FEATURED = 1;
    public static final int CATEGORY_BUILDINGS_ID = 2;
    public static final int CATEGORY_FOOD_DRINK_ID = 3;
    public static final int CATEGORY_NATURE_ID = 4;
    public static final int CATEGORY_OBJECTS_ID = 8;
    public static final int CATEGORY_PEOPLE_ID = 6;
    public static final int CATEGORY_TECHNOLOGY_ID = 7;

    public static int TOTAL_NEW_PHOTOS_COUNT = 14500;
    public static int TOTAL_FEATURED_PHOTOS_COUNT = 900;
    public static int BUILDING_PHOTOS_COUNT = 2720;
    public static int FOOD_DRINK_PHOTOS_COUNT = 650;
    public static int NATURE_PHOTOS_COUNT = 6910;
    public static int OBJECTS_PHOTOS_COUNT = 2150;
    public static int PEOPLE_PHOTOS_COUNT = 3410;
    public static int TECHNOLOGY_PHOTOS_COUNT = 350;

    // activity code.
    public static final int ME_ACTIVITY = 1;

    // permission code.
    public static final int WRITE_EXTERNAL_STORAGE = 1;

    /** <br> life cycle. */

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        readPhotoCount();
        loadLanguage();
    }

    private void initialize() {
        instance = this;
        activityList = new ArrayList<>();
    }

    private void loadLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        LanguageUtils.setLanguage(this, language);
    }

    private void readPhotoCount() {
        ValueUtils.readPhotoCount(this, CATEGORY_TOTAL_NEW);
        ValueUtils.readPhotoCount(this, CATEGORY_TOTAL_FEATURED);
        ValueUtils.readPhotoCount(this, CATEGORY_BUILDINGS_ID);
        ValueUtils.readPhotoCount(this, CATEGORY_FOOD_DRINK_ID);
        ValueUtils.readPhotoCount(this, CATEGORY_NATURE_ID);
        ValueUtils.readPhotoCount(this, CATEGORY_OBJECTS_ID);
        ValueUtils.readPhotoCount(this, CATEGORY_PEOPLE_ID);
        ValueUtils.readPhotoCount(this, CATEGORY_TECHNOLOGY_ID);
    }

    /** <br> data. */

    public void addActivity(Activity a) {
        activityList.add(a);
    }

    public void removeActivity() {
        activityList.remove(activityList.size() - 1);
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setPhoto(Photo p) {
        this.photo = p;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setCollection(Collection c) {
        this.collection = c;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setUser(User u) {
        this.user = u;
    }

    public User getUser() {
        return user;
    }

    public void setDrawable(Drawable d) {
        this.drawable = d;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setMyOwnCollection(boolean own) {
        this.myOwnCollection = own;
    }

    public boolean isMyOwnCollection() {
        return myOwnCollection;
    }

    /** <br> singleton. */

    private static Mysplash instance;

    public static Mysplash getInstance() {
        return instance;
    }
}
