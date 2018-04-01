package com.wangdaye.mysplash.common._basic.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.fragment.MysplashDialogFragment;
import com.wangdaye.mysplash.common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash activity.
 *
 * The basic activity class for Mysplash.
 *
 * */

public abstract class MysplashActivity extends AppCompatActivity {

    private Bundle bundle; // saved instance state.
    private boolean started; // flag of onStart() method.

    @Nullable
    private Bitmap background; // background bitmap for swipe back.

    private List<MysplashDialogFragment> dialogList = new ArrayList<>();
    private List<MysplashPopupWindow> popupList = new ArrayList<>();

    /**
     * Base saved state fragment.
     *
     * This fragment is used to save large data when application is saving state instance.
     *
     * */
    public abstract static class BaseSavedStateFragment extends Fragment {

        private boolean landscape;
        private Bitmap background;

        private static final String FRAGMENT_TAG = "SavedStateFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // set this flag as true, otherwise the fragment will be rebuild when activity restart.
            setRetainInstance(true);
        }

        public void saveData(MysplashActivity a) {
            setLandscape(DisplayUtils.isLandscape(a));
            setBackground(a.getBackground());
            Fragment f = a.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (f != null) {
                a.getSupportFragmentManager().beginTransaction().remove(f).commit();
            }
            a.getSupportFragmentManager()
                    .beginTransaction()
                    .add(this, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }

        @Nullable
        public static BaseSavedStateFragment getData(MysplashActivity a) {
            Fragment f = a.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (f != null) {
                a.getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();

                BaseSavedStateFragment sf = (BaseSavedStateFragment) f;
                if (DisplayUtils.isLandscape(a) == sf.isLandscape()) {
                    a.setBackground(sf.getBackground());
                }
                return sf;
            } else {
                return null;
            }
        }

        public Bitmap getBackground() {
            return background;
        }

        public void setBackground(Bitmap background) {
            this.background = background;
        }

        public boolean isLandscape() {
            return landscape;
        }

        public void setLandscape(boolean landscape) {
            this.landscape = landscape;
        }
    }

    // life cycle.

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        this.bundle = savedInstanceState;
        this.started = false;

        setBackground(Mysplash.getInstance().getBackgroundBitmap());
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
    }

    // control style.

    protected abstract void setTheme();

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

    // handle back press action.

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

    // manage snack bar container.

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

    public Bundle getBundle() {
        return bundle;
    }

    public void setStarted() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public List<MysplashDialogFragment> getDialogList() {
        return dialogList;
    }

    public List<MysplashPopupWindow> getPopupList() {
        return popupList;
    }

    // coordinate swipe back view.

    public void sendBackground() {
        Mysplash.getInstance().setBackgroundBitmap(getWindow().getDecorView());
    }

    @Nullable
    protected Bitmap getBackground() {
        return background;
    }

    protected void setBackground(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            background = Bitmap.createBitmap(bitmap);
        }
    }

    protected boolean isTheLowestLevel() {
        return Mysplash.getInstance().getActivityCount() == 1;
    }
}
