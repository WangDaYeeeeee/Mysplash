package com.wangdaye.common.utils.manager;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;

import com.wangdaye.common.R;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.component.ComponentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Shortcuts manager.
 *
 * A manager class that is used to manage shortcuts.
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsManager {

    @UiThread
    public static void refreshShortcuts(Context context, @Nullable User user) {
        ThreadManager.getInstance().execute(() -> {
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
                            .setIntent(
                                    ComponentFactory.getSearchModule()
                                            .getSearchActivityIntentForShortcut()
                            ).setRank(2)
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
                            .setIntent(
                                    ComponentFactory.getDownloaderService()
                                            .getDownloadManageActivityIntentForShortcut()
                            ).setRank(3)
                            .build()
            );

            if (user != null) {
                Bitmap avatar = null;
                try {
                    avatar = ImageHelper.loadBitmap(context, Uri.parse(user.profile_image.large), null);
                } catch (Exception ignore) {
                    // do nothing.
                }
                if (avatar == null) {
                    try {
                        int size = (int) Math.min(new DisplayUtils(context).dpToPx(108), 192);
                        avatar = ImageHelper.loadBitmap(
                                context,
                                R.drawable.default_avatar_foreground,
                                new int[] {size, size}
                        );
                    } catch (Exception ignore) {
                        // do nothing.
                    }
                }

                Icon icon;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && avatar != null) {
                    icon = Icon.createWithAdaptiveBitmap(avatar);
                } else if (avatar != null) {
                    icon = Icon.createWithBitmap(avatar);
                } else {
                    icon = Icon.createWithResource(context, R.drawable.default_avatar_round);
                }

                shortcutList.add(
                        new ShortcutInfo.Builder(context, user.username)
                                .setIcon(icon)
                                .setShortLabel(user.username)
                                .setLongLabel(user.username)
                                .setIntent(
                                        ComponentFactory.getMeModule()
                                                .getMeActivityIntentForShortcut()
                                ).setRank(1)
                                .build());
            }

            shortcutManager.setDynamicShortcuts(shortcutList);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private static Icon getShortcutsIcon(Context context,
                                         @DrawableRes int id, @DrawableRes int foregroundId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DisplayUtils utils = new DisplayUtils(context);
                int size = (int) Math.min(utils.dpToPx(108), 288);
                Bitmap bitmap = ImageHelper.loadBitmap(context, foregroundId, new int[] {size, size});
                return Icon.createWithAdaptiveBitmap(bitmap);
            } catch (ExecutionException | InterruptedException ignored) {

            }
        }
        return Icon.createWithResource(context, id);
    }
}
