package com.wangdaye.mysplash.common.ui.widget.preference;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.SwitchPreference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Mysplash switch preference.
 *
 * A Preference that can set style of the texts.
 *
 * */

public class MysplashSwitchPreference extends SwitchPreference {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MysplashSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MysplashSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MysplashSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MysplashSwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        clearListenerInViewGroup((ViewGroup) view);
        super.onBindView(view);

        TextView title = (TextView) view.findViewById(android.R.id.title);
        title.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(R.dimen.title_text_size));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ThemeManager.getTitleColor(getContext()));

        TextView summary = view.findViewById(android.R.id.summary);
        summary.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(R.dimen.subtitle_text_size));
        summary.setTextColor(ThemeManager.getSubtitleColor(getContext()));
    }

    private void clearListenerInViewGroup(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }

        for(int i = 0; i < viewGroup.getChildCount(); i ++) {
            View v = viewGroup.getChildAt(i);
            if(v instanceof Switch) {
                ((Switch) v).setOnCheckedChangeListener(null);
                return;
            } else if (v instanceof ViewGroup){
                clearListenerInViewGroup((ViewGroup) v);
            }
        }
    }
}
