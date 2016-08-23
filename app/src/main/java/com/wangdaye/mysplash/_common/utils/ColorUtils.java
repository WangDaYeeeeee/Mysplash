package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.graphics.Color;

/**
 * Color utils.
 * */

public class ColorUtils {

    public static int calcCardBackgroundColor(Context c, String color) {
        int backgroundColor = Color.parseColor(color);
        int red = ((backgroundColor & 0x00FF0000) >> 16);
        int green = ((backgroundColor & 0x0000FF00) >> 8);
        int blue = (backgroundColor & 0x000000FF);
        if (ThemeUtils.getInstance(c).isLightTheme()) {
            return Color.rgb(
                    (int) (red + (255 - red) * 0.7),
                    (int) (green + (255 - green) * 0.7),
                    (int) (blue + (255 - blue) * 0.7));
        } else {
            return Color.rgb(
                    (int) (red * 0.3),
                    (int) (green * 0.3),
                    (int) (blue * 0.3));
        }
    }
}
