package com.wangdaye.mysplash.photo.view.transition;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;

/**
 * Photo shared in.
 * */

public class PhotoSharedIn extends ChangeBounds {
    // data
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";

    /** <br> life cycle. */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoSharedIn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> animation. */

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int[] size = Mysplash.getInstance().getBoundSize();
        int width = ((View) transitionValues.values.get(PROPNAME_PARENT)).getMeasuredWidth();
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        bounds.right = width;
        bounds.bottom = (int) (1.0 * width / size[0] * size[1]);
        transitionValues.values.put(PROPNAME_BOUNDS, bounds);
    }
}
