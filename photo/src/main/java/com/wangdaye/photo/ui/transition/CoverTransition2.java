package com.wangdaye.photo.ui.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;

import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.photo.activity.PhotoActivity;

/**
 * Cover transition.
 *
 * This transition is working for {@link PhotoActivity},
 * it is responsible of the animation of photo image.
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CoverTransition2 extends ChangeBounds {

    private Context context;
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    public CoverTransition2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int[] size = DisplayUtils.getScreenSize(context);
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        if (bounds != null) {
            bounds.left = 0;
            bounds.top = 0;
            bounds.right = size[0];
            bounds.bottom = size[1];
            transitionValues.values.put(PROPNAME_BOUNDS, bounds);
        }
    }
}
