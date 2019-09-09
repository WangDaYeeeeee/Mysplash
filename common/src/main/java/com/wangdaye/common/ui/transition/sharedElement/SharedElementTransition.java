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
import com.wangdaye.common.base.application.MysplashApplication;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class SharedElementTransition extends Transition {

    @Nullable private Boolean enter;

    public SharedElementTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected boolean isEnter(View view) {
        if (enter == null) {
            enter = (getExtraPropertiesFromView(view) == null);
        }
        return enter;
    }

    @Nullable
    protected static Bundle getExtraPropertiesFromApplication() {
        return MysplashApplication.getInstance().getSharedElementTransitionExtraProperties();
    }

    @Nullable
    protected static Bundle getExtraPropertiesFromView(View view) {
        return (Bundle) view.getTag(R.id.tag_transition_extra_properties);
    }

    public static void setExtraPropertiesForView(View view, Bundle b) {
        view.setTag(R.id.tag_transition_extra_properties, b);
    }
}
