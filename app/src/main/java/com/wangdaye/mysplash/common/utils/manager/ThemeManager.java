package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;

/**
 * Theme manager.
 *
 * This class is used to manage theme information.
 *
 * */

public class ThemeManager {

    private static ThemeManager instance;

    public static ThemeManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager(context);
                }
            }
        }
        return instance;
    }

    private boolean lightTheme;

    private static final String PREFERENCE_NAME = "mysplash_theme_manager";
    private static final String KEY_LIGHT_THEME = "light_theme";

    private ThemeManager(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.lightTheme = sharedPreferences.getBoolean(KEY_LIGHT_THEME, true);
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void setLightTheme(Context context, boolean lightTheme) {
        this.lightTheme = lightTheme;
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_LIGHT_THEME, lightTheme);
        editor.apply();
    }

    @ColorInt
    public static int getPrimaryColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] {R.attr.colorPrimary});
        int color = a.getColor(0, ContextCompat.getColor(context, R.color.colorPrimary_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getPrimaryDarkColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] {R.attr.colorPrimaryDark});
        int color = a.getColor(0, ContextCompat.getColor(context, R.color.colorPrimaryDark_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getRootColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_root_color,
                ContextCompat.getColor(context, R.color.colorRoot_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getLineColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_line_color,
                ContextCompat.getColor(context, R.color.colorLine_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getTitleColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_title_color,
                ContextCompat.getColor(context, R.color.colorTextTitle_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getSubtitleColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_subtitle_color,
                ContextCompat.getColor(context, R.color.colorTextSubtitle_light));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getContentColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_content_color,
                ContextCompat.getColor(context, R.color.colorTextContent_light));
        a.recycle();
        return color;
    }

    public static void setNavigationIcon(Toolbar toolbar,
                                         @DrawableRes int lightResId, @DrawableRes int darkResId) {
        if (getInstance(toolbar.getContext()).isLightTheme()) {
            toolbar.setNavigationIcon(lightResId);
        } else {
            toolbar.setNavigationIcon(darkResId);
        }
    }

    public static void inflateMenu(Toolbar toolbar,
                                   @MenuRes int lightResId, @MenuRes int darkResId) {
        if (getInstance(toolbar.getContext()).isLightTheme()) {
            toolbar.inflateMenu(lightResId);
        } else {
            toolbar.inflateMenu(darkResId);
        }
    }

    public static void setImageResource(ImageView imageView,
                                        @DrawableRes int lightResId, @DrawableRes int darkResId) {
        if (getInstance(imageView.getContext()).isLightTheme()) {
            imageView.setImageResource(lightResId);
        } else {
            imageView.setImageResource(darkResId);
        }
    }
}
