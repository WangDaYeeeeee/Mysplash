package com.wangdaye.common.utils;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

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

    @Nullable
    public static String getNameByValue(Context context, String value,
                                        @ArrayRes int nameArrayId, @ArrayRes int valueArrayId) {
        String[] names = context.getResources().getStringArray(nameArrayId);
        String[] values = context.getResources().getStringArray(valueArrayId);

        for (int i = 0; i < values.length; i ++) {
            if (values[i].equals(value)) {
                return names[i];
            }
        }

        return null;
    }

    public static List<Integer> getRandomPageList(int total, int perPage) {
        int count = total / perPage;

        List<Integer> oldList = new ArrayList<>();
        for (int i = 0; i < count; i ++) {
            oldList.add(i);
        }

        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i < count; i ++) {
            newList.add(oldList.get(getRandomInt(oldList.size())));
        }

        return newList;
    }

    private static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }
}
