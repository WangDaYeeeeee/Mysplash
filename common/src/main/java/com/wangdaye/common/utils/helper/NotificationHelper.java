package com.wangdaye.common.utils.helper;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.wangdaye.common.R;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.manager.ThemeManager;

/**
 * Notification helper.
 * */

public class NotificationHelper {

    public static void showSnackbar(String content) {
        MysplashActivity a = MysplashApplication.getInstance().getTopActivity();
        if (a != null) {
            showSnackbar(a, content);
        }
    }

    public static void showSnackbar(@NonNull MysplashActivity activity, String content) {
        View container = activity.provideSnackbarContainer();
        if (container != null) {
            Snackbar snackbar = Snackbar.make(container, content, Snackbar.LENGTH_SHORT);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setBackgroundColor(ThemeManager.getRootColor(activity));

            TextView contentTxt = snackbarLayout.findViewById(R.id.snackbar_text);
            contentTxt.setTextColor(ThemeManager.getContentColor(activity));

            snackbar.show();
        }
    }

    public static void showActionSnackbar(String content, String action,
                                           View.OnClickListener l) {
        MysplashActivity a = MysplashApplication.getInstance().getTopActivity();
        if (a != null) {
            showActionSnackbar(a, content, action, l);
        }
    }

    public static void showActionSnackbar(@NonNull MysplashActivity activity,
                                          String content, String action, View.OnClickListener l) {
        View container = activity.provideSnackbarContainer();
        if (container != null) {
            Snackbar snackbar = Snackbar
                    .make(container, content, Snackbar.LENGTH_LONG)
                    .setAction(action, l);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setBackgroundColor(ThemeManager.getRootColor(activity));

            TextView contentTxt = snackbarLayout.findViewById(R.id.snackbar_text);
            contentTxt.setTextColor(ThemeManager.getContentColor(activity));

            Button actionBtn = snackbarLayout.findViewById(R.id.snackbar_action);
            actionBtn.setTextColor(ThemeManager.getTitleColor(activity));

            snackbar.show();
        }
    }
}
