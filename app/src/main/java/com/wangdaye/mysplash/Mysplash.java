package com.wangdaye.mysplash;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.wangdaye.common.base.application.MultiModulesApplication;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.utils.manager.ShortcutsManager;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.component.service.SettingsService;

/**
 * Mysplash.
 *
 * Application class for Mysplash.
 *
 * */

public class Mysplash extends MysplashApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        initModuleComponent(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            if (!AuthManager.getInstance().isAuthorized()) {
                ShortcutsManager.refreshShortcuts(Mysplash.this, null);
            }
            registerShortcutsUpdateExecutor();
        }

        if (ComponentFactory.getSettingsService()
                .getAutoNightMode()
                .equals(SettingsService.DAY_NIGHT_MODE_FOLLOW_SYSTEM)) {
            ThemeManager.getInstance(this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    ThemeManager.getInstance(this).isLightTheme()
                            ? AppCompatDelegate.MODE_NIGHT_NO
                            : AppCompatDelegate.MODE_NIGHT_YES
            );
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void registerShortcutsUpdateExecutor() {
        AuthManager.getInstance().addOnWriteDataListener(new AuthManager.OnAuthDataChangedListener() {
            @Override
            public void onUpdateAccessToken() {

            }

            @Override
            public void onUpdateUser() {
                ShortcutsManager.refreshShortcuts(
                        Mysplash.this, AuthManager.getInstance().getUser());
            }

            @Override
            public void onUpdateFailed() {

            }

            @Override
            public void onLogout() {
                ShortcutsManager.refreshShortcuts(Mysplash.this, null);
            }
        });
    }

    @Override
    public void initModuleComponent(Context context) {
        for (String a : ComponentFactory.moduleApplications) {
            try {
                ((MultiModulesApplication) Class.forName(a).newInstance()).initModuleComponent(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
