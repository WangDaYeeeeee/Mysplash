package com.wangdaye.common.base.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.wangdaye.base.i.Downloadable;

/**
 * Read write activity.
 *
 * This activity can request read and write permission.
 *
 * */

public abstract class ReadWriteActivity extends MysplashActivity {

    @Nullable private RequestPermissionCallback callback;
    public interface RequestPermissionCallback {
        void onGranted(Downloadable downloadable);
        void onDenied(Downloadable downloadable);
    }

    @Nullable private Downloadable downloadable;

    private static final int REQUEST_CODE = 1;

    public void requestReadWritePermission(Downloadable downloadable,
                                           RequestPermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onRequestReadWritePermissionResult(true);
        } else {
            this.downloadable = downloadable;
            this.callback = callback;
            requestPermission();
        }
    }

    private void onRequestReadWritePermissionResult(boolean granted) {
        if (callback != null) {
            if (granted) {
                callback.onGranted(downloadable);
            } else {
                callback.onDenied(downloadable);
            }
            callback = null;
            downloadable = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, REQUEST_CODE
            );
        } else {
            onRequestReadWritePermissionResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            if (grantResult[i] != PackageManager.PERMISSION_GRANTED) {
                onRequestReadWritePermissionResult(false);
                return;
            }
        }
        onRequestReadWritePermissionResult(true);
    }
}
