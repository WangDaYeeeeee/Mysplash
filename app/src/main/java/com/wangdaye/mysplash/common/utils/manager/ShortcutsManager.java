package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import android.text.TextUtils;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.me.ui.activity.MeActivity;
import com.wangdaye.mysplash.search.ui.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private static final int VERSION_CODE = 2;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @UiThread
    public static void refreshShortcuts(final Context context) {
        if (needRefresh(context)) {
            ThreadManager.getInstance().execute(() -> setShortcuts(context));
        }
    }

    private static boolean needRefresh(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        int versionCode = sharedPreferences.getInt(KEY_VERSION_CODE, 0);
        boolean authorized = sharedPreferences.getBoolean(KEY_AUTHORIZED, false);
        String avatarUrl = sharedPreferences.getString(KEY_AVATAR_URL, "");
        String username = sharedPreferences.getString(KEY_USERNAME, "");

        String authAvatarUrl = null;
        if (AuthManager.getInstance().getUser() != null
                && AuthManager.getInstance().getUser().profile_image != null
                && !TextUtils.isEmpty(AuthManager.getInstance().getUser().profile_image.large)) {
            authAvatarUrl = AuthManager.getInstance().getUser().profile_image.large;
        }
        if (authAvatarUrl == null) {
            authAvatarUrl = "";
        }
        String authUsername = AuthManager.getInstance().getUsername();
        if (authUsername == null) {
            authUsername = "";
        }

        if (versionCode < VERSION_CODE
                || authorized != AuthManager.getInstance().isAuthorized()
                || avatarUrl == null
                || !avatarUrl.equals(authAvatarUrl)
                || username == null
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
    private static void setShortcuts(Context context) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        if (shortcutManager == null) {
            return;
        }

        List<ShortcutInfo> shortcutList = new ArrayList<>();
        shortcutList.add(
                new ShortcutInfo.Builder(context, context.getString(R.string.action_search))
                        .setIcon(
                                getShortcutsIcon(
                                        context,
                                        R.drawable.ic_shortcut_search,
                                        R.drawable.ic_shortcut_search_foreground
                                )
                        ).setShortLabel(context.getString(R.string.action_search))
                        .setLongLabel(context.getString(R.string.action_search))
                        .setIntent(new Intent(SearchActivity.ACTION_SEARCH))
                        .setRank(2)
                        .build()
        );
        shortcutList.add(
                new ShortcutInfo.Builder(context, context.getString(R.string.action_download_manage))
                        .setIcon(
                                getShortcutsIcon(
                                        context,
                                        R.drawable.ic_shortcut_download,
                                        R.drawable.ic_shortcut_download_foreground
                                )
                        ).setShortLabel(context.getString(R.string.action_download_manage))
                        .setLongLabel(context.getString(R.string.action_download_manage))
                        .setIntent(new Intent(DownloadManageActivity.ACTION_DOWNLOAD_MANAGER))
                        .setRank(3)
                        .build()
        );

        if (AuthManager.getInstance().isAuthorized() && AuthManager.getInstance().getUser() != null) {

            Icon icon;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DisplayUtils utils = new DisplayUtils(context);
                int size = (int) Math.min(utils.dpToPx(108), 192);
                try {
                    icon = Icon.createWithAdaptiveBitmap(
                            ImageHelper.loadBitmap(
                                    context,
                                    R.drawable.default_avatar_foreground,
                                    size, size
                            )
                    );
                } catch (ExecutionException | InterruptedException e) {
                    icon = Icon.createWithResource(context, R.drawable.default_avatar_round);
                }
            } else {
                icon = Icon.createWithResource(context, R.drawable.default_avatar_round);
            }

            shortcutList.add(
                    new ShortcutInfo.Builder(context, AuthManager.getInstance().getUser().username)
                            .setIcon(icon)
                            .setShortLabel(AuthManager.getInstance().getUser().username)
                            .setLongLabel(AuthManager.getInstance().getUser().username)
                            .setIntent(
                                    new Intent("com.wangdaye.mysplash.Me")
                                            .putExtra(MeActivity.EXTRA_BROWSABLE, true)
                            )
                            .setRank(1)
                            .build());
        }

        shortcutManager.setDynamicShortcuts(shortcutList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private static Icon getShortcutsIcon(Context context,
                                         @DrawableRes int id, @DrawableRes int foregroundId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DisplayUtils utils = new DisplayUtils(context);
                int size = (int) Math.min(utils.dpToPx(108), 288);
                Bitmap bitmap = ImageHelper.loadBitmap(context, foregroundId, size, size);
                return Icon.createWithAdaptiveBitmap(bitmap);
            } catch (ExecutionException | InterruptedException ignored) {

            }
        }
        return Icon.createWithResource(context, id);
    }
}
