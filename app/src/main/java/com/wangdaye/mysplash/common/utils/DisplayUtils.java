package com.wangdaye.mysplash.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Display utils.
 *
 * An utils class that make operations of display easier.
 *
 * */

public class DisplayUtils {

    private int dpi = 0;

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

    public static int getNavigationBarHeight(Resources r) {
        if (!isNavigationBarShow()){
            return 0;
        }
        int result = 0;
        int resourceId = r.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static boolean isNavigationBarShow(){
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            return false;
        }
    }

    public static void setWindowTop(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
            ActivityManager.TaskDescription taskDescription
                    = new ActivityManager.TaskDescription(
                    activity.getString(R.string.app_name),
                    icon,
                    ThemeManager.getPrimaryColor(activity));
            activity.setTaskDescription(taskDescription);
            icon.recycle();
        }
    }

    public static void initStatusBarStyle(Activity activity) {
        setStatusBarStyle(activity, false);
    }

    public static void setStatusBarStyle(Activity activity, boolean onlyWhiteText) {
        if (!onlyWhiteText && isNeedSetStatusBarTextDark(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    private static boolean isNeedSetStatusBarTextDark(Context c) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ThemeManager.getInstance(c).isLightTheme();
    }

    public static void cancelTranslucentNavigation(Activity a) {
        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public static void changeTheme(Context c) {
        ThemeManager.getInstance(c)
                .setLightTheme(
                        c,
                        !ThemeManager.getInstance(c).isLightTheme());
    }

    public static void setTypeface(Context c, TextView t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Courier.ttf"));
        }
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

    public static int getGirdColumnCount(Context context) {
        if (isLandscape(context)) {
            if (SettingsOptionManager.getInstance(context).isShowGridInLand()) {
                if (isTabletDevice(context)) {
                    return 3;
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        } else  {
            if (SettingsOptionManager.getInstance(context).isShowGridInPort()
                    && isTabletDevice(context)) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    public static boolean isLandscape(Context context) {
        return context.getResources()
                .getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
