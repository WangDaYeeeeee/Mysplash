package com.wangdaye.mysplash.common.utils.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.basic.MysplashActivity;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThreadManager;
import com.wangdaye.mysplash.common.basic.FlagRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * notification helper.
 * */

public class NotificationHelper {
    // widget
    private Context context;
    private NotificationManager manager;

    // data
    private List<String> titleList;
    private long soFar;
    private long total;
    private int iconCode;
    private long refreshTime;

    public static final int REFRESH_RATE = 150;

    public static final int DOWNLOAD_NOTIFICATION_ID = 7777;

    private static final String NOTIFICATION_GROUP_KEY = "mysplash_download_result_notification";
    private static final String PREFERENCE_NOTIFICATION = "notification";
    private static final String KEY_NOTIFICATION_ID = "notification_id";
    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1001;

    /** <br> singleton. */

    private static NotificationHelper instance;

    public static NotificationHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationHelper.class) {
                if (instance == null) {
                    instance = new NotificationHelper(context);
                }
            }
        }
        return instance;
    }

    /** <br> life cycle. */

    private NotificationHelper(Context context) {
        this.context = context;
        this.refreshNotification.setRunning(false);
        this.manager = (NotificationManager) Mysplash.getInstance()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        this.titleList = new ArrayList<>();
        this.refreshTime = this.soFar = this.total = this.iconCode = 0;
    }

    private void destroy() {
        context = null;
        manager = null;
        instance = null;
        refreshNotification.setRunning(false);
    }

    /** <br> notification. */

    // progress.

    @Nullable
    public Notification sendDownloadProgressNotification(String title, long deltaSoFar, long deltaTotal,
                                                         boolean titleChanged, boolean remove) {
        boolean timeChanged = Math.abs(System.currentTimeMillis() - refreshTime) >= REFRESH_RATE;

        soFar = Math.max(0, soFar + deltaSoFar);
        total = Math.max(0, total + deltaTotal);

        if (titleChanged && titleList != null) {
            if (remove) {
                titleList.remove(title);
            } else {
                boolean newTitle = true;
                for (int i = 0; i < titleList.size(); i ++) {
                    if (titleList.get(i).equals(title)) {
                        newTitle = false;
                        break;
                    }
                }
                if (newTitle) {
                    titleList.add(title);
                }
            }
        }
        if (titleChanged || timeChanged) {
            if (timeChanged) {
                refreshTime = System.currentTimeMillis();
            }
            int downloadingCount = titleList == null ? 0 : titleList.size();
            if (downloadingCount > 0 && context != null) {
                Notification notification = buildProgressNotification(context, timeChanged);
                manager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
                if (!remove && downloadingCount == 1 && !refreshNotification.isRunning()) {
                    refreshNotification.setRunning(true);
                    ThreadManager.getInstance().execute(refreshNotification);
                }
                return notification;
            }
            if (remove && downloadingCount <= 0) {
                destroy();
            }
        }
        return null;
    }

    private Notification buildProgressNotification(Context c, boolean timeChanged) {
        int process = (int) (100.0 * soFar / total);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setSmallIcon(getIconResId(timeChanged));
        builder.setContentTitle(c.getString(R.string.feedback_downloading));
        builder.setSubText(process + "%");
        builder.setProgress(100, process, false);

        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        for (int i = 0; i < titleList.size(); i ++) {
            inbox.addLine(titleList.get(i));
        }
        builder.setStyle(inbox);

        Intent intent = IntentHelper.getDownloadManageActivityIntent(c);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    private int getIconResId(boolean timeChanged) {
        if (iconCode == 0) {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_1;
        } else if (iconCode == 1) {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_2;
        } else if (iconCode == 2) {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_3;
        } else if (iconCode == 3) {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_4;
        } else if (iconCode == 4) {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_5;
        } else {
            setIconCode(timeChanged);
            return R.drawable.ic_notification_progress_6;
        }
    }

    private void setIconCode(boolean timeChanged) {
        if (timeChanged) {
            if (iconCode < 5) {
                iconCode ++;
            } else {
                iconCode = 0;
            }
        }
    }

    // feedback.

    public static void sendDownloadPhotoSuccessNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(c, "Photo", entity.getRealTitle(), true, true));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c, true, true));
        }
    }

    public static void sendDownloadCollectionSuccessNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(
                                c, "Collection", entity.getRealTitle(), false, true));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c, false, true));
        }
    }

    public static void sendDownloadPhotoFailedNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(c, "Photo", entity.getRealTitle(), true, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c, true, false));
        }
    }

    public static void sendDownloadCollectionFailedNotification(Context c, DownloadMissionEntity entity) {
        NotificationManagerCompat.from(c)
                .notify(
                        getNotificationId(c),
                        buildSingleNotification(
                                c, "Collection", entity.getRealTitle(), false, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat.from(c)
                    .notify(NOTIFICATION_GROUP_SUMMARY_ID, buildGroupSummaryNotification(c, false, false));
        }
    }

    private static Notification buildSingleNotification(Context c, String subText, String contentText,
                                                        boolean photo, boolean succeed) {
        String title;
        if (photo && succeed) {
            title = c.getString(R.string.feedback_download_photo_success);
        } else if (photo) {
            title = c.getString(R.string.feedback_download_photo_failed);
        } else if (succeed) {
            title = c.getString(R.string.feedback_download_collection_success);
        } else {
            title = c.getString(R.string.feedback_delete_collection_failed);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setSubText(subText)
                .setContentText(contentText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setGroup(NOTIFICATION_GROUP_KEY);
        }
        return builder.build();
    }

    private static Notification buildGroupSummaryNotification(Context c, boolean photo, boolean succeed) {
        String title;
        if (photo && succeed) {
            title = c.getString(R.string.feedback_download_photo_success);
        } else if (photo) {
            title = c.getString(R.string.feedback_download_photo_failed);
        } else if (succeed) {
            title = c.getString(R.string.feedback_download_collection_success);
        } else {
            title = c.getString(R.string.feedback_delete_collection_failed);
        }
        return new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .build();
    }

    private static int getNotificationId(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(
                PREFERENCE_NOTIFICATION,
                Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt(KEY_NOTIFICATION_ID, 1) + 1;
        if (id > NOTIFICATION_GROUP_SUMMARY_ID - 1) {
            id = 1;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NOTIFICATION_ID, id);
        editor.apply();

        return id;
    }

    /** <br> snackbar & toast. */

    public static void showSnackbar(String content, int duration) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        if (Mysplash.getInstance().getActivityCount() > 0) {
            View container = a.provideSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            DisplayUtils.setTypeface(a, contentTxt);

            if (Mysplash.getInstance().isLightTheme()) {
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
            MysplashActivity a = Mysplash.getInstance().getTopActivity();
            View container = a.provideSnackbarContainer();

            Snackbar snackbar = Snackbar
                    .make(container, content, duration)
                    .setAction(action, l);

            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            DisplayUtils.setTypeface(a, contentTxt);

            Button actionBtn = (Button) snackbarLayout.findViewById(R.id.snackbar_action);

            if (Mysplash.getInstance().isLightTheme()) {
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

    /** <br> inner class. */

    private FlagRunnable refreshNotification = new FlagRunnable(true) {
        @Override
        public void run() {
            while (isRunning()) {
                sendDownloadProgressNotification("", 0, 0, false, false);
                SystemClock.sleep(100);
            }
        }
    };
}
