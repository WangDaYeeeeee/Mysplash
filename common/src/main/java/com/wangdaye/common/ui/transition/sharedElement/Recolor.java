package com.wangdaye.common.ui.transition.sharedElement;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Recolor extends SharedElementTransition {

    private static final String PROPNAME_COLOR = "mysplash:recolor:color";

    private static final String[] transitionProperties = {
            PROPNAME_COLOR
    };

    public Recolor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void addExtraProperties(View view, Bundle bundle) {
        bundle.putInt(PROPNAME_COLOR, getBackgroundColor(view));
    }

    @ColorInt
    private static int getBackgroundColor(View view) {
        int color = Color.TRANSPARENT;
        if (view instanceof CardView) {
            color = ((CardView) view).getCardBackgroundColor().getDefaultColor();
        } else {
            Drawable d = view.getBackground();
            if (d instanceof ColorDrawable) {
                color = ((ColorDrawable) d).getColor();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && d instanceof ColorStateListDrawable) {
                color = ((ColorStateListDrawable) d).getColorStateList().getDefaultColor();
            }
        }
        return color;
    }

    @Override
    public String[] getTransitionProperties() {
        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        if (isStart(transitionValues.view)) {
            int initColor = getBackgroundColor(transitionValues.view);
            Bundle b = getExtraPropertiesFromView(transitionValues.view);
            if (b != null && b.containsKey(PROPNAME_COLOR)) {
                initColor = b.getInt(PROPNAME_COLOR);
            }
            transitionValues.values.put(PROPNAME_COLOR, initColor);
        } else {
            transitionValues.values.put(PROPNAME_COLOR, getBackgroundColor(transitionValues.view));
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        ValueAnimator animator = ValueAnimator.ofArgb(
                (Integer) startValues.values.get(PROPNAME_COLOR),
                (Integer) endValues.values.get(PROPNAME_COLOR)
        );
        animator.addUpdateListener(valueAnimator ->
                endValues.view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue()));
        return animator;
    }
}
