package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.data.PhotoDetails;

import java.util.List;

import retrofit2.Response;

/**
 * Value utils.
 * */

public class ValueUtils {

    public static String getOrderName(Context c, String key) {
        switch (key) {
            case PhotoApi.ORDER_BY_LATEST:
                return c.getResources().getStringArray(R.array.photo_orders)[0];

            case PhotoApi.ORDER_BY_OLDEST:
                return c.getResources().getStringArray(R.array.photo_orders)[1];

            case PhotoApi.ORDER_BY_POPULAR:
                return c.getResources().getStringArray(R.array.photo_orders)[2];

            case "random":
                return c.getResources().getStringArray(R.array.photo_orders)[3];

            default:
                return null;
        }
    }

    public static String getCollectionName(Context c, String key) {
        switch (key) {
            case "all":
                return c.getResources().getStringArray(R.array.collection_types)[0];

            case "curated":
                return c.getResources().getStringArray(R.array.collection_types)[1];

            case "featured":
                return c.getResources().getStringArray(R.array.collection_types)[2];

            default:
                return null;
        }
    }

    public static String getScaleName(Context c, String key) {
        switch (key) {
            case "compact":
                return c.getResources().getStringArray(R.array.download_types)[0];

            case "raw":
                return c.getResources().getStringArray(R.array.download_types)[1];

            default:
                return null;
        }
    }

    public static String getBackToTopName(Context c, String key) {
        switch (key) {
            case "all":
                return c.getResources().getStringArray(R.array.back_to_top_type)[0];

            case "home":
                return c.getResources().getStringArray(R.array.back_to_top_type)[1];

            case "none":
                return c.getResources().getStringArray(R.array.back_to_top_type)[2];

            default:
                return null;
        }
    }

    public static String getLanguageName(Context c, String key) {
        switch (key) {
            case "follow_system":
                return c.getResources().getStringArray(R.array.languages)[0];

            case "english":
                return c.getResources().getStringArray(R.array.languages)[1];

            case "chinese":
                return c.getResources().getStringArray(R.array.languages)[2];

            default:
                return null;
        }
    }

    public static String getOrientationName(Context c, String key) {
        switch (key) {
            case PhotoApi.LANDSCAPE_ORIENTATION:
                return c.getResources().getStringArray(R.array.search_orientations)[0];

            case PhotoApi.PORTRAIT_ORIENTATION:
                return c.getResources().getStringArray(R.array.search_orientations)[1];

            case PhotoApi.SQUARE_ORIENTATION:
                return c.getResources().getStringArray(R.array.search_orientations)[2];

            default:
                return null;
        }
    }

    public static String getToolbarTitleByCategory(Context context, int id) {
        switch (id) {
            case Mysplash.CATEGORY_BUILDINGS_ID:
                return context.getString(R.string.action_category_buildings);

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return context.getString(R.string.action_category_food_drink);

            case Mysplash.CATEGORY_NATURE_ID:
                return context.getString(R.string.action_category_nature);

            case Mysplash.CATEGORY_OBJECTS_ID:
                return context.getString(R.string.action_category_objects);

            case Mysplash.CATEGORY_PEOPLE_ID:
                return context.getString(R.string.action_category_people);

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return context.getString(R.string.action_category_technology);

            default:
                return context.getString(R.string.app_name);
        }
    }

    public static List<Integer> getPageListByCategory(int id) {
        switch (id) {
            case Mysplash.CATEGORY_TOTAL_NEW:
                return MathUtils.getPageList(Mysplash.TOTAL_NEW_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TOTAL_FEATURED:
                return MathUtils.getPageList(Mysplash.TOTAL_FEATURED_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_BUILDINGS_ID:
                return MathUtils.getPageList(Mysplash.BUILDING_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return MathUtils.getPageList(Mysplash.FOOD_DRINK_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_NATURE_ID:
                return MathUtils.getPageList(Mysplash.NATURE_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_OBJECTS_ID:
                return MathUtils.getPageList(Mysplash.OBJECTS_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_PEOPLE_ID:
                return MathUtils.getPageList(Mysplash.PEOPLE_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return MathUtils.getPageList(Mysplash.TECHNOLOGY_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            default:
                return MathUtils.getPageList(0);
        }
    }

    public static void writePhotoCount(Context c, Response<List<Photo>> response, int category) {
        String value = response.headers().get("X-Total");
        if (!TextUtils.isEmpty(value)) {
            int count = Integer.parseInt(value);
            writePhotoCount(c, category, count);
        }
    }

    public static void writePhotoCount(Context c, PhotoDetails details) {
        for (int i = 0; i < details.categories.size(); i ++) {
            writePhotoCount(
                    c,
                    details.categories.get(i).id,
                    details.categories.get(i).photo_count);
        }
    }

    private static void writePhotoCount(Context c, int category, int count) {
        if (count == 0) {
            return;
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        switch (category) {
            case Mysplash.CATEGORY_TOTAL_NEW:
                editor.putInt(
                        c.getString(R.string.key_category_total_new_count),
                        count);
                break;

            case Mysplash.CATEGORY_TOTAL_FEATURED:
                editor.putInt(
                        c.getString(R.string.key_category_total_feature_count),
                        count);
                break;

            case Mysplash.CATEGORY_BUILDINGS_ID:
                editor.putInt(
                        c.getString(R.string.key_category_buildings_count),
                        count);
                break;

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                editor.putInt(
                        c.getString(R.string.key_category_food_drink_count),
                        count);
                break;

            case Mysplash.CATEGORY_NATURE_ID:
                editor.putInt(
                        c.getString(R.string.key_category_nature_count),
                        count);
                break;

            case Mysplash.CATEGORY_OBJECTS_ID:
                editor.putInt(
                        c.getString(R.string.key_category_objects_count),
                        count);
                break;

            case Mysplash.CATEGORY_PEOPLE_ID:
                editor.putInt(
                        c.getString(R.string.key_category_people_count),
                        count);
                break;

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                editor.putInt(
                        c.getString(R.string.key_category_technology_count),
                        count);
                break;
        }
        editor.apply();
    }

    public static void readPhotoCount(Context c, int category) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        switch (category) {
            case Mysplash.CATEGORY_TOTAL_NEW:
                Mysplash.TOTAL_NEW_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_total_new_count),
                        Mysplash.TOTAL_NEW_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_TOTAL_FEATURED:
                Mysplash.TOTAL_FEATURED_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_total_feature_count),
                        Mysplash.TOTAL_FEATURED_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_BUILDINGS_ID:
                Mysplash.BUILDING_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_buildings_count),
                        Mysplash.BUILDING_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                Mysplash.FOOD_DRINK_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_food_drink_count),
                        Mysplash.FOOD_DRINK_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_NATURE_ID:
                Mysplash.NATURE_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_nature_count),
                        Mysplash.NATURE_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_OBJECTS_ID:
                Mysplash.OBJECTS_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_objects_count),
                        Mysplash.OBJECTS_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_PEOPLE_ID:
                Mysplash.PEOPLE_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_people_count),
                        Mysplash.PEOPLE_PHOTOS_COUNT);
                break;

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                Mysplash.TECHNOLOGY_PHOTOS_COUNT = sharedPreferences.getInt(
                        c.getString(R.string.key_category_technology_count),
                        Mysplash.TECHNOLOGY_PHOTOS_COUNT);
                break;
        }
    }
}
