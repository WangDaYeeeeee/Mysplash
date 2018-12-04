package com.wangdaye.mysplash.common.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.wangdaye.mysplash.common.data.service.downloader.AndroidDownloaderService;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

/**
 * Download receiver.
 *
 * Receive broadcasts from {@link DownloadManager}.
 *
 * */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getAction())) {
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    long missionId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    new AndroidDownloaderService(context).downloadFinish(context, missionId);
                    break;

                case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                    IntentHelper.startDownloadManageActivityFromNotification(context);
                    break;
            }
        }
    }
}
