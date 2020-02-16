package com.wangdaye.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.Size;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.wangdaye.common.R;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.utils.manager.ThemeManager;

import java.lang.reflect.Method;
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

    public static void setSystemBarStyle(MysplashActivity activity,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {
        setSystemBarStyle(activity,
                false, statusShader, lightStatus, navigationShader, lightNavigation);
    }

    public static void setSystemBarStyle(MysplashActivity activity, boolean miniAlpha,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        statusShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightStatus &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        navigationShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightNavigation &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (lightStatus) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (lightNavigation) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(visibility);

        setSystemBarColor(activity, miniAlpha, statusShader, lightStatus, navigationShader, lightNavigation);
    }

    public static void setSystemBarColor(MysplashActivity activity, boolean miniAlpha,
                                         boolean statusShader, boolean lightStatus,
                                         boolean navigationShader, boolean lightNavigation) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        // statusShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightStatus &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        navigationShader &= Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
        lightNavigation &= Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

        if (!statusShader) {
            // window.setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.argb(1, 0, 0, 0));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(getStatusBarColor23(activity, lightStatus, miniAlpha));
        } else {
            activity.getWindow().setStatusBarColor(getStatusBarColor21());
        }
        if (!navigationShader) {
            // window.setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.argb(1, 0, 0, 0));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(getStatusBarColor26(activity, lightNavigation, miniAlpha));
        } else {
            activity.getWindow().setNavigationBarColor(getNavigationBarColor21());
        }
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static int getStatusBarColor21() {
        return ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.M)
    private static int getStatusBarColor23(Context context, boolean light, boolean miniAlpha) {
        if (miniAlpha) {
            return light
                    ? ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.2 * 255))
                    : ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
        }
        return ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, light ? R.color.colorPrimary_light : R.color.colorPrimary_dark),
                (int) (0.5 * 255)
        );
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static int getNavigationBarColor21() {
        return ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
    }

    @ColorInt
    @RequiresApi(Build.VERSION_CODES.O)
    private static int getStatusBarColor26(Context context, boolean light, boolean miniAlpha) {
        if (miniAlpha) {
            return light
                    ? ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.2 * 255))
                    : ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1 * 255));
        }
        return ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, light ? R.color.colorPrimary_light : R.color.colorPrimary_dark),
                (int) (0.5 * 255)
        );
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

    public static void inflateToolbarMenu(Toolbar toolbar, @MenuRes int menuId,
                                          Toolbar.OnMenuItemClickListener listener) {
        toolbar.inflateMenu(menuId);
        toolbar.setOnMenuItemClickListener(listener);
        // setOverflowMenuIconsVisible(toolbar.getMenu());
    }

    public static void setOverflowMenuIconsVisible(Menu menu) {
        if (menu == null) {
            return;
        }
        if (menu instanceof MenuBuilder) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
