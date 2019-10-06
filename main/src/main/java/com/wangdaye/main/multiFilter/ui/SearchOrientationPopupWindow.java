package com.wangdaye.main.multiFilter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.main.R;

/**
 * Search orientation popup window.
 *
 * This popup window is used to select orientation.
 *
 * */

public class SearchOrientationPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnSearchOrientationChangedListener listener;

    private String[] names;
    private String[] values;
    private String valueNow;

    public SearchOrientationPopupWindow(Context c, View anchor, String valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, String valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_search_orientation, null);
        setContentView(v);

        initData(c, valueNow);
        initWidget();
        show(anchor, 0, 0);
    }

    private void initData(Context c, String valueNow) {
        names = c.getResources().getStringArray(R.array.search_orientations);
        values = c.getResources().getStringArray(R.array.search_orientation_values);
        this.valueNow = valueNow;
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_search_orientation_all).setOnClickListener(this);
        v.findViewById(R.id.popup_search_orientation_landscape).setOnClickListener(this);
        v.findViewById(R.id.popup_search_orientation_portrait).setOnClickListener(this);
        v.findViewById(R.id.popup_search_orientation_squarish).setOnClickListener(this);

        TextView allTxt = v.findViewById(R.id.popup_search_orientation_allTxt);
        allTxt.setText(v.getContext().getText(R.string.all));
        if (TextUtils.isEmpty(valueNow)) {
            allTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView landscapeTxt = v.findViewById(R.id.popup_search_orientation_landscapeTxt);
        landscapeTxt.setText(names[0]);
        if (values[0].equals(valueNow)) {
            landscapeTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView portraitTxt = v.findViewById(R.id.popup_search_orientation_portraitTxt);
        portraitTxt.setText(names[1]);
        if (values[1].equals(valueNow)) {
            portraitTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView squarishTxt = v.findViewById(R.id.popup_search_orientation_squarishTxt);
        squarishTxt.setText(names[2]);
        if (values[2].equals(valueNow)) {
            squarishTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }
    }

    // interface.

    // on search orientation changed listener.

    public interface OnSearchOrientationChangedListener {
        void onSearchOrientationChanged(String orientationValue);
    }

    public void setOnSearchOrientationChangedListener(OnSearchOrientationChangedListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        String newValue = valueNow;
        int i = view.getId();
        if (i == R.id.popup_search_orientation_all) {
            newValue = "";
        } else if (i == R.id.popup_search_orientation_landscape) {
            newValue = values[0];
        } else if (i == R.id.popup_search_orientation_portrait) {
            newValue = values[1];
        } else if (i == R.id.popup_search_orientation_squarish) {
            newValue = values[2];
        }

        if (!newValue.equals(valueNow) && listener != null) {
            listener.onSearchOrientationChanged(newValue);
            dismiss();
        }
    }
}