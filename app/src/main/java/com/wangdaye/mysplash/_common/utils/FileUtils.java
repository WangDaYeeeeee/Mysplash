package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

import java.io.File;

/**
 * File utils.
 * */

public class FileUtils {

    public static boolean createDownloadPath(Context c) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationUtils.showSnackbar(
                    c.getString(R.string.feedback_no_sd_card),
                    Snackbar.LENGTH_SHORT);
            return false;
        }
        File dirFile1 = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!dirFile1.exists()) {
            if (!dirFile1.mkdir()) {
                NotificationUtils.showSnackbar(
                        c.getString(R.string.feedback_create_file_failed) + " -1",
                        Snackbar.LENGTH_SHORT);
                return false;
            }
        }
        File dirFile2 = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Mysplash");
        if (!dirFile2.exists()) {
            if (!dirFile2.mkdir()) {
                NotificationUtils.showSnackbar(
                        c.getString(R.string.feedback_create_file_failed) + " -2",
                        Snackbar.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    public static boolean isPhotoExists(Context c, String title) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            NotificationUtils.showSnackbar(
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
            NotificationUtils.showSnackbar(
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
}
