package com.wangdaye.mysplash.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.content.ContextCompat;

import com.wangdaye.mysplash.R;

/**
 * Color utils.
 * */

public class ColorUtils {

    public static int calcBackgroundColor(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale((float) (1.0 / bitmap.getWidth()), (float) (1.0 / 2.0));
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 2, matrix, false);
        return bitmap.getPixel(0, 0);
    }

    public static boolean isLightColor(Context context, int color) {
        int alpha = 0xFF << 24;
        int grey = color;
        int red = ((grey & 0x00FF0000) >> 16);
        int green = ((grey & 0x0000FF00) >> 8);
        int blue = (grey & 0x000000FF);

        grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        grey = alpha | (grey << 16) | (grey << 8) | grey;
        return context != null && grey > ContextCompat.getColor(context, R.color.colorTextGrey2nd);
    }

    public static boolean needColorBurn(Context context, int color) {
        int alpha = 0xFF << 24;
        int grey = color;
        int red = ((grey & 0x00FF0000) >> 16);
        int green = ((grey & 0x0000FF00) >> 8);
        int blue = (grey & 0x000000FF);

        grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        grey = alpha | (grey << 16) | (grey << 8) | grey;
        return context != null && grey > Color.rgb(204, 204, 204);
    }

    public static int colorBurn(int color) {
        int red = ((color & 0x00FF0000) >> 16);
        int green = ((color & 0x0000FF00) >> 8);
        int blue = (color & 0x000000FF);
        return Color.rgb(
                (int) (red * 0.9),
                (int) (green * 0.9),
                (int) (blue * 0.9));
    }

    public static int colorOpaque(int color) {
        int red = ((color & 0x00FF0000) >> 16);
        int green = ((color & 0x0000FF00) >> 8);
        int blue = (color & 0x000000FF);
        return Color.rgb(red, green, blue);
    }
}
