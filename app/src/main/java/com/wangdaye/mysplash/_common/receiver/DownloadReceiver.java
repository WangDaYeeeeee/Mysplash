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
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.DownloadMissionEntity;
import com.wangdaye.mysplash._common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash._common.utils.NotificationUtils;

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
                if (!downloadCollectionSuccess(context, missionId)) {
                    downloadPhotoSuccess(context, missionId);
                    DownloadHelper.getInstance(context).refreshEntityList();
                }
                break;

            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                Intent manageActivity = new Intent(context, DownloadManageActivity.class);
                manageActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                manageActivity.putExtra(DownloadManageActivity.EXTRA_NOTIFICATION, true);
                context.startActivity(manageActivity);
                break;
        }
    }

    /** <br> feedback. */

    private void downloadPhotoSuccess(Context c, long missionId) {
        for (int i = 0; i < DownloadHelper.getInstance(c).entityList.size(); i ++) {
            if (DownloadHelper.getInstance(c).entityList.get(i).missionId == missionId) {
                DownloadMissionEntity entity = DownloadHelper.getInstance(c).entityList.get(i);

                c.sendBroadcast(
                        new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory()
                                        + Mysplash.DOWNLOAD_PATH
                                        + entity.photoId + Mysplash.DOWNLOAD_FORMAT)));

                switch (entity.downloadType) {
                    case DownloadHelper.DOWNLOAD_TYPE:
                        if (Mysplash.getInstance() != null
                                && Mysplash.getInstance().getTopActivity() != null) {
                            simpleDownloadSuccess(entity);
                        }
                        break;

                    case DownloadHelper.SHARE_TYPE: {
                        if (Mysplash.getInstance() != null
                                && Mysplash.getInstance().getTopActivity() != null) {
                            shareDownloadSuccess(entity);
                        }
                        break;
                    }

                    case DownloadHelper.WALLPAPER_TYPE: {
                        if (Mysplash.getInstance() != null
                                && Mysplash.getInstance().getTopActivity() != null) {
                            wallpaperDownloadSuccess(entity);
                        }
                        break;
                    }
                }
                DownloadHelper.getInstance(c).downloadPhotoSuccess(i);
                return;
            }
        }
    }

    private void simpleDownloadSuccess(DownloadMissionEntity entity) {
        Context c = Mysplash.getInstance().getTopActivity();
        NotificationUtils.showActionSnackbar(
                c.getString(R.string.feedback_download_photo_success),
                c.getString(R.string.check),
                Snackbar.LENGTH_LONG,
                new OnCheckPhotoListener(Mysplash.getInstance().getTopActivity(), entity.photoId));
    }

    private void shareDownloadSuccess(DownloadMissionEntity entity) {
        Uri file = Uri.parse("file://"
                + Environment.getExternalStorageDirectory()
                + Mysplash.DOWNLOAD_PATH
                + entity.photoId + Mysplash.DOWNLOAD_FORMAT);
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
                + entity.photoId + Mysplash.DOWNLOAD_FORMAT);
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

    private boolean downloadCollectionSuccess(Context c, long missionId) {
        for (int i = 0; i < DownloadHelper.getInstance(c).collectionIdList.size(); i ++) {
            if (DownloadHelper.getInstance(c).collectionIdList.get(i) == missionId) {
                if (Mysplash.getInstance() != null
                        && Mysplash.getInstance().getTopActivity() != null) {
                    NotificationUtils.showActionSnackbar(
                            c.getString(R.string.feedback_download_collection_success),
                            c.getString(R.string.check),
                            Snackbar.LENGTH_LONG,
                            new OnCheckCollectionListener(c, missionId));
                }
                return true;
            }
        }
        return false;
    }

    /** <br> interface. */

    private class OnCheckPhotoListener implements View.OnClickListener {
        // widget
        private Context c;

        // data
        private String id;

        // life cycle.
        OnCheckPhotoListener(Context c, String id) {
            this.c = c;
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()
                    + Mysplash.DOWNLOAD_PATH
                    + id + Mysplash.DOWNLOAD_FORMAT);
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
        private long id;

        // life cycle.
        OnCheckCollectionListener(Context c, long id) {
            this.c = c;
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
            Cursor cursor = ((DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE)).query(query);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()
                    + Mysplash.DOWNLOAD_PATH
                    + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    + ".zip");
            intent.setDataAndType(uri, "application/x-zip-compressed");

            c.startActivity(
                    Intent.createChooser(
                            intent,
                            c.getString(R.string.check)));
        }
    }
}
