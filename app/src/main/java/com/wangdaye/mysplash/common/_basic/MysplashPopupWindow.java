package com.wangdaye.mysplash.common._basic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Mysplash popup window.
 *
 * Basic PopupWindow class for Mysplash.
 *
 * */

public class MysplashPopupWindow extends PopupWindow {

    /** <br> life cycle. */

    public MysplashPopupWindow(Context context) {
        super(context);
        final MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            activity.getPopupList().add(this);
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    activity.getPopupList().remove(MysplashPopupWindow.this);
                }
            });
        }
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
    }

    /** <br> UI. */

    public void setContentView(View contentView) {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        super.setContentView(contentView);
    }

    @SuppressLint("RtlHardcoded")
    protected void show(View anchor, int offsetX, int offsetY) {
        int[] locations = new int[2];
        anchor.getLocationOnScreen(locations);
        locations[0] += offsetX;
        locations[1] += offsetY;

        int[] screenSizes = new int[] {
                anchor.getContext().getResources().getDisplayMetrics().widthPixels,
                anchor.getContext().getResources().getDisplayMetrics().heightPixels};
        int[] triggers = new int[] {
                (int) (0.5 * screenSizes[0]),
                screenSizes[1] - getContentView().getMeasuredHeight()
                        - DisplayUtils.getNavigationBarHeight(anchor.getResources())
                        - 3 * anchor.getResources().getDimensionPixelSize(R.dimen.normal_margin)};

        if (locations[0] <= triggers[0]) {
            if (locations[1] <= triggers[1]) {
                setAnimationStyle(R.style.MysplashPopupWindowAnimation_Top_Left);
                showAsDropDown(anchor, offsetX, offsetY, Gravity.LEFT);
            } else {
                setAnimationStyle(R.style.MysplashPopupWindowAnimation_Bottom_Left);
                showAsDropDown(
                        anchor,
                        offsetX,
                        offsetY - anchor.getMeasuredHeight() - getContentView().getMeasuredHeight(),
                        Gravity.LEFT);
            }
        } else {
            if (locations[1] <= triggers[1]) {
                setAnimationStyle(R.style.MysplashPopupWindowAnimation_Top_Right);
                showAsDropDown(anchor, offsetX, offsetY, Gravity.RIGHT);
            } else {
                setAnimationStyle(R.style.MysplashPopupWindowAnimation_Bottom_Right);
                showAsDropDown(
                        anchor,
                        offsetX,
                        offsetY - anchor.getMeasuredHeight() - getContentView().getMeasuredHeight(),
                        Gravity.RIGHT);
            }
        }
    }
}
