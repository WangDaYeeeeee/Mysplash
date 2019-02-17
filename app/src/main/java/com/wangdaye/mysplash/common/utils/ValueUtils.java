package com.wangdaye.mysplash.common.utils;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Value utils.
 *
 * An utils class to handle some values.
 *
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

    public static String getDownloaderName(Context c, String key) {
        switch (key) {
            case "mysplash":
                return c.getResources().getStringArray(R.array.downloader_types)[0];

            case "system":
                return c.getResources().getStringArray(R.array.downloader_types)[1];

            default:
                return null;
        }
    }

    public static String getScaleName(Context c, String key) {
        switch (key) {
            case "tiny":
                return c.getResources().getStringArray(R.array.download_types)[0];

            case "compact":
                return c.getResources().getStringArray(R.array.download_types)[1];

            case "raw":
                return c.getResources().getStringArray(R.array.download_types)[2];

            default:
                return null;
        }
    }

    public static String getOrientationName(Context c, String key) {
        switch (key) {
            case "landscape":
                return c.getResources().getStringArray(R.array.search_orientations)[0];

            case "portrait":
                return c.getResources().getStringArray(R.array.search_orientations)[1];

            case "square":
                return c.getResources().getStringArray(R.array.search_orientations)[2];

            default:
                return null;
        }
    }

    public static String getBackToTopName(Context c, String key) {
        switch (key) {
            case "all":
                return c.getResources().getStringArray(R.array.back_to_top_types)[0];

            case "home":
                return c.getResources().getStringArray(R.array.back_to_top_types)[1];

            case "none":
                return c.getResources().getStringArray(R.array.back_to_top_types)[2];

            default:
                return null;
        }
    }

    public static String getAutoNightModeName(Context c, String key) {
        switch (key) {
            case "close":
                return c.getResources().getStringArray(R.array.auto_night_mode_types)[0];

            case "auto":
                return c.getResources().getStringArray(R.array.auto_night_mode_types)[1];

            case "follow_system":
                return c.getResources().getStringArray(R.array.auto_night_mode_types)[2];

            default:
                return null;
        }
    }

    public static String getSaturationAnimationDurationName(Context c, String key) {
        switch (key) {
            case "300":
                return c.getResources().getStringArray(R.array.saturation_animation_durations)[0];

            case "1000":
                return c.getResources().getStringArray(R.array.saturation_animation_durations)[1];

            case "2000":
                return c.getResources().getStringArray(R.array.saturation_animation_durations)[2];

            default:
                return null;
        }
    }

    public static String getLanguageName(Context c, String key) {
        switch (key) {
            case "follow_system":
                return c.getResources().getStringArray(R.array.languages)[0];

            case "english_usa":
                return c.getResources().getStringArray(R.array.languages)[1];

            case "english_uk":
                return c.getResources().getStringArray(R.array.languages)[2];

            case "english_au":
                return c.getResources().getStringArray(R.array.languages)[3];

            case "chinese":
                return c.getResources().getStringArray(R.array.languages)[4];

            case "italian":
                return c.getResources().getStringArray(R.array.languages)[5];

            case "turkish":
                return c.getResources().getStringArray(R.array.languages)[6];

            case "german":
                return c.getResources().getStringArray(R.array.languages)[7];

            case "russian":
                return c.getResources().getStringArray(R.array.languages)[8];

            case "spanish":
                return c.getResources().getStringArray(R.array.languages)[9];

            case "japanese":
                return c.getResources().getStringArray(R.array.languages)[10];

            case "french":
                return c.getResources().getStringArray(R.array.languages)[11];

            default:
                return null;
        }
    }

    public static String getMuzeiSourceName(Context c, String key) {
        switch (key) {
            case "all":
                return c.getResources().getStringArray(R.array.muzei_sources)[0];

            case "featured":
                return c.getResources().getStringArray(R.array.muzei_sources)[1];

            case "collection":
                return c.getResources().getStringArray(R.array.muzei_sources)[2];

            default:
                return null;
        }
    }

    public static String getMuzeiCacheModeName(Context c, String key) {
        switch (key) {
            case "keep":
                return c.getResources().getStringArray(R.array.muzei_cache_modes)[0];

            case "delete":
                return c.getResources().getStringArray(R.array.muzei_cache_modes)[1];

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
                return getPageList(Mysplash.TOTAL_NEW_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TOTAL_FEATURED:
                return getPageList(Mysplash.TOTAL_FEATURED_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_BUILDINGS_ID:
                return getPageList(Mysplash.BUILDING_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return getPageList(Mysplash.FOOD_DRINK_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_NATURE_ID:
                return getPageList(Mysplash.NATURE_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_OBJECTS_ID:
                return getPageList(Mysplash.OBJECTS_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_PEOPLE_ID:
                return getPageList(Mysplash.PEOPLE_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return getPageList(Mysplash.TECHNOLOGY_PHOTOS_COUNT / Mysplash.DEFAULT_PER_PAGE);

            default:
                return getPageList(0);
        }
    }

    private static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    private static List<Integer> getPageList(int max) {
        List<Integer> oldList = new ArrayList<>();
        for (int i = 0; i < max; i ++) {
            oldList.add(i);
        }

        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i < max; i ++) {
            newList.add(oldList.get(getRandomInt(oldList.size())));
        }

        return newList;
    }
}
