package com.wangdaye.mysplash.common.basic.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;

/**
 * Read write activity.
 *
 * This activity can request read and write permission.
 *
 * */

public abstract class ReadWriteActivity extends MysplashActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestReadWritePermission() {
        requestPermission(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestReadWritePermission(int requestCode) {
        requestPermission(requestCode);
    }

    protected void requestReadWritePermissionSucceed(int requestCode) {
        // do nothing.
    }

    protected void requestReadWritePermissionFailed(int requestCode) {
        NotificationHelper.showSnackbar(getString(R.string.feedback_need_permission));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int requestCode) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        } else {
            requestReadWritePermissionSucceed(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            if (grantResult[i] != PackageManager.PERMISSION_GRANTED) {
                requestReadWritePermissionFailed(requestCode);
                return;
            }
        }
        requestReadWritePermissionSucceed(requestCode);
    }
}
