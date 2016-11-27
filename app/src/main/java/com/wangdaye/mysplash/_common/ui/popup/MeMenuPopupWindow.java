package com.wangdaye.mysplash._common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Me menu popup window.
 * */

public class MeMenuPopupWindow extends PopupWindow
        implements View.OnClickListener {
    // widget
    private OnSelectItemListener listener;

    // data
    public static final int ITEM_SUBMIT = 1;
    public static final int ITEM_PORTFOLIO = 2;
    public static final int ITEM_SHARE = 3;

    /** <br> life cycle. */

    public MeMenuPopupWindow(Context c, View anchor) {
        super(c);
        this.initialize(c, anchor);
        Mysplash.getInstance().setActivityInBackstage(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Mysplash.getInstance().setActivityInBackstage(false);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_me_menu, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initWidget();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
        showAsDropDown(anchor, anchor.getMeasuredWidth(), 0, Gravity.CENTER);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_me_menu_submit).setOnClickListener(this);
        v.findViewById(R.id.popup_me_menu_portfolio).setOnClickListener(this);
        v.findViewById(R.id.popup_me_menu_share).setOnClickListener(this);

        TextView submitTxt = (TextView) v.findViewById(R.id.popup_me_menu_submitTxt);
        DisplayUtils.setTypeface(v.getContext(), submitTxt);

        TextView portfolioTxt = (TextView) v.findViewById(R.id.popup_me_menu_portfolioTxt);
        DisplayUtils.setTypeface(v.getContext(), portfolioTxt);

        TextView shareTxt = (TextView) v.findViewById(R.id.popup_me_menu_shareTxt);
        DisplayUtils.setTypeface(v.getContext(), shareTxt);

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_me_menu_submitIcon)).setImageResource(R.drawable.ic_plus_light);
            ((ImageView) v.findViewById(R.id.popup_me_menu_portfolioIcon)).setImageResource(R.drawable.ic_earth_light);
            ((ImageView) v.findViewById(R.id.popup_me_menu_shareIcon)).setImageResource(R.drawable.ic_share_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_me_menu_submitIcon)).setImageResource(R.drawable.ic_plus_dark);
            ((ImageView) v.findViewById(R.id.popup_me_menu_portfolioIcon)).setImageResource(R.drawable.ic_earth_dark);
            ((ImageView) v.findViewById(R.id.popup_me_menu_shareIcon)).setImageResource(R.drawable.ic_share_dark);
        }
    }

    /** <br> interface. */

    public interface OnSelectItemListener {
        void onSelectItem(int id);
    }

    public void setOnSelectItemListener(OnSelectItemListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_me_menu_submit:
                if (listener != null) {
                    listener.onSelectItem(ITEM_SUBMIT);
                }
                break;

            case R.id.popup_me_menu_portfolio:
                if (listener != null) {
                    listener.onSelectItem(ITEM_PORTFOLIO);
                }
                break;

            case R.id.popup_me_menu_share:
                if (listener != null) {
                    listener.onSelectItem(ITEM_SHARE);
                }
                break;
        }
        dismiss();
    }
}