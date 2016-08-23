package com.wangdaye.mysplash._common.ui.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * My toast.
 * */

public class MaterialToast
        implements View.OnClickListener {
    // widget
    private WindowManager windowManager;
    private View toastView;
    private WindowManager.LayoutParams params;
    private Timer timer;

    private OnActionClickListener listener;

    // data
    private int showTime;
    private boolean showing;

    public static final int LENGTH_SHORT = 1500;
    public static final int LENGTH_LONG = 3500;

    /** <br> life cycle. */

    @SuppressLint("ShowToast")
    private MaterialToast(Context context, String text, String action, int showTime){
        this.showTime = showTime;
        this.showing = false;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        timer = new Timer();
        setParams();
        buildView(context, text, action);
    }

    public static MaterialToast makeText(Context context,
                                         String text, @Nullable String action, int showTime) {
        return new MaterialToast(context, text, action, showTime);
    }

    /** <br> UI. */

    @SuppressLint("InflateParams")
    private void buildView(Context c, String text, String action) {
        View v = LayoutInflater.from(c).inflate(R.layout.toast_action_view, null);

        TextView txt = (TextView) v.findViewById(R.id.toast_action_view_text);
        TypefaceUtils.setTypeface(c, txt);
        txt.setText(text);

        Button btn = (Button) v.findViewById(R.id.toast_action_view_button);
        if (action == null || action.equals("")) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setText(action);
            btn.setOnClickListener(this);
        }

        toastView = v;
    }

    private void setParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.material_toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.y = 200;
    }

    public void show(){
        if(!showing){
            showing = true;
            windowManager.addView(toastView, params);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    windowManager.removeView(toastView);
                    showing = false;
                }
            }, showTime);
        }
    }

    public void cancel(){
        if(timer == null){
            windowManager.removeView(toastView);
            timer.cancel();
        }
        showing = false;
    }

    /** <br> interface. */

    public interface OnActionClickListener {
        void onActionClick();
    }

    public MaterialToast setOnActionClickListener(OnActionClickListener l) {
        this.listener = l;
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toast_action_view_button:
                if (listener != null) {
                    listener.onActionClick();
                    cancel();
                }
                break;
        }
    }
}
