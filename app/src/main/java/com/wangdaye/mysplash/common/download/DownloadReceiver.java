package com.wangdaye.mysplash.common.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wangdaye.mysplash.common.download.imp.AbstractDownloaderService;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

/**
 * Download receiver.
 * */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case AbstractDownloaderService.ACTION_DOWNLOAD_COMPLETE:
                DownloadHelper.getInstance(context).completeMission(
                        context,
                        intent.getLongExtra(
                                AbstractDownloaderService.EXTRA_DOWNLOAD_ID,
                                -1
                        )
                );
                break;

            case AbstractDownloaderService.ACTION_NOTIFICATION_CLICKED:
                IntentHelper.startDownloadManageActivityFromNotification(context);
                break;
        }
    }
}
