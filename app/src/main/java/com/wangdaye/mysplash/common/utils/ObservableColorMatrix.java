package com.wangdaye.mysplash.common.utils;

import android.graphics.ColorMatrix;
import android.util.Property;

/**
 * An extension to {@link ColorMatrix} which caches the saturation value for animation purposes.
 */
public class ObservableColorMatrix extends ColorMatrix {

    private float saturation = 1f;

    public ObservableColorMatrix() {
        super();
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setSaturation(float saturation) {
        this.saturation = saturation;
        super.setSaturation(saturation);
    }

    public static final Property<ObservableColorMatrix, Float> SATURATION
            = new AnimUtils.FloatProperty<ObservableColorMatrix>("saturation") {

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
