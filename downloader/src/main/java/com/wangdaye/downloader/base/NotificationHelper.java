package com.wangdaye.downloader.base;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.wangdaye.common.R;
import com.wangdaye.base.DownloadTask;

public class NotificationHelper extends com.wangdaye.common.utils.helper.NotificationHelper {

    private static final String NOTIFICATION_GROUP_KEY = "mysplash_download_result_notification";
    private static final String PREFERENCE_NOTIFICATION = "notification";
    private static final String KEY_NOTIFICATION_ID = "notification_id";

    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1001;
    public static final int NOTIFICATION_DOWNLOADING_ID = 99999;

    public static final String CHANNEL_ID_DOWNLOAD = "channel_download";
    private static final String CHANNEL_ID_NOTIFICATION = "channel_notification";

    // feedback.

    public static void sendDownloadPhotoSuccessNotification(Context c, Intent intent, DownloadTask task) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createNotificationChannel(c, manager);
            manager.notify(
                    getNotificationId(c),
                    buildSingleNotification(
                            c,
                            intent,
                            "Photo",
                            task.getNotificationTitle(),
                            true,
                            true
                    )
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.notify(
                        NOTIFICATION_GROUP_SUMMARY_ID,
                        buildGroupSummaryNotification(c, intent, true, true)
                );
            }
        }
    }

    public static void sendDownloadCollectionSuccessNotification(Context c, Intent intent, DownloadTask task) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createNotificationChannel(c, manager);
            manager.notify(
                    getNotificationId(c),
                    buildSingleNotification(
                            c,
                            intent,
                            "Collection",
                            task.getNotificationTitle(),
                            false,
                            true
                    )
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.notify(
                        NOTIFICATION_GROUP_SUMMARY_ID,
                        buildGroupSummaryNotification(c, intent, false, true)
                );
            }
        }
    }

    public static void sendDownloadPhotoFailedNotification(Context c, Intent intent, DownloadTask task) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createNotificationChannel(c, manager);
            manager.notify(
                    getNotificationId(c),
                    buildSingleNotification(
                            c,
                            intent,
                            "Photo",
                            task.getNotificationTitle(),
                            true,
                            false
                    )
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.notify(
                        NOTIFICATION_GROUP_SUMMARY_ID,
                        buildGroupSummaryNotification(c, intent, true, false)
                );
            }
        }
    }

    public static void sendDownloadCollectionFailedNotification(Context c, Intent intent, DownloadTask task) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createNotificationChannel(c, manager);
            manager.notify(
                    getNotificationId(c),
                    buildSingleNotification(
                            c,
                            intent,
                            "Collection",
                            task.getNotificationTitle(),
                            false,
                            false
                    )
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.notify(
                        NOTIFICATION_GROUP_SUMMARY_ID,
                        buildGroupSummaryNotification(c, intent, false, false)
                );
            }
        }
    }

    private static Notification buildSingleNotification(Context c, Intent intent,
                                                        String subText, String contentText,
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, CHANNEL_ID_NOTIFICATION)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(title)
                .setSubText(subText)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(buildPendingIntent(c, intent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setGroup(NOTIFICATION_GROUP_KEY);
        }
        return builder.build();
    }

    private static Notification buildGroupSummaryNotification(Context c, Intent intent, boolean photo, boolean succeed) {
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
        return new NotificationCompat.Builder(c, CHANNEL_ID_NOTIFICATION)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(title)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setContentIntent(buildPendingIntent(c, intent))
                .setAutoCancel(true)
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

    // downloading.

    public static void sendDownloadingNotification(Context context, Notification notification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            createDownloadChannel(context, manager);
            manager.notify(NOTIFICATION_DOWNLOADING_ID, notification);
        }
    }

    public static void removeDownloadingNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(NOTIFICATION_DOWNLOADING_ID);
        }
    }

    // builder.

    public static void createDownloadChannel(Context c, @NonNull NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_DOWNLOAD,
                    getNotificationChannelName(c, CHANNEL_ID_DOWNLOAD),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(true);
            channel.setSound(null, null);
            channel.setLightColor(ContextCompat.getColor(c, R.color.colorNotification));
            manager.createNotificationChannel(channel);
        }
    }

    private static void createNotificationChannel(Context c, @NonNull NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_NOTIFICATION,
                    getNotificationChannelName(c, CHANNEL_ID_NOTIFICATION),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(true);
            channel.setSound(null, null);
            channel.setLightColor(ContextCompat.getColor(c, R.color.colorPrimary_dark));
            manager.createNotificationChannel(channel);
        }
    }

    private static String getNotificationChannelName(Context context, String channelId) {
        switch (channelId) {
            case CHANNEL_ID_DOWNLOAD:
                return context.getString(R.string.app_name) + " " + context.getString(R.string.action_download);

            case CHANNEL_ID_NOTIFICATION:
                return context.getString(R.string.app_name) + " " + context.getString(R.string.action_notification);

            default:
                return null;
        }
    }

    public static PendingIntent buildPendingIntent(Context c, Intent intent) {
        return PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
