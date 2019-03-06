package com.wangdaye.mysplash.common.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.di.component.DaggerServiceComponent;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.json.AccessToken;
import com.wangdaye.mysplash.common.network.json.Me;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.service.UserService;
import com.wangdaye.mysplash.common.download.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Auth manager.
 *
 * A manager class to manage the authorization information.
 * */

public class AuthManager {

    private static AuthManager instance;

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
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
        DaggerServiceComponent.builder().build().inject(this);

        SharedPreferences sharedPreferences = Mysplash.getInstance()
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

            NotificationHelper.showSnackbar(Mysplash.getInstance().getString(R.string.feedback_please_login));
        }
    }

    public void logout() {
        service.cancel();

        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE).edit();
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
        this.state = State.FREE;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onLogout();
        }
    }

    // HTTP request.

    public void requestPersonalProfile() {
        if (authorized) {
            service.cancel();
            if (me != null) {
                requestUser(me.username);
            } else {
                state = State.LOADING;
                service.requestMeProfile(new RequestMeCallback());
            }
        }
    }

    private void requestUser(String username) {
        state = State.LOADING;
        service.requestUserProfile(username, new RequestUserCallback());
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

    public void updateUser(User u) {
        this.user = u;
    }

    public void writeAccessToken(AccessToken token) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ACCESS_TOKEN, token.access_token);
        editor.apply();

        access_token = token.access_token;
        authorized = true;

        for (int i = 0; i < listenerList.size(); i ++) {
            listenerList.get(i).onUpdateAccessToken();
        }
    }

    public void writeMe(Me me) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE).edit();
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
    }

    private void writeUser(User user) {
        SharedPreferences.Editor editor = Mysplash.getInstance()
                .getSharedPreferences(PREFERENCE_MYSPLASH_AUTHORIZE_MANAGER, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USERNAME, user.username);
        editor.putString(KEY_FIRST_NAME, user.first_name);
        editor.putString(KEY_LAST_NAME, user.last_name);
        editor.putString(KEY_AVATAR_PATH, user.profile_image.large);
        editor.putInt(KEY_NUMERIC_ID, user.numeric_id);
        editor.apply();

        this.user = user;

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

    private class RequestMeCallback extends Callback<Me> {

        @Override
        public void onSucceed(Me me) {
            writeMe(me);
            requestUser(me.username);
        }

        @Override
        public void onFailed() {
            state = State.FREE;
            for (int i = 0; i < listenerList.size(); i ++) {
                listenerList.get(i).onUpdateFailed();
            }
        }
    }

    private class RequestUserCallback extends Callback<User> {

        @Override
        public void onSucceed(User user) {
            state = State.FREE;
            writeUser(user);
        }

        @Override
        public void onFailed() {
            state = State.FREE;
            for (int i = 0; i < listenerList.size(); i ++) {
                listenerList.get(i).onUpdateFailed();
            }
        }
    }
}

