package com.wangdaye.mysplash.common.basic.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
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
import com.wangdaye.mysplash.common.ui.widget.windowInsets.ApplyWindowInsetsLayout;
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
    @Nullable private ApplyWindowInsetsLayout applyWindowInsetsLayout;

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

        LanguageUtils.setLanguage(this);
        DisplayUtils.setWindowTop(this);
    }

    public boolean hasTranslucentNavigationBar() {
        return false;
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bindApplyWindowInsetsLayout();
    }

    protected void bindApplyWindowInsetsLayout() {
        applyWindowInsetsLayout = new ApplyWindowInsetsLayout(this);
        applyWindowInsetsLayout.setOnApplyWindowInsetsListener(() -> {
            DisplayUtils.setStatusBarStyle(this, false);
            DisplayUtils.setNavigationBarStyle(
                    this, false, hasTranslucentNavigationBar());
        });

        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        ViewGroup contentView = (ViewGroup) decorView.getChildAt(0);

        decorView.removeView(contentView);
        applyWindowInsetsLayout.addView(contentView);

        decorView.addView(applyWindowInsetsLayout, 0);
    }

    @Nullable
    protected ApplyWindowInsetsLayout getApplyWindowInsetsLayout() {
        return applyWindowInsetsLayout;
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

    // interface.

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
