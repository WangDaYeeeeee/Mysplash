package com.wangdaye.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.manager.ThemeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Display utils.
 *
 * An utils class that make operations of display easier.
 *
 * */

public class DisplayUtils {

    private int dpi;

    public DisplayUtils(Context context) {
        dpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    public float dpToPx(int dp) {
        if (dpi == 0) {
            return 0;
        }
        return (float) (dp * (dpi / 160.0));
    }

    public static int getStatusBarHeight(Resources r) {
        int result = 0;
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        if (!isNavigationBarShow(context)){
            return 0;
        }
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Size(2)
    public static int[] getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Resources r = context.getResources();

        if (manager == null) {
            return new int[] {r.getDisplayMetrics().widthPixels, r.getDisplayMetrics().heightPixels};
        } else {
            Point size = new Point();
            Display display = manager.getDefaultDisplay();
            display.getRealSize(size);
            return new int[] {size.x, size.y};
        }
    }

    private static boolean isNavigationBarShow(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) {
            return false;
        }

        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    }

    public static void setWindowTop(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.setTaskDescription(
                    new ActivityManager.TaskDescription(
                            activity.getString(R.string.app_name),
                            R.mipmap.ic_launcher,
                            ThemeManager.getPrimaryColor(activity)
                    )
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setTaskDescription(
                    new ActivityManager.TaskDescription(
                            activity.getString(R.string.app_name),
                            BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher),
                            ThemeManager.getPrimaryColor(activity)
                    )
            );
        }
    }

    public static void setStatusBarStyle(@NonNull Activity activity, boolean onlyDarkStatusBar) {
        setStatusBarStyle(
                activity, onlyDarkStatusBar, ThemeManager.getInstance(activity).isLightTheme());
    }

    public static void setStatusBarStyle(@NonNull Activity activity,
                                         boolean onlyDarkStatusBar, boolean lightTheme) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (onlyDarkStatusBar || !lightTheme) {
                flags ^= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @SuppressLint("InlinedApi")
    public static void setNavigationBarStyle(@NonNull Activity activity,
                                             boolean onlyDarkNavigationBar, boolean translucent) {
        setNavigationBarStyle(activity,
                onlyDarkNavigationBar, translucent, ThemeManager.getInstance(activity).isLightTheme());
    }

    @SuppressLint("InlinedApi")
    public static void setNavigationBarStyle(@NonNull Activity activity,
                                             boolean onlyDarkNavigationBar, boolean translucent,
                                             boolean lightTheme) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (translucent) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        } else {
            flags ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }

        flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; // android O (API 26).

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navigationBarColor;
            if (translucent) {
                if (MysplashApplication.getInstance().getWindowInsets().bottom == 0) {
                    if (lightTheme) {
                        navigationBarColor = Color.argb((int) (0.03 * 255), 0, 0, 0);
                    } else {
                        flags ^= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; // android O (API 26).
                        navigationBarColor = Color.argb((int) (0.2 * 255), 0, 0, 0);
                    }
                } else {
                    if (!onlyDarkNavigationBar && lightTheme) {
                        navigationBarColor = Color.argb((int) (0.03 * 255), 0, 0, 0);
                    } else {
                        flags ^= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; // android O (API 26).
                        navigationBarColor = Color.argb((int) (0.2 * 255), 0, 0, 0);
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (lightTheme) {
                    navigationBarColor = Color.rgb(241, 241, 241);
                } else {
                    flags ^= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; // android O (API 26).
                    navigationBarColor = Color.rgb(26, 26, 26);
                }
            } else {
                navigationBarColor = Color.BLACK;
            }
            activity.getWindow().setNavigationBarColor(navigationBarColor);
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    public static void changeTheme(Context c) {
        ThemeManager.getInstance(c).setLightTheme(c, !ThemeManager.getInstance(c).isLightTheme());
    }

    public static String abridgeNumber(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        } else {
            num = num / 100;
            return (num / 10.0) + "K";
        }
    }

    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources()
                .getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    public static String getDate(Context context, String text) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            try {
                return new SimpleDateFormat(
                        context.getString(R.string.date_format),
                        LanguageUtils.getLocale()
                ).format(date);
            } catch (Exception ignored) {

            }
        }
        try {
            return text.split("T")[0];
        } catch (Exception ignored) {

        }
        return text;
    }
}
