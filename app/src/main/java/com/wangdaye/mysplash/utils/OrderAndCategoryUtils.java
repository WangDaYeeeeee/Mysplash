package com.wangdaye.mysplash.utils;

import android.content.Context;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.constant.Mysplash;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;

import java.util.List;

/**
 * Order and category utils.
 * */

public class OrderAndCategoryUtils {

    public static String getOrderName(String key) {
        switch (key) {
            case PhotoApi.ORDER_BY_LATEST:
                return "Latest";

            case PhotoApi.ORDER_BY_OLDEST:
                return "Oldest";

            case PhotoApi.ORDER_BY_POPULAR:
                return "Popular";

            default:
                return null;
        }
    }

    public static String getOrderKey(String name) {
        switch (name) {
            case "Latest":
                return PhotoApi.ORDER_BY_LATEST;

            case "Oldest":
                return PhotoApi.ORDER_BY_OLDEST;

            case "Popular":
                return PhotoApi.ORDER_BY_POPULAR;

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

    public static List<Integer> getPositionListByCategory(int id) {
        switch (id) {
            case Mysplash.CATEGORY_BUILDINGS_ID:
                return MathUtils.getPositionList(Mysplash.BUILDING_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_FOOD_DRINK_ID:
                return MathUtils.getPositionList(Mysplash.FOOD_DRINK_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_NATURE_ID:
                return MathUtils.getPositionList(Mysplash.NATURE_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_OBJECTS_ID:
                return MathUtils.getPositionList(Mysplash.OBJECTS_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_PEOPLE_ID:
                return MathUtils.getPositionList(Mysplash.PEOPLE_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            case Mysplash.CATEGORY_TECHNOLOGY_ID:
                return MathUtils.getPositionList(Mysplash.TECHNOLOGY_PHOTOS_COUNT / PhotoApi.DEFAULT_PER_PAGE);

            default:
                return MathUtils.getPositionList(0);
        }
    }
}
