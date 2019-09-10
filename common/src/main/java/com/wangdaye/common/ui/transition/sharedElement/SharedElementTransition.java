package com.wangdaye.common.ui.transition.sharedElement;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.wangdaye.common.R;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class SharedElementTransition extends Transition {

    private static final String KEY_START = "mysplash:sharedElementTransition:start";

    public SharedElementTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean isStart(View view) {
        Bundle bundle = getExtraPropertiesFromView(view);
        return bundle != null && bundle.getBoolean(KEY_START, false);
    }

    @Nullable
    protected static Bundle getExtraPropertiesFromView(View view) {
        return (Bundle) view.getTag(R.id.tag_transition_extra_properties);
    }

    public static void setExtraPropertiesForView(View view, @Nullable Bundle bundle, boolean start) {
        if (bundle != null) {
            bundle.putBoolean(KEY_START, start);
            view.setTag(R.id.tag_transition_extra_properties, bundle);
        }
    }
}
