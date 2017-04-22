package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortcuts manager.
 *
 * A manager class that is used to manage shortcuts.
 *
 * */

public class ShortcutsManager {

    private static final String PREFERENCE_NAME = "mysplash_shortcuts_manager";
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String KEY_AUTHORIZED = "authorized";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_USERNAME = "username";

    private static final int VERSION_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @UiThread
    public static void refreshShortcuts(final Context context) {
        if (needRefresh(context)) {
            if (!TextUtils.isEmpty(AuthManager.getInstance().getAvatarPath())) {
                ImageHelper.loadBitmap(
                        context,
                        new SimpleTarget<Bitmap>(128, 128) {
                            @Override
                            public void onResourceReady(Bitmap resource,
                                                        GlideAnimation<? super Bitmap> glideAnimation) {
                                setShortcuts(context, resource);
                            }
                        },
                        AuthManager.getInstance().getAvatarPath(),
                        true);
            } else {
                setShortcuts(context, null);
            }
        }
    }

    private static boolean needRefresh(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE);
        int versionCode = sharedPreferences.getInt(KEY_VERSION_CODE, 0);
        boolean authorized = sharedPreferences.getBoolean(KEY_AUTHORIZED, false);
        String avatarUrl = sharedPreferences.getString(KEY_AVATAR_URL, "");
        String username = sharedPreferences.getString(KEY_USERNAME, "");

        String authAvatarUrl = AuthManager.getInstance().getAvatarPath();
        if (authAvatarUrl == null) {
            authAvatarUrl = "";
        }
        String authUsername = AuthManager.getInstance().getUsername();
        if (authUsername == null) {
            authUsername = "";
        }

        if (versionCode < VERSION_CODE
                || authorized != AuthManager.getInstance().isAuthorized()
                || !avatarUrl.equals(authAvatarUrl)
                || !username.equals(authUsername)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_VERSION_CODE, VERSION_CODE);
            editor.putBoolean(KEY_AUTHORIZED, AuthManager.getInstance().isAuthorized());
            editor.putString(KEY_AVATAR_URL, authAvatarUrl);
            editor.putString(KEY_USERNAME, authUsername);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private static void setShortcuts(Context context, @Nullable Bitmap bitmap) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        List<ShortcutInfo> shortcutList = new ArrayList<>();
        shortcutList.add(
                new ShortcutInfo.Builder(context, context.getString(R.string.action_search))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut_search))
                        .setShortLabel(context.getString(R.string.action_search))
                        .setLongLabel(context.getString(R.string.action_search))
                        .setIntent(new Intent(MainActivity.ACTION_SEARCH))
                        .setRank(2)
                        .build());
        shortcutList.add(
                new ShortcutInfo.Builder(context, context.getString(R.string.action_download_manage))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut_download))
                        .setShortLabel(context.getString(R.string.action_download_manage))
                        .setLongLabel(context.getString(R.string.action_download_manage))
                        .setIntent(new Intent(DownloadManageActivity.ACTION_DOWNLOAD_MANAGER))
                        .setRank(3)
                        .build());
        if (AuthManager.getInstance().isAuthorized()) {
            Icon icon;
            if (bitmap != null) {
                icon = Icon.createWithBitmap(bitmap);
            } else {
                icon = Icon.createWithResource(context, R.drawable.default_avatar_round);
            }
            shortcutList.add(
                    new ShortcutInfo.Builder(context, AuthManager.getInstance().getUsername())
                            .setIcon(icon)
                            .setShortLabel(AuthManager.getInstance().getUsername())
                            .setLongLabel(AuthManager.getInstance().getUsername())
                            .setIntent(
                                    new Intent("com.wangdaye.mysplash.Me")
                                            .putExtra(MeActivity.EXTRA_BROWSABLE, true))
                            .setRank(1)
                            .build());
        }

        shortcutManager.setDynamicShortcuts(shortcutList);
    }
}
