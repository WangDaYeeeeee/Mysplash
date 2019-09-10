package com.wangdaye.common.base.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.SharedElementCallback;

import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.base.dialog.MysplashDialogFragment;
import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.common.ui.transition.sharedElement.SharedElementTransition;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.windowInsets.ApplyWindowInsetsLayout;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.LanguageUtils;
import com.wangdaye.common.utils.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash activity.
 *
 * The basic activity class for Mysplash.
 *
 * */

public abstract class MysplashActivity extends AppCompatActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @Nullable private ApplyWindowInsetsLayout applyWindowInsetsLayout;
    private Rect windowInsets;

    private boolean foreground;

    private List<MysplashDialogFragment> dialogList = new ArrayList<>();
    private List<MysplashPopupWindow> popupList = new ArrayList<>();

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerSharedElementTransitionCallback();
        }

        super.onCreate(savedInstanceState);
        MysplashApplication.getInstance().addActivity(this);

        LanguageUtils.setLanguage(this);
        DisplayUtils.setWindowTop(this);

        windowInsets = new Rect(0, 0, 0, 0);
    }

    public boolean hasTranslucentNavigationBar() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerSharedElementTransitionCallback() {
        setEnterSharedElementCallback(new SharedElementCallback() {

            @Override
            public void onSharedElementStart(List<String> sharedElementNames,
                                             List<View> sharedElements,
                                             List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                setExtraPropertiesForViews(sharedElementNames, sharedElements, true);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                setExtraPropertiesForViews(sharedElementNames, sharedElements, false);
            }

            private void setExtraPropertiesForViews(List<String> sharedElementNames,
                                                    List<View> sharedElements,
                                                    boolean start) {
                Intent intent = getIntent();
                for (int i = 0; i < sharedElementNames.size(); i++) {
                    String name = sharedElementNames.get(i);
                    if (intent.hasExtra(name)) {
                        SharedElementTransition.setExtraPropertiesForView(
                                sharedElements.get(i),
                                intent.getBundleExtra(name),
                                start
                        );
                    }
                }
            }
        });
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        if (ThemeManager.getInstance(this).isDayNightSwitchTime(this)) {
            ThemeManager.getInstance(this)
                    .setLightTheme(this, !ThemeManager.getInstance(this).isLightTheme());
            MysplashApplication.getInstance().dispatchRecreate();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bindApplyWindowInsetsLayout();
    }

    protected void bindApplyWindowInsetsLayout() {
        applyWindowInsetsLayout = new ApplyWindowInsetsLayout(this);
        applyWindowInsetsLayout.setOnApplyWindowInsetsListener(windowInsets -> {
            MysplashApplication.getInstance().setWindowInsets(
                    windowInsets.left, windowInsets.top, windowInsets.right, windowInsets.bottom);
            setWindowInsets(
                    windowInsets.left, windowInsets.top, windowInsets.right, windowInsets.bottom);

            initSystemBar();
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

    protected void initSystemBar() {
        DisplayUtils.setStatusBarStyle(this, false);
        DisplayUtils.setNavigationBarStyle(this, false,
                hasTranslucentNavigationBar());
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
        MysplashApplication.getInstance().removeActivity(this);
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
        MysplashApplication.getInstance().removeActivity(this);
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        MysplashApplication.getInstance().removeActivity(this);
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

    // window insets.

    public void setWindowInsets(int left, int top, int right, int bottom) {
        if (left != windowInsets.left || top != windowInsets.top
                || right != windowInsets.right || bottom != windowInsets.bottom) {
            windowInsets.set(left, top, right, bottom);
        }
    }

    public Rect getWindowInsets() {
        return windowInsets;
    }

    // coordinate swipe back view.

    protected boolean isTheLowestLevel() {
        return MysplashApplication.getInstance().getActivityCount() == 1;
    }

    @Override
    public MysplashActivity provideActivity() {
        return this;
    }
}
