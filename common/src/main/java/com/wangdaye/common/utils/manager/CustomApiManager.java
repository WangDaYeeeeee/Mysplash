package com.wangdaye.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.wangdaye.common.BuildConfig;
import com.wangdaye.common.base.application.MysplashApplication;

/**
 * Custom API manager.
 *
 * This manager class can manage the custom API key and secret.
 *
 * */

public class CustomApiManager {

    private volatile static CustomApiManager instance;

    public static CustomApiManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CustomApiManager.class) {
                if (instance == null) {
                    instance = new CustomApiManager(context);
                }
            }
        }
        return instance;
    }

    private String customApiKey;
    private String customApiSecret;

    private static final String PREFERENCE_MYSPLASH_API_MANAGER = "mysplash_api_manager";
    private static final String KEY_CUSTOM_API_KEY = "custom_api_key";
    private static final String KEY_CUSTOM_API_SECRET = "custom_api_secret";

    private CustomApiManager(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_MYSPLASH_API_MANAGER,
                Context.MODE_PRIVATE
        );
        this.customApiKey = sharedPreferences.getString(KEY_CUSTOM_API_KEY, null);
        this.customApiSecret = sharedPreferences.getString(KEY_CUSTOM_API_SECRET, null);
    }

    public String getAppId(Context c, boolean auth) {
        if (MysplashApplication.isDebug(c)) {
            return BuildConfig.APP_ID_BETA;
        } else if (TextUtils.isEmpty(getCustomApiKey())
                || TextUtils.isEmpty(getCustomApiSecret())) {
            if (auth) {
                return BuildConfig.APP_ID_RELEASE;
            } else {
                return BuildConfig.APP_ID_RELEASE_UNAUTH;
            }
        } else {
            return getCustomApiKey();
        }
    }

    public String getSecret(Context c) {
        if (MysplashApplication.isDebug(c)) {
            return BuildConfig.SECRET_BETA;
        } else if (TextUtils.isEmpty(getCustomApiKey())
                || TextUtils.isEmpty(getCustomApiSecret())) {
            return BuildConfig.SECRET_RELEASE;
        } else {
            return getCustomApiSecret();
        }
    }

    public String getCustomApiKey() {
        return customApiKey;
    }

    public String getCustomApiSecret() {
        return customApiSecret;
    }

    public boolean setCustomApi(Context context, String key, String secret) {
        if (TextUtils.isEmpty(customApiKey) && TextUtils.isEmpty(key)
                && TextUtils.isEmpty(customApiSecret) && TextUtils.isEmpty(secret)) {
            return false;
        } else if (!TextUtils.equals(customApiKey, key)
                || !TextUtils.equals(customApiSecret, secret)) {
            // write.
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    PREFERENCE_MYSPLASH_API_MANAGER, Context.MODE_PRIVATE).edit();
            editor.putString(KEY_CUSTOM_API_KEY, key);
            editor.putString(KEY_CUSTOM_API_SECRET, secret);
            editor.apply();

            AuthManager.getInstance().logout();
            return true;
        }
        return false;
    }
}
