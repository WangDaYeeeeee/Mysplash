package com.wangdaye.mysplash.common.basic.activity;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common.basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.LanguageUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Mysplash activity.
 *
 * The basic activity class for Mysplash.
 *
 * */

public abstract class MysplashActivity extends AppCompatActivity
        implements HasSupportFragmentInjector {

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

    private boolean foreground;

    private List<MysplashDialogFragment> dialogList = new ArrayList<>();
    private List<MysplashPopupWindow> popupList = new ArrayList<>();

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Mysplash.getInstance().addActivity(this);
        } else {
            Mysplash.getInstance().addActivityToFirstPosition(this);
        }

        setTheme();
        LanguageUtils.setLanguage(this);
        DisplayUtils.setWindowTop(this);
        if (!operateStatusBarBySelf()) {
            DisplayUtils.setStatusBarStyle(this, false);
        }
        if (hasTranslucentNavigationBar()) {
            DisplayUtils.setNavigationBarStyle(this, false, hasTranslucentNavigationBar());
        }
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        if (ThemeManager.getInstance(this).isDayNightSwitchTime(this)) {
            ThemeManager.getInstance(this)
                    .setLightTheme(this, !ThemeManager.getInstance(this).isLightTheme());
            Mysplash.getInstance().dispatchRecreate();
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
    }

    protected void setTheme() {
        // do nothing.
    }

    /**
     * If return true, child class will be responsible for the operation of the status bar.
     * Otherwise, MysplashActivity class will deal with it.
     * */
    protected boolean operateStatusBarBySelf() {
        return false;
    }

    public boolean hasTranslucentNavigationBar() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (dialogList.size() > 0) {
            // has dialogs. --> dismiss the dialog which on the top of task.
            dialogList.get(dialogList.size() - 1).dismiss();
        } else if (popupList.size() > 0) {
            // has popup windows.
            popupList.get(popupList.size() - 1).dismiss();
        } else {
            // give the back pressed action to child class.
            handleBackPressed();
        }
    }

    /**
     * Consume the back pressed action.
     * */
    public abstract void handleBackPressed();

    /**
     * This method can make list view back to the top.
     * */
    protected abstract void backToTop();

    public abstract void finishSelf(boolean backPressed);

    @Override
    public void finish() {
        super.finish();
        Mysplash.getInstance().removeActivity(this);
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        Mysplash.getInstance().removeActivity(this);
    }

    /**
     * Provide the container layout of snack bar. Include dialogs in this activity.
     *
     * @return The container of snack bar.
     * */
    public CoordinatorLayout provideSnackbarContainer() {
        if (dialogList.size() > 0) {
            // has dialogs. --> return the top dialog's snack bar container.
            return dialogList.get(dialogList.size() - 1).getSnackbarContainer();
        } else {
            // return the snack bar container of activity.
            return getSnackbarContainer();
        }
    }

    /**
     * Get the CoordinatorLayout as a container of snack bar in layout of activity or fragments.
     *
     * @return The container of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();

    // save instance state.

    public boolean isForeground() {
        return foreground;
    }

    public List<MysplashDialogFragment> getDialogList() {
        return dialogList;
    }

    public List<MysplashPopupWindow> getPopupList() {
        return popupList;
    }

    // coordinate swipe back view.

    protected boolean isTheLowestLevel() {
        return Mysplash.getInstance().getActivityCount() == 1;
    }

    // update data.

    public void updatePhoto(@NonNull Photo photo, Mysplash.MessageType type) {
        // do nothing.
    }

    public void updateUser(@NonNull User user, Mysplash.MessageType type) {
        // do nothing.
    }

    public void updateCollection(@NonNull Collection collection, Mysplash.MessageType type) {
        // do nothing.
    }

    // interface.

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
