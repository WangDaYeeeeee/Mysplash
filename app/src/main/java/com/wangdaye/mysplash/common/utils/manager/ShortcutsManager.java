package com.wangdaye.mysplash.common.utils.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.wangdaye.mysplash.R;
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

    /** <br> utils. */

    @TargetApi(25)
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void refreshShortcuts(Context c) {
        ShortcutManager shortcutManager = c.getSystemService(ShortcutManager.class);

        List<ShortcutInfo> shortcutList = new ArrayList<>();
        shortcutList.add(
                new ShortcutInfo.Builder(c, c.getString(R.string.action_search))
                        .setIcon(Icon.createWithResource(c, R.drawable.ic_shortcut_search))
                        .setShortLabel(c.getString(R.string.action_search))
                        .setLongLabel(c.getString(R.string.action_search))
                        .setIntent(new Intent("com.wangdaye.mysplash.Search"))
                        .setRank(2)
                        .build());
        shortcutList.add(
                new ShortcutInfo.Builder(c, c.getString(R.string.action_download_manage))
                        .setIcon(Icon.createWithResource(c, R.drawable.ic_shortcut_download))
                        .setShortLabel(c.getString(R.string.action_download_manage))
                        .setLongLabel(c.getString(R.string.action_download_manage))
                        .setIntent(new Intent("com.wangdaye.mysplash.DownloadManager"))
                        .setRank(3)
                        .build());
        if (AuthManager.getInstance().isAuthorized()) {
            shortcutList.add(
                    new ShortcutInfo.Builder(c, AuthManager.getInstance().getUsername())
                            .setIcon(Icon.createWithResource(c, R.drawable.default_avatar_round))
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
