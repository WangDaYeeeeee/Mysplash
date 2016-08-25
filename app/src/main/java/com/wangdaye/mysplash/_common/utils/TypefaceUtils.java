package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;

/**
 * Typeface utils.
 * */

public class TypefaceUtils {

    public static void setTypeface(Context c, TextView t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Courier.ttf"));
        }
    }
}
