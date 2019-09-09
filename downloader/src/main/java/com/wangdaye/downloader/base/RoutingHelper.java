package com.wangdaye.downloader.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wangdaye.downloader.R;
import com.wangdaye.downloader.activity.DownloadManageActivity;

public class RoutingHelper extends com.wangdaye.common.utils.helper.RoutingHelper {

    public static void startDownloadManageActivity(Activity a) {
        ARouter.getInstance()
                .build(DownloadManageActivity.DOWNLOAD_MANAGE_ACTIVITY)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    public static void startDownloadManageActivityFromNotification(Context context) {
        ARouter.getInstance()
                .build(DownloadManageActivity.DOWNLOAD_MANAGE_ACTIVITY)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withBoolean(DownloadManageActivity.KEY_DOWNLOAD_MANAGE_ACTIVITY_FROM_NOTIFICATION, true)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(context);
    }

    public static Intent getDownloadManageActivityIntentForNotification() {
        return new Intent(DownloadManageActivity.ACTION_DOWNLOAD_MANAGE_ACTIVITY)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(DownloadManageActivity.KEY_DOWNLOAD_MANAGE_ACTIVITY_FROM_NOTIFICATION, true);
    }

    public static Intent getDownloadManageActivityIntentForShortcut() {
        return new Intent(DownloadManageActivity.ACTION_DOWNLOAD_MANAGE_ACTIVITY);
    }
}
