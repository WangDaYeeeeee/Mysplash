package com.wangdaye.mysplash._common.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

/**
 * Snackbar utils.
 * */

public class NotificationUtils {

    public static void showSnackbar(String content, int duration) {
        if (Mysplash.getInstance().getActivityCount() > 0) {
            Activity a = Mysplash.getInstance().getTopActivity();
            View container = ((SnackbarContainer) a).getSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            TypefaceUtils.setTypeface(a, contentTxt);

            if (ThemeUtils.getInstance(a).isLightTheme()) {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_light));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_light);
            } else {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_dark));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_dark);
            }

            snackbar.show();
        }
    }

    public static void showActionSnackbar(String content, String action,
                                          int duration, View.OnClickListener l) {
        if (Mysplash.getInstance().getActivityCount() > 0) {
            Activity a = Mysplash.getInstance().getTopActivity();
            View container = ((SnackbarContainer) a).getSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration)
                    .setAction(action, l);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            TypefaceUtils.setTypeface(a, contentTxt);

            Button actionBtn = (Button) snackbarLayout.findViewById(R.id.snackbar_action);

            if (ThemeUtils.getInstance(a).isLightTheme()) {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_light));
                actionBtn.setTextColor(ContextCompat.getColor(a, R.color.colorTextTitle_light));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_light);
            } else {
                contentTxt.setTextColor(ContextCompat.getColor(a, R.color.colorTextContent_dark));
                actionBtn.setTextColor(ContextCompat.getColor(a, R.color.colorTextTitle_dark));
                snackbarLayout.setBackgroundResource(R.color.colorRoot_dark);
            }

            snackbar.show();
        }
    }

    public interface SnackbarContainer {
        View getSnackbarContainer();
    }
}
