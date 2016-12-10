package com.wangdaye.mysplash._common.ui._basic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash Activity
 * */

public abstract class MysplashActivity extends AppCompatActivity {
    // widget
    private Bundle bundle;
    private List<MysplashDialogFragment> dialogList;
    private List<MysplashPopupWindow> popupList;

    // data.
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mysplash.getInstance().addActivity(this);
        setTheme();
        LanguageUtils.setLanguage(this);
        DisplayUtils.setWindowTop(this);
        if (needSetStatusBarTextDark()) {
            DisplayUtils.setStatusBarTextDark(this);
        }

        this.bundle = savedInstanceState;
        this.dialogList = new ArrayList<>();
        this.popupList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mysplash.getInstance().removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (dialogList.size() > 0) {
            dialogList.get(dialogList.size() - 1).dismiss();
            dialogList.remove(dialogList.size() - 1);
        } else if (popupList.size() > 0) {
            popupList.get(popupList.size() - 1).dismiss();
            popupList.remove(popupList.size() - 1);
        } else {
            handleBackPressed();
        }
    }

    public abstract void finishActivity(int dir);

    public abstract View getSnackbarContainer();

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

    protected abstract void backToTop();

    protected abstract boolean needSetStatusBarTextDark();

    /** <br> interface. */

    public View provideSnackbarContainer() {
        if (dialogList.size() > 0) {
            return dialogList.get(dialogList.size() - 1).getSnackbarContainer();
        } else {
            return getSnackbarContainer();
        }
    }
}
