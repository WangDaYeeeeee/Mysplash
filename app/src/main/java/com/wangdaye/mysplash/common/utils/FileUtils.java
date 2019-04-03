package com.wangdaye.mysplash.common.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.db.DownloadMissionEntity;
import com.wangdaye.mysplash.common.download.NotificationHelper;

import java.io.File;

/**
 * File utils.
 *
 * An utils class that makes operations of file easier.
 *
 * */

public class FileUtils {

    public static boolean createDownloadPath(Context c) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_no_sd_card));
            return false;
        }
        File dirFile1 = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!dirFile1.exists()) {
            if (!dirFile1.mkdir()) {
                NotificationHelper.showSnackbar(c.getString(R.string.feedback_create_file_failed) + " -1");
                return false;
            }
        }
        File dirFile2 = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
        if (!dirFile2.exists()) {
            if (!dirFile2.mkdir()) {
                NotificationHelper.showSnackbar(c.getString(R.string.feedback_create_file_failed) + " -2");
                return false;
            }
        }
        return true;
    }

    public static boolean isPhotoExists(Context c, String title) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_no_sd_card));
            return false;
        }
        File f;
        f = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!f.exists()) {
            return false;
        }
        f = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
        if (!f.exists()) {
            return false;
        }
        f = new File(Environment.getExternalStorageDirectory().toString()
                + Mysplash.DOWNLOAD_PATH
                + title + Mysplash.DOWNLOAD_PHOTO_FORMAT);
        return f.exists();
    }

    public static boolean isCollectionExists(Context c, String title) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_no_sd_card));
            return false;
        }
        File f;
        f = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!f.exists()) {
            return false;
        }
        f = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
        if (!f.exists()) {
            return false;
        }
        f = new File(Environment.getExternalStorageDirectory().toString()
                + Mysplash.DOWNLOAD_PATH
                + title + Mysplash.DOWNLOAD_COLLECTION_FORMAT);
        return f.exists();
    }

    public static boolean deleteFile(DownloadMissionEntity entity) {
        File f = new File(entity.getFilePath());
        return f.exists() && f.delete();
    }

    @Nullable
    public static String uriToFilePath(Context context, @NonNull Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver()
                .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (index > -1) {
                path = cursor.getString(index);
            }
            cursor.close();
        }
        return path;
    }

    public static Uri filePathToUri(Context context, @NonNull String filePath) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[] {filePath.substring(filePath.lastIndexOf("/") + 1)},
                null
        );

        Uri uri = Uri.parse("file://" + filePath);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                if (index > -1) {
                    uri = ContentUris.withAppendedId(mediaUri, cursor.getLong(index));
                }
            }
            cursor.close();
        }
        return uri;
    }
}
