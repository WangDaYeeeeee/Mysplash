package com.wangdaye.photo.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.common.base.popup.MysplashPopupWindow;
import com.wangdaye.photo.R;
import com.wangdaye.photo.activity.SetWallpaperActivity;

/**
 * Wallpaper clip popup window.
 *
 * This popup window is used to select what kind of shape to wallpaper clip.
 *
 * */

public class WallpaperClipPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {

    private OnClipTypeChangedListener listener;
    private int valueNow;

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

    private void initData(int valueNow) {
        this.valueNow = valueNow;
    }

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_wallpaper_clip_square).setOnClickListener(this);
        v.findViewById(R.id.popup_wallpaper_clip_rect).setOnClickListener(this);

        TextView squareTxt = v.findViewById(R.id.popup_wallpaper_clip_squareTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_SQUARE) {
            squareTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle));
        }

        TextView rectTxt = v.findViewById(R.id.popup_wallpaper_clip_rectTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_RECT) {
            rectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle));
        }
    }

    // interface.

    // on clip type changed listener.

    public interface OnClipTypeChangedListener {
        void onClipTypeChanged(int type);
    }

    public void setOnClipTypeChangedListener(OnClipTypeChangedListener l) {
        listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        int newValue = valueNow;
        int i = view.getId();
        if (i == R.id.popup_wallpaper_clip_square) {
            newValue = SetWallpaperActivity.CLIP_TYPE_SQUARE;
        } else if (i == R.id.popup_wallpaper_clip_rect) {
            newValue = SetWallpaperActivity.CLIP_TYPE_RECT;
        }

        if (newValue != valueNow && listener != null) {
            listener.onClipTypeChanged(newValue);
            dismiss();
        }
    }
}