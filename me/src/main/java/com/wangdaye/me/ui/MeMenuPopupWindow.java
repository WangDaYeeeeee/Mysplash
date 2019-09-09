package com.wangdaye.me.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;

import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.me.R;
import com.wangdaye.me.activity.MeActivity;

/**
 * Me menu popup window.
 *
 * This popup window is used to show the menu in
 * {@link MeActivity}.
 *
 * */

public class MeMenuPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnSelectItemListener listener;

    public static final int ITEM_SUBMIT = 1;
    public static final int ITEM_PORTFOLIO = 2;
    public static final int ITEM_SHARE = 3;
    @IntDef({ITEM_SUBMIT, ITEM_PORTFOLIO, ITEM_SHARE})
    private @interface MenuItemRule {}

    public MeMenuPopupWindow(Context c, View anchor) {
        super(c);
        this.initialize(c, anchor);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_me_menu, null);
        setContentView(v);

        initWidget();
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_me_menu_submit).setOnClickListener(this);
        v.findViewById(R.id.popup_me_menu_portfolio).setOnClickListener(this);
        v.findViewById(R.id.popup_me_menu_share).setOnClickListener(this);
    }

    // interface.

    // on select item listener.

    public interface OnSelectItemListener {
        void onSelectItem(@MenuItemRule int id);
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.popup_me_menu_submit) {
            if (listener != null) {
                listener.onSelectItem(ITEM_SUBMIT);
            }
        } else if (i == R.id.popup_me_menu_portfolio) {
            if (listener != null) {
                listener.onSelectItem(ITEM_PORTFOLIO);
            }
        } else if (i == R.id.popup_me_menu_share) {
            if (listener != null) {
                listener.onSelectItem(ITEM_SHARE);
            }
        }
        dismiss();
    }
}