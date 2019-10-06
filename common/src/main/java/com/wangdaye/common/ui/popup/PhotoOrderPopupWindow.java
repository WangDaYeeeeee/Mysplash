package com.wangdaye.common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.wangdaye.common.R;
import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.common.utils.manager.ThemeManager;

/**
 * Order popup window.
 *
 * This popup window is used to select order of photos.
 *
 * */

public class PhotoOrderPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnPhotoOrderChangedListener listener;

    private String[] names;
    private String[] values;
    private String valueNow;

    @TypeRule
    private int type;

    public static final int NORMAL_TYPE = 0;
    public static final int CATEGORY_TYPE = 1;
    public static final int NO_RANDOM_TYPE = 2;
    @IntDef({NORMAL_TYPE, CATEGORY_TYPE, NO_RANDOM_TYPE})
    private @interface TypeRule {}

    public PhotoOrderPopupWindow(Context c, View anchor, String valueNow, @TypeRule int type) {
        super(c);
        this.initialize(c, anchor, valueNow, type);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, String valueNow, @TypeRule int type) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_photo_order, null);
        setContentView(v);

        initData(c, valueNow, type);
        initWidget();
        show(anchor, anchor.getMeasuredWidth(), 0);
    }

    private void initData(Context c, String valueNow, @TypeRule int type) {
        names = c.getResources().getStringArray(R.array.photo_orders);
        values = c.getResources().getStringArray(R.array.photo_order_values);
        this.valueNow = valueNow;

        this.type = type;
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_photo_order_latest).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_order_oldest).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_order_popular).setOnClickListener(this);
        v.findViewById(R.id.popup_photo_order_random).setOnClickListener(this);

        if (type == CATEGORY_TYPE) {
            v.findViewById(R.id.popup_photo_order_oldest).setVisibility(View.GONE);
            v.findViewById(R.id.popup_photo_order_popular).setVisibility(View.GONE);
        } else if (type == NO_RANDOM_TYPE) {
            v.findViewById(R.id.popup_photo_order_random).setVisibility(View.GONE);
        }

        TextView latestTxt = v.findViewById(R.id.popup_photo_order_latestTxt);
        latestTxt.setText(names[0]);
        if (valueNow.equals(values[0])) {
            latestTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView oldestTxt = v.findViewById(R.id.popup_photo_order_oldestTxt);
        oldestTxt.setText(names[1]);
        if (valueNow.equals(values[1])) {
            oldestTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView popularTxt = v.findViewById(R.id.popup_photo_order_popularTxt);
        popularTxt.setText(names[2]);
        if (valueNow.equals(values[2])) {
            popularTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }

        TextView randomTxt = v.findViewById(R.id.popup_photo_order_randomTxt);
        randomTxt.setText(names[3]);
        if (valueNow.equals(values[3])) {
            randomTxt.setTextColor(ThemeManager.getSubtitleColor(v.getContext()));
        }
    }

    // interface.

    // on photo order changed listener.

    public interface OnPhotoOrderChangedListener {
        void onPhotoOrderChange(String orderValue);
    }

    public void setOnPhotoOrderChangedListener(OnPhotoOrderChangedListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        String newValue = valueNow;
        int i = view.getId();
        if (i == R.id.popup_photo_order_latest) {
            newValue = values[0];
        } else if (i == R.id.popup_photo_order_oldest) {
            newValue = values[1];
        } else if (i == R.id.popup_photo_order_popular) {
            newValue = values[2];
        } else if (i == R.id.popup_photo_order_random) {
            newValue = values[3];
        }

        if (!newValue.equals(valueNow) && listener != null) {
            listener.onPhotoOrderChange(newValue);
            dismiss();
        }
    }
}