package com.wangdaye.mysplash.common._basic;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wangdaye.mysplash.Mysplash;
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
    // widget
    private Bundle bundle; // saved instance state.
    private List<MysplashDialogFragment> dialogList;
    private List<MysplashPopupWindow> popupList;

    // data.
    private boolean started; // flag of onStart() method.

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mysplash.getInstance().addActivity(this);

        setTheme();
        LanguageUtils.setLanguage(this);
        DisplayUtils.setWindowTop(this);
        if (!operateStatusBarBySelf()) {
            DisplayUtils.initStatusBarStyle(this);
        }

        this.bundle = savedInstanceState;
        this.dialogList = new ArrayList<>();
        this.popupList = new ArrayList<>();
        this.started = false;
    }

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
            dialogList.remove(dialogList.size() - 1);
        } else if (popupList.size() > 0) {
            // has popup windows.
            popupList.get(popupList.size() - 1).dismiss();
            popupList.remove(popupList.size() - 1);
        } else {
            // give the back pressed action to child class.
            handleBackPressed();
        }
    }

    public abstract void finishActivity(int dir);

    /**
     * Get the CoordinatorLayout as a container of snack bar in layout of activity or fragments.
     *
     * @return The container of snack bar.
     * */
    public abstract CoordinatorLayout getSnackbarContainer();

    /**
     * Consume the back pressed action.
     * */
    public abstract void handleBackPressed();

    /** <br> widget. */

    public Bundle getBundle() {
        return bundle;
    }

    public List<MysplashDialogFragment> getDialogList() {
        return dialogList;
    }

    public List<MysplashPopupWindow> getPopupList() {
        return popupList;
    }

    /** <br> data. */

    public void setStarted() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    protected abstract void setTheme();

    /**
     * This method can make list view back to the top.
     * */
    protected abstract void backToTop();

    /**
     * If return true, child class will be responsible for the operation of the status bar.
     * Otherwise, MysplashActivity class will deal with it.
     * */
    protected abstract boolean operateStatusBarBySelf();

    /** <br> interface. */

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

    /** <br> inner class. */

    /**
     * Base saved state fragment.
     *
     * This fragment is used to save large data when application is saving state instance.
     *
     * */
    public abstract static class BaseSavedStateFragment extends Fragment {
        // data
        private static final String FRAGMENT_TAG = "SavedStateFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // set this flag as true, otherwise the fragment will be rebuild when activity restart.
            setRetainInstance(true);
        }

        public void saveData(MysplashActivity a) {
            a.getSupportFragmentManager()
                    .beginTransaction()
                    .add(this, FRAGMENT_TAG)
                    .commit();
        }

        public static BaseSavedStateFragment getData(MysplashActivity a) {
            Fragment f = a.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (f != null) {
                a.getSupportFragmentManager().beginTransaction().remove(f).commit();
                return (BaseSavedStateFragment) f;
            } else {
                return null;
            }
        }
    }
}
