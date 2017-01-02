package com.wangdaye.mysplash._common.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.database.DownloadMissionEntity;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;

/**
 * Download receiver.
 * */

public class DownloadReceiver extends BroadcastReceiver {

    /** <br> life cycle. */

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                long missionId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(missionId);
                Cursor cursor = ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).query(query);

                int cursorCount = cursor.getCount();
                if (cursorCount > 0) {
                    cursor.moveToFirst();
                    if (DownloadHelper.isMissionSuccess(cursor)) {
                        DownloadMissionEntity entity = DatabaseHelper.getInstance(context).readDownloadEntity(missionId);
                        if (entity != null && entity.downloadType != DownloadHelper.COLLECTION_TYPE) {
                            downloadPhotoSuccess(context, missionId);
                        } else {
                            downloadCollectionSuccess(context, missionId);
                        }
                    }
                } else {
                    DatabaseHelper.getInstance(context).deleteDownloadEntity(missionId);
                }
                break;

            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                IntentHelper.startDownloadManageActivityFromNotification(context);
                break;
        }
    }

    /** <br> feedback. */

    private void downloadPhotoSuccess(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(missionId);
        if (entity != null) {
            c.sendBroadcast(
                    new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory()
                                    + Mysplash.DOWNLOAD_PATH
                                    + entity.title + Mysplash.DOWNLOAD_PHOTO_FORMAT)));

            if (Mysplash.getInstance() != null
                    && Mysplash.getInstance().getTopActivity() != null) {
                switch (entity.downloadType) {
                    case DownloadHelper.DOWNLOAD_TYPE:
                        simpleDownloadSuccess(entity);
                        break;

                    case DownloadHelper.SHARE_TYPE: {
                        shareDownloadSuccess(entity);
                        break;
                    }

                    case DownloadHelper.WALLPAPER_TYPE: {
                        wallpaperDownloadSuccess(entity);
                        break;
                    }
                }
            } else {
                NotificationUtils.sendDownloadPhotoSuccessNotification(c, entity);
            }
            DatabaseHelper.getInstance(c).deleteDownloadEntity(missionId);
        }
    }

    private void simpleDownloadSuccess(DownloadMissionEntity entity) {
        Context c = Mysplash.getInstance().getTopActivity();
        NotificationUtils.showActionSnackbar(
                c.getString(R.string.feedback_download_photo_success),
                c.getString(R.string.check),
                Snackbar.LENGTH_LONG,
                new OnCheckPhotoListener(Mysplash.getInstance().getTopActivity(), entity.title));
    }

    private void shareDownloadSuccess(DownloadMissionEntity entity) {
        Uri file = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + entity.title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
        Intent action = new Intent(Intent.ACTION_SEND);
        action.putExtra(Intent.EXTRA_STREAM, file);
        action.setType("image/*");
        Mysplash.getInstance()
                .getTopActivity()
                .startActivity(
                        Intent.createChooser(
                                action,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_share_app)));
    }

    private void wallpaperDownloadSuccess(DownloadMissionEntity entity) {
        Uri file = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + entity.title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
        Intent action = new Intent(Intent.ACTION_ATTACH_DATA);
        action.setDataAndType(file, "image/jpg");
        action.putExtra("mimeType", "image/jpg");
        Mysplash.getInstance()
                .getTopActivity()
                .startActivity(
                        Intent.createChooser(
                                action,
                                Mysplash.getInstance()
                                        .getString(R.string.feedback_choose_wallpaper_app)));
    }

    private void downloadCollectionSuccess(Context c, long missionId) {
        DownloadMissionEntity entity = DatabaseHelper.getInstance(c).readDownloadEntity(missionId);
        if (entity != null) {
            if (Mysplash.getInstance() != null
                    && Mysplash.getInstance().getTopActivity() != null) {
                NotificationUtils.showActionSnackbar(
                        c.getString(R.string.feedback_download_collection_success),
                        c.getString(R.string.check),
                        Snackbar.LENGTH_LONG,
                        new OnCheckCollectionListener(c, entity.title));
            } else {
                NotificationUtils.sendDownloadCollectionSuccessNotification(c, entity);
            }
        }
        DatabaseHelper.getInstance(c).deleteDownloadEntity(missionId);
    }

    /** <br> interface. */

    private class OnCheckPhotoListener implements View.OnClickListener {
        // widget
        private Context c;

        // data
        private String title;

        // life cycle.
        OnCheckPhotoListener(Context c, String title) {
            this.c = c;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()
                    + Mysplash.DOWNLOAD_PATH
                    + title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
            intent.setDataAndType(uri, "image/jpg");

            c.startActivity(
                    Intent.createChooser(
                            intent,
                            c.getString(R.string.check)));
        }
    }

    private class OnCheckCollectionListener implements View.OnClickListener {
        // widget
        private Context c;

        // data
        private String title;

        // life cycle.
        OnCheckCollectionListener(Context c, String title) {
            this.c = c;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()
                    + Mysplash.DOWNLOAD_PATH
                    + title
                    + ".zip");
            intent.setDataAndType(uri, "application/x-zip-compressed");

            c.startActivity(
                    Intent.createChooser(
                            intent,
                            c.getString(R.string.check)));
        }
    }
}
