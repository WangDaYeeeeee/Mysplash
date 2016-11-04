package com.wangdaye.mysplash._common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.AccessToken;
import com.wangdaye.mysplash._common.data.entity.Me;
import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.ui.dialog.RateLimitDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Authorize manager.
 * */

public class AuthManager
        implements UserService.OnRequestMeProfileListener, UserService.OnRequestUserProfileListener {
    // widget
    private List<OnAuthDataChangedListener> listenerList;

    // data
    private Me me;
    private User user;
    private UserService service;

    private String access_token;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String avatar_path;
    private boolean authorized;

    private static final String PREFERENCE_NAME = "mysplash_authorize_manager";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR_PATH = "avatar_path";

    private int state;
    public static final int FREEDOM_STATE = 0;
    public static final int LOADING_ME_STATE = 1;
    public static final int LOADING_USER_STATE = 2;

    private static final String KEY_VERSION = "version";
    private static final int VERSION_CODE = 4;

    private static final String KEY_BUILD_TYPE = "build_type";
    private static final int BUILD_TYPE_BETA = 1;
    private static final int BUILD_TYPE_RELEASE = 2;
    private final int CORRECT_BUILD_TYPE = BUILD_TYPE_BETA;
    // TODO: Need change APPLICATION_ID & SECRET when build type is change.

    /** <br> life cycle. */

    private AuthManager() {
        updateVersion();

        SharedPreferences sharedPreferences = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        this.listenerList = new ArrayList<>();

        this.access_token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        this.authorized = !TextUtils.isEmpty(access_token);

        if (authorized) {
            this.username = sharedPreferences.getString(KEY_USERNAME, null);
            this.first_name = sharedPreferences.getString(KEY_FIRST_NAME, null);
            this.last_name = sharedPreferences.getString(KEY_LAST_NAME, null);
            this.email = sharedPreferences.getString(KEY_EMAIL, null);
            this.avatar_path = sharedPreferences.getString(KEY_AVATAR_PATH, null);
        }

        this.me = null;
        this.user = null;
        this.service = UserService.getService();

        this.state = FREEDOM_STATE;
    }

    private void updateVersion() {
        int versionNow = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_VERSION, 0);
        int buildTypeNow = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_BUILD_TYPE, BUILD_TYPE_RELEASE);
        String token = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ACCESS_TOKEN, null);

        if ((versionNow < VERSION_CODE || buildTypeNow != CORRECT_BUILD_TYPE)
                && !TextUtils.isEmpty(token)) {

            SharedPreferences.Editor editor = Mysplash.getInstance()
                    .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
            editor.putInt(KEY_VERSION, VERSION_CODE);
            editor.putInt(KEY_BUILD_TYPE, CORRECT_BUILD_TYPE);
            editor.putString(KEY_ACCESS_TOKEN, null);
            editor.putString(KEY_USERNAME, null);
            editor.putString(KEY_FIRST_NAME, null);
            editor.putString(KEY_LAST_NAME, null);
            editor.putString(KEY_EMAIL, null);
            editor.putString(KEY_AVATAR_PATH, null);
            editor.apply();
        }
    }

    /** <br> data. */

    // refresh.

    public void refreshPersonalProfile() {
        if (authorized) {
            service.cancel();
            state = LOADING_ME_STATE;
            service.requestMeProfile(this);
        }
    }

    public void cancelRequest() {
        service.cancel();
    }

    // getter.

    public Me getMe() {
        return me;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarPath() {
        return avatar_path;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public int getState() {
        return state;
    }

    // setter.

    public void writeAccessToken(AccessToken token) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ACCESS_TOKEN, token.access_token);
        editor.apply();

        access_token = token.access_token;
        authorized = true;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onWriteAccessToken();
        }
    }

    public void writeUserInfo(Me me) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USERNAME, me.username);
        editor.putString(KEY_FIRST_NAME, me.first_name);
        editor.putString(KEY_LAST_NAME, me.last_name);
        editor.putString(KEY_EMAIL, me.email);
        editor.apply();

        this.me = me;
        username = me.username;
        first_name = me.first_name;
        last_name = me.last_name;
        email = me.email;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onWriteUserInfo();
        }
    }

    public void writeUserInfo(User user) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USERNAME, user.username);
        editor.putString(KEY_FIRST_NAME, user.first_name);
        editor.putString(KEY_LAST_NAME, user.last_name);
        editor.putString(KEY_AVATAR_PATH, user.profile_image.large);
        editor.apply();

        this.user = user;
        avatar_path = user.profile_image.large;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onWriteAvatarPath();
        }
    }

    public void logout() {
        service.cancel();

        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ACCESS_TOKEN, null);
        editor.putString(KEY_USERNAME, null);
        editor.putString(KEY_FIRST_NAME, null);
        editor.putString(KEY_LAST_NAME, null);
        editor.putString(KEY_EMAIL, null);
        editor.putString(KEY_AVATAR_PATH, null);
        editor.apply();

        this.access_token = null;
        this.username = null;
        this.first_name = null;
        this.last_name = null;
        this.email = null;
        this.avatar_path = null;
        this.authorized = false;

        this.me = null;
        this.user = null;
        this.state = FREEDOM_STATE;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onLogout();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsManager.checkAndPublishShortcuts(Mysplash.getInstance());
        }
    }

    /** singleton. */

    private static AuthManager instance;

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public static AuthManager reBuild() {
        instance = new AuthManager();
        return instance;
    }

    /** <br> interface. */

    // on auth data changed listener.

    public interface OnAuthDataChangedListener {
        void onWriteAccessToken();
        void onWriteUserInfo();
        void onWriteAvatarPath();
        void onLogout();
    }

    public void addOnWriteDataListener(OnAuthDataChangedListener l) {
        listenerList.add(l);
    }

    public void removeOnWriteDataListener(OnAuthDataChangedListener l) {
        listenerList.remove(l);
    }

    // on request me profile listener.

    @Override
    public void onRequestMeProfileSuccess(Call<Me> call, Response<Me> response) {
        if (response.isSuccessful() && response.body() != null) {
            state = LOADING_USER_STATE;
            writeUserInfo(response.body());
            service.requestUserProfile(response.body().username, this);
        } else {
            service.requestMeProfile(this);
        }
    }

    @Override
    public void onRequestMeProfileFailed(Call<Me> call, Throwable t) {
        service.requestMeProfile(this);
    }

    // on request user profile listener.

    @Override
    public void onRequestUserProfileSuccess(Call<User> call, Response<User> response) {
        if (response.isSuccessful() && response.body() != null) {
            state = FREEDOM_STATE;
            writeUserInfo(response.body());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutsManager.checkAndPublishShortcuts(Mysplash.getInstance());
            }
        } else if (Integer.parseInt(response.headers().get("X-Ratelimit-Remaining")) < 0) {
            RateLimitDialog dialog = new RateLimitDialog();
            dialog.show(
                    Mysplash.getInstance().getTopActivity().getFragmentManager(),
                    null);
        } else {
            service.requestUserProfile(me.username, this);
        }
    }

    @Override
    public void onRequestUserProfileFailed(Call<User> call, Throwable t) {
        service.requestUserProfile(me.username, this);
    }
}