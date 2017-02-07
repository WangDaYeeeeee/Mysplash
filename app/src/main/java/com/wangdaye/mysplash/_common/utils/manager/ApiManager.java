package com.wangdaye.mysplash._common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;

/**
 * Api manager.
 * */

public class ApiManager {
    // widget
    private SharedPreferences sharedPreferences;

    // data
    private static final String PREFERENCE_MYSPLASH_API_MANAGER = "mysplash_api_manager";
    private static final String KEY_CUSTOM_API_KEY = "custom_api_key";
    private static final String KEY_CUSTOM_API_SECRET = "custom_api_secret";

    /** <br> life cycle. */

    private ApiManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(
                PREFERENCE_MYSPLASH_API_MANAGER, Context.MODE_PRIVATE);
    }

    /** <br> data. */

    public String[] readCustomApi() {
        return new String[] {
                sharedPreferences.getString(KEY_CUSTOM_API_KEY, null),
                sharedPreferences.getString(KEY_CUSTOM_API_SECRET, null)};
    }

    public void writeCustomApi(String key, String secret) {
        if ((TextUtils.isEmpty(Mysplash.getInstance().getCustomApiKey()) && TextUtils.isEmpty(key))
                && (TextUtils.isEmpty(Mysplash.getInstance().getCustomApiSecret()) && TextUtils.isEmpty(secret))) {
            // do nothing.
        } else if (!TextUtils.equals(Mysplash.getInstance().getCustomApiKey(), key)
                || !TextUtils.equals(Mysplash.getInstance().getCustomApiSecret(), secret)) {
            // write.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CUSTOM_API_KEY, key);
            editor.putString(KEY_CUSTOM_API_SECRET, secret);
            editor.apply();

            // re login.
            AuthManager.getInstance().logout();
            Snackbar.make(
                    Mysplash.getInstance().getTopActivity().getSnackbarContainer(),
                    Mysplash.getInstance().getString(R.string.feedback_please_login),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void destroy() {
        sharedPreferences = null;
        instance = null;
    }

    /** singleton. */

    private static ApiManager instance;

    public static ApiManager getInstance(Context context) {
        synchronized (ApiManager.class) {
            if (instance == null) {
                instance = new ApiManager(context);
            }
        }
        return instance;
    }
}
