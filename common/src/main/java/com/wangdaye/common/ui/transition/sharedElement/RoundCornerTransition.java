package com.wangdaye.common.ui.transition.sharedElement;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RoundCornerTransition extends SharedElementTransition {

    private static final String PROPNAME_RADIUS = "mysplash:roundCorner:radius";

    private static final String[] transitionProperties = {
            PROPNAME_RADIUS
    };

    public RoundCornerTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void addExtraProperties(View view, Bundle bundle) {
        bundle.putFloat(PROPNAME_RADIUS, getRadius(view));
    }

    @Px
    private static float getRadius(View view) {
        if (view instanceof CardView) {
            return ((CardView) view).getRadius();
        }
        return 0;
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
            float actualInitRadius = getRadius(transitionValues.view);
            Bundle b = getExtraPropertiesFromView(transitionValues.view);
            if (b != null && b.containsKey(PROPNAME_RADIUS)) {
                actualInitRadius = b.getFloat(PROPNAME_RADIUS);
            }
            transitionValues.values.put(PROPNAME_RADIUS, actualInitRadius);
        } else {
            transitionValues.values.put(PROPNAME_RADIUS, getRadius(transitionValues.view));
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(
                (Float) startValues.values.get(PROPNAME_RADIUS),
                (Float) endValues.values.get(PROPNAME_RADIUS)
        );
        animator.addUpdateListener(valueAnimator -> {
            endValues.view.setOutlineProvider(new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(
                            view.getPaddingLeft(),
                            view.getPaddingTop(),
                            view.getWidth() - view.getPaddingRight(),
                            view.getHeight() - view.getPaddingBottom(),
                            (Float) valueAnimator.getAnimatedValue()
                    );
                }
            });
            endValues.view.setClipToOutline(true);
        });
        return animator;
    }
}
