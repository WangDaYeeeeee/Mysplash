package com.wangdaye.common.utils.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.wangdaye.common.R;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.common.utils.FileUtils;

import java.io.File;
import java.util.List;

public class RoutingHelper {

    public static void startCheckPhotoActivity(Context c, String title) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileUtils.filePathToUri(
                    c,
                    Environment.getExternalStorageDirectory()
                            + DownloadTask.DOWNLOAD_PATH
                            + title
                            + DownloadTask.DOWNLOAD_PHOTO_FORMAT
            );
            intent.setDataAndType(uri, "image/jpg");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    c.getString(R.string.check)
            );
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            File file = new File(
                    Environment.getExternalStorageDirectory()
                            + DownloadTask.DOWNLOAD_PATH
                            + title
                            + DownloadTask.DOWNLOAD_PHOTO_FORMAT
            );
            Uri uri = FileProvider.getUriForFile(c, FileUtils.getFileProviderAuthorities(), file);
            intent.setDataAndType(uri, "image/jpg");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    c.getString(R.string.check)
            );
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        }
    }

    public static void startCheckCollectionActivity(Context c, String title) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(
                    "file://"
                            + Environment.getExternalStorageDirectory()
                            + DownloadTask.DOWNLOAD_PATH
                            + title
                            + DownloadTask.DOWNLOAD_PHOTO_FORMAT
            );
            intent.setDataAndType(uri, "application/x-zip-compressed");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    c.getString(R.string.check)
            );
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(
                    Environment.getExternalStorageDirectory()
                            + DownloadTask.DOWNLOAD_PATH
                            + title
                            + DownloadTask.DOWNLOAD_PHOTO_FORMAT
            );
            Uri uri = FileProvider.getUriForFile(c, FileUtils.getFileProviderAuthorities(), file);
            intent.setDataAndType(uri, "application/x-zip-compressed");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent chooser = Intent.createChooser(
                    intent,
                    c.getString(R.string.check)
            );
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chooser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            c.startActivity(chooser);
        }
    }

    public static void startWebActivity(Context c, String url) {
        String packageName = "com.android.chrome";
        Intent browserIntent = new Intent();
        browserIntent.setPackage(packageName);
        List<ResolveInfo> activitiesList = c.getPackageManager()
                .queryIntentActivities(browserIntent, -1);
        if (activitiesList.size() > 0) {
            CustomTabHelper.startCustomTabActivity(c, url);
        } else {
            c.startActivity(getWebActivityIntent(url));
        }
    }

    public static void backToHome(MysplashActivity a) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        a.startActivity(intent);
    }

    public static Intent getWebActivityIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
}
