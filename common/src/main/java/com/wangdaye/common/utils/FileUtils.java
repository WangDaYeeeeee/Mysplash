package com.wangdaye.common.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.common.R;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.helper.NotificationHelper;

import java.io.File;

/**
 * File utils.
 *
 * An utils class that makes operations of file easier.
 *
 * */

public class FileUtils {

    public static boolean createDownloadPath(Context c) {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // edit file by ContentResolver and MediaStore in android 10.
            return true;
        }*/

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_no_sd_card));
            return false;
        }

        File dirFile1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!dirFile1.exists()) {
            if (!dirFile1.mkdirs()) {
                NotificationHelper.showSnackbar(c.getString(R.string.feedback_create_file_failed) + " -1");
                return false;
            }
        }

        File dirFile2 = new File(dirFile1, DownloadTask.DOWNLOAD_SUB_PATH);
        if (!dirFile2.exists()) {
            if (!dirFile2.mkdirs()) {
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
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!f.exists()) {
            return false;
        }
        f = new File(f, DownloadTask.DOWNLOAD_SUB_PATH);
        if (!f.exists()) {
            return false;
        }
        return new File(f, title + DownloadTask.DOWNLOAD_PHOTO_FORMAT).exists();
    }

    public static boolean isCollectionExists(Context c, String title) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(c.getString(R.string.feedback_no_sd_card));
            return false;
        }
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!f.exists()) {
            return false;
        }
        f = new File(f, DownloadTask.DOWNLOAD_SUB_PATH);
        if (!f.exists()) {
            return false;
        }
        return new File(f, title + DownloadTask.DOWNLOAD_COLLECTION_FORMAT).exists();
    }

    public static boolean deleteFile(Context context, DownloadTask task) {
        File f = new File(task.getFilePath(context));
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

    public static String getFileProviderAuthorities() {
        return MysplashApplication.getInstance().getPackageName() + ".fileprovider";
    }
}
