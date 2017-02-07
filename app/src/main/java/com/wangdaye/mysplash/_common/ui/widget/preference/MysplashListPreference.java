package com.wangdaye.mysplash._common.ui.widget.preference;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.ListPreference;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Mysplash list preference_widget.
 * */

public class MysplashListPreference extends ListPreference {

    /** <br> life cycle. */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MysplashListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MysplashListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MysplashListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MysplashListPreference(Context context) {
        super(context);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView title = (TextView) view.findViewById(android.R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(
                Mysplash.getInstance().isLightTheme() ?
                        ContextCompat.getColor(getContext(), R.color.colorTextTitle_light)
                        :
                        ContextCompat.getColor(getContext(), R.color.colorTextTitle_dark));

        TextView summary = (TextView) view.findViewById(android.R.id.summary);
        DisplayUtils.setTypeface(getContext(), summary);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        summary.setTextColor(
                Mysplash.getInstance().isLightTheme() ?
                        ContextCompat.getColor(getContext(), R.color.colorTextSubtitle_light)
                        :
                        ContextCompat.getColor(getContext(), R.color.colorTextSubtitle_dark));
    }
}
