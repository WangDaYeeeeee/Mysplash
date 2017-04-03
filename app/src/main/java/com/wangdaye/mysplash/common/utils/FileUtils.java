package com.wangdaye.mysplash.common.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.table.DownloadMissionEntity;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

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
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_no_sd_card),
                    Snackbar.LENGTH_SHORT);
            return false;
        }
        File dirFile1 = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!dirFile1.exists()) {
            if (!dirFile1.mkdir()) {
                NotificationHelper.showSnackbar(
                        c.getString(R.string.feedback_create_file_failed) + " -1",
                        Snackbar.LENGTH_SHORT);
                return false;
            }
        }
        File dirFile2 = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
        if (!dirFile2.exists()) {
            if (!dirFile2.mkdir()) {
                NotificationHelper.showSnackbar(
                        c.getString(R.string.feedback_create_file_failed) + " -2",
                        Snackbar.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    public static boolean isPhotoExists(Context c, String title) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_no_sd_card),
                    Snackbar.LENGTH_SHORT);
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
            NotificationHelper.showSnackbar(
                    c.getString(R.string.feedback_no_sd_card),
                    Snackbar.LENGTH_SHORT);
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

    public static String uriToFilePath(Context context, @NonNull Uri uri) {
        String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver()
                    .query(
                            uri,
                            new String[] {MediaStore.Images.ImageColumns.DATA},
                            null, null, null );
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Uri filePathToUri(Context context, @NonNull String filePath) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[] {filePath.substring(filePath.lastIndexOf("/") + 1)},
                null);

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
