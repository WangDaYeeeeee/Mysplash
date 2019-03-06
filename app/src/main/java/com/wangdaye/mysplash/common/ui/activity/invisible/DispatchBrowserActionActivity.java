package com.wangdaye.mysplash.common.ui.activity.invisible;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.wangdaye.mysplash.common.utils.LogUtils;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

import java.util.List;

/**
 * Dispatch browser action activity.
 *
 * This activity is used to get and dispatch all of view action from intent.
 *
 * */

public class DispatchBrowserActionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        readIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readIntent(intent);
    }

    private void readIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            List<String> keyList = uri.getPathSegments();
            LogUtils.log("URI", "uri: "+uri);
            LogUtils.log("URI", "scheme: "+uri.getScheme());
            LogUtils.log("URI", "host: "+uri.getHost());
            LogUtils.log("URI", "port: "+uri.getPort());
            LogUtils.log("URI", "path: "+uri.getPath());
            LogUtils.log("URI", "queryString: "+uri.getQuery());
            LogUtils.log("URI", "queryParameter: "+uri.getQueryParameter("key"));
            try {
                if (keyList.size() == 0) {
                    IntentHelper.startMainActivity(this);
                } else if (keyList.get(0).equals("photos")) {
                    IntentHelper.startPhotoActivity(this, keyList.get(1));
                } else if (keyList.get(0).equals("collections")) {
                    if (keyList.get(1).equals("curated")) {
                        IntentHelper.startCollectionActivity(this, keyList.get(2));
                    } else {
                        IntentHelper.startCollectionActivity(this, keyList.get(1));
                    }
                } else if (keyList.get(0).charAt(0) == '@') {
                    IntentHelper.startUserActivity(this, keyList.get(0).replaceFirst("@", ""));
                } else {
                    showErrorInformation(intent);
                }
            } catch (Exception ignored) {
                showErrorInformation(intent);
            }
        }
        finish();
    }

    private void showErrorInformation(Intent intent) {
        Toast.makeText(
                this,
                "Error - Browser Action Uri\n" + intent.getDataString(),
                Toast.LENGTH_LONG).show();
    }
}
