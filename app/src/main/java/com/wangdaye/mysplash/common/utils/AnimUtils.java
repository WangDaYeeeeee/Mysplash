package com.wangdaye.mysplash.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.os.Build;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Anim utils.
 *
 * An utils class that makes operations of animations easier.
 *
 */
public class AnimUtils {

    private static Interpolator fastOutSlowIn;

    /**
     * An implementation of {@link android.util.Property} to be used specifically with fields of
     * type
     * <code>float</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Float) set()} function that takes the primitive
     * <code>float</code> type and avoids autoboxing and other overhead associated with the
     * <code>Float</code> class.
     *
     * @param <T> The class on which the Property is declared.
     **/
    static abstract class FloatProperty<T> extends Property<T, Float> {
        FloatProperty(String name) {
            super(Float.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Float)} that is faster when dealing
         * with fields of type <code>float</code>.
         */
        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    /**
     * An extension to {@link ColorMatrix} which caches the saturation value for animation purposes.
     */
    public static class ObservableColorMatrix extends ColorMatrix {

        private float saturation = 1f;

        public ObservableColorMatrix() {
            super();
        }

        private float getSaturation() {
            return saturation;
        }

        @Override
        public void setSaturation(float saturation) {
            this.saturation = saturation;
            super.setSaturation(saturation);
        }

        public static final Property<ObservableColorMatrix, Float> SATURATION
                = new FloatProperty<ObservableColorMatrix>("saturation") {

            @Override
            public void setValue(ObservableColorMatrix cm, float value) {
                cm.setSaturation(value);
            }

            @Override
            public Float get(ObservableColorMatrix cm) {
                return cm.getSaturation();
            }
        };
    }

    private AnimUtils() {}

    public static void translationYInitShow(final View v, int delay) {
        v.setVisibility(View.INVISIBLE);
        DisplayUtils utils = new DisplayUtils(v.getContext());
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "translationY", utils.dpToPx(72), 0)
                .setDuration(300);

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setStartDelay(delay);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    public static void alphaInitShow(final View v, int delay) {
        v.setVisibility(View.INVISIBLE);
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300);

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setStartDelay(delay);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    public static void animShow(View v) {
        animShow(v, 300, 0, 1);
    }

    public static void animShow(View v, int duration, float from, float to) {
        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
        v.clearAnimation();
        if (from != to) {
            ObjectAnimator
                    .ofFloat(v, "alpha", from, to)
                    .setDuration(duration)
                    .start();
        }
    }

    public static void animHide(final View v) {
        animHide(v, 300, v.getAlpha(), 0, true);
    }

    public static void animHide(final View v, int duration, float from, float to, final boolean gone) {
        v.clearAnimation();
        if (from != to) {
            ObjectAnimator anim = ObjectAnimator
                    .ofFloat(v, "alpha", from, to)
                    .setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (gone) {
                        v.setVisibility(View.GONE);
                    }
                }
            });
            anim.start();
        }
    }

    public static void animScale(final View v, int duration, int delay, float to) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", v.getScaleX(), to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", v.getScaleY(), to);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.setInterpolator(new DecelerateInterpolator());
        if (delay > 0) {
            set.setStartDelay(delay);
        }

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        });
        set.play(scaleX).with(scaleY);
        set.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }
}
