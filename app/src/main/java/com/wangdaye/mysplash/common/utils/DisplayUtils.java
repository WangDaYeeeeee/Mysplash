package com.wangdaye.mysplash.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;

import com.wangdaye.mysplash.R;

/**
 * Display utils.
 * */

public class DisplayUtils {
    // data
    private int dpi = 0;

    /** <br> life cycle. */

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
        int resourceId = r.getIdentifier("status_bar_height", "dimen","android");
        return r.getDimensionPixelSize(resourceId);
    }

    public static void setWindowTop(Activity activity, String name, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
            ActivityManager.TaskDescription taskDescription
                    = new ActivityManager.TaskDescription(name, icon, color);
            activity.setTaskDescription(taskDescription);
            icon.recycle();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarTextDark(Activity activity) {
        if (ModeUtils.getInstance(activity).isNeedSetStatusBarTextDark()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
