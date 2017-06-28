package com.wangdaye.mysplash.common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.ButterKnife;

/**
 * Collection type popup window.
 *
 * This popup window is used to select the type of collections.
 *
 * */

public class CollectionTypePopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnCollectionTypeChangedListener listener;

    private String[] names;
    private String[] values;
    private String valueNow;

    public CollectionTypePopupWindow(Context c, View anchor, String valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, String valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_collection_type, null);
        setContentView(v);

        initData(c, valueNow);
        initWidget();
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initData(Context c, String valueNow) {
        names = c.getResources().getStringArray(R.array.collection_types);
        values = c.getResources().getStringArray(R.array.collection_type_values);
        this.valueNow = valueNow;
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_collection_type_all).setOnClickListener(this);
        v.findViewById(R.id.popup_collection_type_curated).setOnClickListener(this);
        v.findViewById(R.id.popup_collection_type_featured).setOnClickListener(this);

        TextView allTxt = ButterKnife.findById(v, R.id.popup_collection_type_allTxt);
        DisplayUtils.setTypeface(v.getContext(), allTxt);
        allTxt.setText(names[0]);
        allTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));

        TextView curatedTxt = ButterKnife.findById(v, R.id.popup_collection_type_curatedTxt);
        DisplayUtils.setTypeface(v.getContext(), curatedTxt);
        curatedTxt.setText(names[1]);
        curatedTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));

        TextView featuredTxt = (TextView) v.findViewById(R.id.popup_collection_type_featuredTxt);
        DisplayUtils.setTypeface(v.getContext(), featuredTxt);
        featuredTxt.setText(names[2]);
        featuredTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));

        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_collection_type_allIcon),
                R.drawable.ic_mountain_light, R.drawable.ic_mountain_dark);
        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_collection_type_curatedIcon),
                R.drawable.ic_star_outline_light, R.drawable.ic_star_outline_dark);
        ThemeManager.setImageResource(
                (ImageView) v.findViewById(R.id.popup_collection_type_featuredIcon),
                R.drawable.ic_feature_light, R.drawable.ic_feature_dark);
    }

    // interface.

    // on collection type changed listener.

    public interface OnCollectionTypeChangedListener {
        void CollectionTypeChange(String typeValue);
    }

    public void setOnCollectionTypeChangedListener(OnCollectionTypeChangedListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        String newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_collection_type_all:
                newValue = values[0];
                break;

            case R.id.popup_collection_type_curated:
                newValue = values[1];
                break;

            case R.id.popup_collection_type_featured:
                newValue = values[2];
                break;
        }

        if (!newValue.equals(valueNow) && listener != null) {
            listener.CollectionTypeChange(newValue);
            dismiss();
        }
    }
}
