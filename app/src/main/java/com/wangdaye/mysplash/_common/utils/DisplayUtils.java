package com.wangdaye.mysplash._common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.ProfileImage;
import com.wangdaye.mysplash._common.utils.widget.glide.CircleTransformation;
import com.wangdaye.mysplash._common.utils.widget.glide.ColorAnimRequestListener;

import org.greenrobot.greendao.annotation.NotNull;

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
        int result = 0;
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context c) {
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = c.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setWindowTop(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
            if (Mysplash.getInstance().isLightTheme()) {
                ActivityManager.TaskDescription taskDescription
                        = new ActivityManager.TaskDescription(
                        activity.getString(R.string.app_name),
                        icon,
                        ContextCompat.getColor(activity, R.color.colorPrimary_light));
                activity.setTaskDescription(taskDescription);
            } else {
                ActivityManager.TaskDescription taskDescription
                        = new ActivityManager.TaskDescription(
                        activity.getString(R.string.app_name),
                        icon,
                        ContextCompat.getColor(activity, R.color.colorPrimary_dark));
                activity.setTaskDescription(taskDescription);
            }
            icon.recycle();
        }
    }

    public static void setStatusBarTextDark(Activity activity) {
        if (isNeedSetStatusBarTextDark()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private static boolean isNeedSetStatusBarTextDark() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Mysplash.getInstance().isLightTheme();
    }

    public static boolean isNeedSetStatusBarMask() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && Mysplash.getInstance().isLightTheme();
    }

    public static void changeTheme(Context c) {
        Mysplash.getInstance().changeTheme();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putBoolean(c.getString(R.string.key_light_theme), Mysplash.getInstance().isLightTheme());
        editor.apply();
    }

    public static void setTypeface(Context c, TextView t) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Courier.ttf"));
        }
    }

    public static int calcCardBackgroundColor(String color) {
        int backgroundColor = Color.parseColor(color);
        int red = ((backgroundColor & 0x00FF0000) >> 16);
        int green = ((backgroundColor & 0x0000FF00) >> 8);
        int blue = (backgroundColor & 0x000000FF);
        if (Mysplash.getInstance().isLightTheme()) {
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

    public static void loadAvatar(Context context, final ImageView view, ProfileImage profileImage) {
        if (profileImage == null || TextUtils.isEmpty(profileImage.large)) {
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view);
        } else {
            loadAvatar(context, view, profileImage.large);
        }
    }

    public static void loadAvatarWithColorAnim(final Context context, final ImageView view, ProfileImage profileImage) {
        if (profileImage == null || TextUtils.isEmpty(profileImage.large)) {
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .override(128, 128)
                    .transform(new CircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(view);
        } else {
            DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE);
            Glide.with(context)
                    .load(profileImage.large)
                    .override(128, 128)
                    .thumbnail(thumbnailRequest)
                    .error(R.drawable.default_avatar)
                    .transform(new CircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new ColorAnimRequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource,
                                                       String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            startColorAnimation(context, view);
                            return false;
                        }
                    })
                    .into(view);
        }
    }

    public static void loadAvatar(Context context, final ImageView view, @NotNull String url) {
        DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                .load(R.drawable.default_avatar)
                .override(128, 128)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        Glide.with(context)
                .load(url)
                .override(128, 128)
                .thumbnail(thumbnailRequest)
                .error(R.drawable.default_avatar)
                .transform(new CircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(view);
    }
}
