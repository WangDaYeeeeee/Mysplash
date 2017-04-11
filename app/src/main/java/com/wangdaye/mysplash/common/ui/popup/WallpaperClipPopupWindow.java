package com.wangdaye.mysplash.common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash.common.ui.activity.SetWallpaperActivity;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import butterknife.ButterKnife;

/**
 * Wallpaper clip popup window.
 *
 * This popup window is used to select what kind of shape to wallpaper clip.
 *
 * */

public class WallpaperClipPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {
    // widget
    private OnClipTypeChangedListener listener;

    // data
    private int valueNow;

    /** <br> life cycle. */

    public WallpaperClipPopupWindow(Context c, View anchor, int valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, int valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_wallpaper_clip, null);
        setContentView(v);

        initData(valueNow);
        initWidget();
        show(anchor, 0, 0);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_wallpaper_clip_square).setOnClickListener(this);
        v.findViewById(R.id.popup_wallpaper_clip_rect).setOnClickListener(this);

        TextView squareTxt = ButterKnife.findById(v, R.id.popup_wallpaper_clip_squareTxt);
        DisplayUtils.setTypeface(v.getContext(), squareTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_SQUARE) {
            squareTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
        }

        TextView rectTxt = ButterKnife.findById(v, R.id.popup_wallpaper_clip_rectTxt);
        DisplayUtils.setTypeface(v.getContext(), rectTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_RECT) {
            rectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
        }
    }

    /** <br> data. */

    private void initData(int valueNow) {
        this.valueNow = valueNow;
    }

    /** <br> interface. */

    public interface OnClipTypeChangedListener {
        void onClipTypeChanged(int type);
    }

    public void setOnClipTypeChangedListener(OnClipTypeChangedListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        int newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_wallpaper_clip_square:
                newValue = SetWallpaperActivity.CLIP_TYPE_SQUARE;
                break;

            case R.id.popup_wallpaper_clip_rect:
                newValue = SetWallpaperActivity.CLIP_TYPE_RECT;
                break;
        }

        if (newValue != valueNow && listener != null) {
            listener.onClipTypeChanged(newValue);
            dismiss();
        }
    }
}