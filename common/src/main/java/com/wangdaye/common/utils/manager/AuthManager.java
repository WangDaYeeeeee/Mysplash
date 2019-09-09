package com.wangdaye.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.base.unsplash.AccessToken;
import com.wangdaye.base.unsplash.Me;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.di.component.DaggerNetworkServiceComponent;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.utils.helper.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Auth manager.
 *
 * A manager class to manage the authorization information.
 * */

public class AuthManager {

    private static class Inner{
        private static AuthManager instance = new AuthManager();
    }

    public static AuthManager getInstance() {
        return Inner.instance;
    }

    private List<OnAuthDataChangedListener> listenerList;

    private Me me;
    private User user;
    @Inject UserService service;

    private String access_token;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private boolean authorized;

    private UserCollectionsManager collectionsManager; // cache of user's collections.
    private UserNotificationManager notificationManager; // manage user's notification feeds.

    private static final String PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER = "mysplash_authorize_manager";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR_PATH = "avatar_path";
    private static final String KEY_NUMERIC_ID = "numeric_id";

    private State state;
    public enum State {
        FREE, LOADING
    }

    // if version code is increased, the user need to login again.
    private static final String KEY_VERSION = "version";
    private static final int VERSION_CODE = 8;

    private AuthManager() {
        DaggerNetworkServiceComponent.create().inject(this);

        SharedPreferences sharedPreferences = MysplashApplication.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE);

        updateVersion(sharedPreferences);

        this.listenerList = new ArrayList<>();

        this.access_token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        this.authorized = !TextUtils.isEmpty(access_token);

        if (authorized) {
            this.username = sharedPreferences.getString(KEY_USERNAME, null);
            this.first_name = sharedPreferences.getString(KEY_FIRST_NAME, null);
            this.last_name = sharedPreferences.getString(KEY_LAST_NAME, null);
            this.email = sharedPreferences.getString(KEY_EMAIL, null);
        }
        this.collectionsManager = new UserCollectionsManager();
        this.notificationManager = new UserNotificationManager();

        this.me = null;
        this.user = null;

        this.state = State.FREE;
    }

    private void updateVersion(SharedPreferences sharedPreferences) {
        int versionNow = sharedPreferences.getInt(KEY_VERSION, 0);

        if (versionNow < VERSION_CODE) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_VERSION, VERSION_CODE);
            editor.putString(KEY_ACCESS_TOKEN, null);
            editor.putString(KEY_USERNAME, null);
            editor.putString(KEY_FIRST_NAME, null);
            editor.putString(KEY_LAST_NAME, null);
            editor.putString(KEY_EMAIL, null);
            editor.putString(KEY_AVATAR_PATH, null);
            editor.apply();

            NotificationHelper.showSnackbar(MysplashApplication.getInstance().getString(R.string.feedback_please_login));
        }
    }

    public void logout() {
        service.cancel();

        SharedPreferences.Editor editor = MysplashApplication.getInstance()
                .getSharedPreferences(
                        PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER,
                        Context.MODE_PRIVATE
                ).edit();
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
        this.authorized = false;
        this.collectionsManager.clearCollections();
        this.notificationManager.clearNotifications(true);

        this.me = null;
        this.user = null;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onLogout();
        }

        state = State.FREE;
    }

    // HTTP request.

    public void requestPersonalProfile() {
        if (!authorized) {
            return;
        }

        state = State.LOADING;

        service.cancel();
        service.requestAuthUser(me, new UserService.RequestAuthUserObserver() {
            @Override
            public void onRequestMeCompleted(@NonNull Me me) {
                updateMe(me);
            }

            @Override
            public void onRequestUserCompleted(@NonNull User user) {
                updateUser(user);
            }

            @Override
            public void onComplete() {
                state = State.FREE;
                for (int i = 0; i < listenerList.size(); i ++) {
                    listenerList.get(i).onUpdateUser();
                }
            }

            @Override
            public void onError() {
                state = State.FREE;
                for (int i = 0; i < listenerList.size(); i ++) {
                    listenerList.get(i).onUpdateFailed();
                }
            }
        });
    }

    // getter.

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

    public boolean isAuthorized() {
        return authorized;
    }

    public State getState() {
        return state;
    }

    public UserCollectionsManager getCollectionsManager() {
        return collectionsManager;
    }

    public UserNotificationManager getNotificationManager() {
        return notificationManager;
    }

    // setter.

    public void updateAccessToken(AccessToken token) {
        SharedPreferences.Editor editor = MysplashApplication.getInstance()
                .getSharedPreferences(
                        PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER,
                        Context.MODE_PRIVATE
                ).edit();
        editor.putString(KEY_ACCESS_TOKEN, token.access_token);
        editor.apply();

        access_token = token.access_token;
        authorized = true;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onUpdateAccessToken();
        }
    }

    public void updateMe(Me me) {
        this.me = me;

        MysplashApplication.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_USERNAME, me.username)
                .putString(KEY_FIRST_NAME, me.first_name)
                .putString(KEY_LAST_NAME, me.last_name)
                .putString(KEY_EMAIL, me.email)
                .apply();

        username = me.username;
        first_name = me.first_name;
        last_name = me.last_name;
        email = me.email;
    }

    public void updateUser(User user) {
        this.user = user;

        MysplashApplication.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_USERNAME, user.username)
                .putString(KEY_FIRST_NAME, user.first_name)
                .putString(KEY_LAST_NAME, user.last_name)
                .putString(KEY_AVATAR_PATH, user.profile_image.large)
                .putInt(KEY_NUMERIC_ID, user.numeric_id)
                .apply();

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onUpdateUser();
        }
    }

    // interface.

    // on auth data changed listener.

    public interface OnAuthDataChangedListener {
        void onUpdateAccessToken();
        void onUpdateUser();
        void onUpdateFailed();
        void onLogout();
    }

    public void addOnWriteDataListener(OnAuthDataChangedListener l) {
        listenerList.add(l);
    }

    public void removeOnWriteDataListener(OnAuthDataChangedListener l) {
        listenerList.remove(l);
    }
}

