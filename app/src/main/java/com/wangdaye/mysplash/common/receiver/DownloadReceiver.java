package com.wangdaye.mysplash.common.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

/**
 * Download receiver.
 *
 * Receive broadcasts from {@link DownloadManager}.
 *
 * */

public class DownloadReceiver extends BroadcastReceiver {

    /** <br> life cycle. */

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                long missionId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadHelper.downloadFinish(context, missionId);
                break;

            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                IntentHelper.startDownloadManageActivityFromNotification(context);
                break;
        }
    }
}
