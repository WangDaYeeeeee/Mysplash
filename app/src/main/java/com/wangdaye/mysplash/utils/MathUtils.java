package com.wangdaye.mysplash.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Math utils.
 * */

public class MathUtils {

    public static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    public static List<Integer> getPageList(int max) {
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
