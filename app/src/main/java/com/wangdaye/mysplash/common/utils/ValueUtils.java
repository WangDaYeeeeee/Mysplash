package com.wangdaye.mysplash.common.utils;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;

import java.util.List;

/**
 * Value utils.
 * */

public class ValueUtils {

    public static String getOrderName(Context c, String key) {
        switch (key) {
            case PhotoApi.ORDER_BY_LATEST:
                return c.getResources().getStringArray(R.array.orders)[0];

            case PhotoApi.ORDER_BY_OLDEST:
                return c.getResources().getStringArray(R.array.orders)[1];

            case PhotoApi.ORDER_BY_POPULAR:
                return c.getResources().getStringArray(R.array.orders)[2];

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
                return c.getString(R.string.action_orientation_landscape);

            case PhotoApi.PORTRAIT_ORIENTATION:
                return c.getString(R.string.action_orientation_portrait);

            case PhotoApi.SQUARE_ORIENTATION:
                return c.getString(R.string.action_orientation_square);

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
            case Mysplash.CATEGORY_BUILDINGS_ID:
                return MathUtils.getPageList(Mysplash.BUILDING_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return MathUtils.getPageList(Mysplash.FOOD_DRINK_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_NATURE_ID:
                return MathUtils.getPageList(Mysplash.NATURE_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_OBJECTS_ID:
                return MathUtils.getPageList(Mysplash.OBJECTS_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_PEOPLE_ID:
                return MathUtils.getPageList(Mysplash.PEOPLE_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return MathUtils.getPageList(Mysplash.TECHNOLOGY_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            default:
                return MathUtils.getPageList(0);
        }
    }
}
