package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;

import java.util.Calendar;

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
    private String nightStartTime;
    private String nightEndTime;

    private static final String PREFERENCE_NAME = "mysplash_theme_manager";
    private static final String KEY_LIGHT_THEME = "light_theme";
    private static final String KEY_NIGHT_START_TIME = "night_start_time";
    private static final String KEY_NIGHT_END_TIME = "night_end_time";

    private ThemeManager(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        nightStartTime = sharedPreferences.getString(KEY_NIGHT_START_TIME, "18:00");
        nightEndTime = sharedPreferences.getString(KEY_NIGHT_END_TIME, "06:00");

        switch (SettingsOptionManager.getInstance(context).getAutoNightMode()) {
            case "auto":
                setLightTheme(context, !isAutoNight(nightStartTime, nightEndTime));
                break;

            case "follow_system":
                setLightTheme(context, !isSystemNight(context));
                break;

            default: // close
                this.lightTheme = sharedPreferences.getBoolean(KEY_LIGHT_THEME, true);
                break;
        }
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

    public String getNightStartTime() {
        return nightStartTime;
    }

    public void setNightStartTime(Context context, String startTime) {
        this.nightStartTime = startTime;
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_NIGHT_START_TIME, startTime);
        editor.apply();
    }

    public String getNightEndTime() {
        return nightEndTime;
    }

    public void setNightEndTime(Context context, String EndTime) {
        this.nightEndTime = EndTime;
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_NIGHT_END_TIME, EndTime);
        editor.apply();
    }

    public boolean isDayNightSwitchTime(Context context) {
        switch (SettingsOptionManager.getInstance(context).getAutoNightMode()) {
            case "auto":
                return isLightTheme() == isAutoNight(getNightStartTime(), getNightEndTime());

            case "follow_system":
                return isLightTheme() == isSystemNight(context);
        }
        return false;
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
                ContextCompat.getColor(context, R.color.colorRoot));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getLineColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_line_color,
                ContextCompat.getColor(context, R.color.colorLine));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getTitleColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_title_color,
                ContextCompat.getColor(context, R.color.colorTextTitle));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getSubtitleColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_subtitle_color,
                ContextCompat.getColor(context, R.color.colorTextSubtitle));
        a.recycle();
        return color;
    }

    @ColorInt
    public static int getContentColor(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.ThemeColor);
        int color = a.getColor(
                R.styleable.ThemeColor_content_color,
                ContextCompat.getColor(context, R.color.colorTextContent));
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

    public static void setImageResource(ImageView imageView,
                                        @DrawableRes int lightResId, @DrawableRes int darkResId) {
        if (getInstance(imageView.getContext()).isLightTheme()) {
            imageView.setImageResource(lightResId);
        } else {
            imageView.setImageResource(darkResId);
        }
    }

    public static boolean isAutoNight(String start, String end) {
        String starts[] = start.split(":");
        String ends[] = end.split(":");

        Calendar calendar = Calendar.getInstance();

        int startTime = Integer.parseInt(starts[0]) * 60 + Integer.parseInt(starts[1]);
        int endTime = Integer.parseInt(ends[0]) * 60 + Integer.parseInt(ends[1]);
        int nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

        if (startTime < endTime) {
            return startTime <= nowTime && nowTime < endTime;
        } else {
            return !(endTime <= nowTime && nowTime < startTime);
        }
    }

    public static boolean isSystemNight(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
