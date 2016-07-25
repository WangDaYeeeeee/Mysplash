package com.wangdaye.mysplash.data.constant;

import android.os.Environment;

/**
 * Mysplash.
 * */

public class Mysplash {
    // Unsplash data.
    public static final String APPLICATION_ID = "72bf3302b0fb868d8822332a8dad712341c48a5bec5af94b7beea4d1cc030ee6";
    public static final String SECRET = "da8217d65b3a76ca3c94710a33287dbe2fee53892595917339b9dcef2eaf94e6";

    // Unsplash url.
    public static final String BASE_URL = "https://api.unsplash.com/";
    public static final String UNSPLASH_URL = "https://unsplash.com/";

    // application data.
    public static final String EMAIL = "wangdayeeeeee@gmail.com";
    public static final String AUTHOR_GITHUB = "https://github.com/WangDaYeeeeee";
    public static final String MYSPLASH_GITHUB = "https://github.com/WangDaYeeeeee/MySplash";

    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash/";
    public static final String DOWNLOAD_FORMAT = ".jpg";

    public static final int CATEGORY_BUILDINGS_ID = 2;
    public static final int CATEGORY_FOOD_DRINK_ID = 3;
    public static final int CATEGORY_NATURE_ID = 4;
    public static final int CATEGORY_OBJECTS_ID = 8;
    public static final int CATEGORY_PEOPLE_ID = 6;
    public static final int CATEGORY_TECHNOLOGY_ID = 7;

    public static final int TOTAL_NEW_PHOTOS_COUNT = 14500;
    public static final int TOTAL_FEATURED_PHOTOS_COUNT = 900;
    public static final int BUILDING_PHOTOS_COUNT = 2720;
    public static final int FOOD_DRINK_PHOTOS_COUNT = 650;
    public static final int NATURE_PHOTOS_COUNT = 6910;
    public static final int OBJECTS_PHOTOS_COUNT = 2150;
    public static final int PEOPLE_PHOTOS_COUNT = 3410;
    public static final int TECHNOLOGY_PHOTOS_COUNT = 350;
}
