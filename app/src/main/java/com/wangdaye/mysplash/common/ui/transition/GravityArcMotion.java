package com.wangdaye.mysplash.common.ui.transition;

import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.transition.ArcMotion;
import android.util.AttributeSet;

/**
 * Gravity arc motion.
 *
 * This motion is used to draw a arc track that was effected by gravity.
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GravityArcMotion extends ArcMotion {

    private static final float DEFAULT_MIN_ANGLE_DEGREES = 0;
    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70;
    private static final float DEFAULT_MAX_TANGENT = (float)
            Math.tan(Math.toRadians(DEFAULT_MAX_ANGLE_DEGREES/2));

    private float mMinimumHorizontalAngle = 0;
    private float mMinimumVerticalAngle = 0;
    private float mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
    private float mMinimumHorizontalTangent = 0;
    private float mMinimumVerticalTangent = 0;
    private float mMaximumTangent = DEFAULT_MAX_TANGENT;

    public GravityArcMotion() {}

    public GravityArcMotion(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMinimumHorizontalAngle(float angleInDegrees) {
        mMinimumHorizontalAngle = angleInDegrees;
        mMinimumHorizontalTangent = toTangent(angleInDegrees);
    }

    @Override
    public float getMinimumHorizontalAngle() {
        return mMinimumHorizontalAngle;
    }

    @Override
    public void setMinimumVerticalAngle(float angleInDegrees) {
        mMinimumVerticalAngle = angleInDegrees;
        mMinimumVerticalTangent = toTangent(angleInDegrees);
    }

    @Override
    public float getMinimumVerticalAngle() {
        return mMinimumVerticalAngle;
    }

    @Override
    public void setMaximumAngle(float angleInDegrees) {
        mMaximumAngle = angleInDegrees;
        mMaximumTangent = toTangent(angleInDegrees);
    }

    @Override
    public float getMaximumAngle() {
        return mMaximumAngle;
    }

    private static float toTangent(float arcInDegrees) {
        if (arcInDegrees < 0 || arcInDegrees > 90) {
            throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
        }
        return (float) Math.tan(Math.toRadians(arcInDegrees / 2));
    }

    @Override
    public Path getPath(float startX, float startY, float endX, float endY) {
        Path path = new Path();
        path.moveTo(startX, startY);

        float ex;
        float ey;
        if (startY == endY) {
            ex = (startX + endX) / 2;
            ey = startY + mMinimumHorizontalTangent * Math.abs(endX - startX) / 2;
        } else if (startX == endX) {
            ex = startX + mMinimumVerticalTangent * Math.abs(endY - startY) / 2;
            ey = (startY + endY) / 2;
        } else {
            float deltaX = endX - startX;

            float deltaY;
            if (endY < startY) {
                deltaY = startY - endY; // Y is inverted compared to diagram above.
            } else {
                deltaY = endY - startY;
            }

            // hypotenuse squared.
            float h2 = deltaX * deltaX + deltaY * deltaY;

            // Midpoint between start and end
            float dx = (startX + endX) / 2;
            float dy = (startY + endY) / 2;

            // Distance squared between end point and mid point is (1/2 hypotenuse)^2
            float midDist2 = h2 * 0.25f;

            float minimumArcDist2;

            if (Math.abs(deltaX) < Math.abs(deltaY)) {
                // Similar triangles bfa and bde mean that (ab/fb = eb/bd)
                // Therefore, eb = ab * bd / fb
                // ab = hypotenuse
                // bd = hypotenuse/2
                // fb = deltaY
                float eDistY = h2 / (2 * deltaY);
                ey = endY + eDistY;
                ex = endX;

                minimumArcDist2 = midDist2 * mMinimumVerticalTangent
                        * mMinimumVerticalTangent;
            } else {
                // Same as above, but flip X & Y
                float eDistX = h2 / (2 * deltaX);
                ex = endX + eDistX;
                ey = endY;

                minimumArcDist2 = midDist2
                        * mMinimumHorizontalTangent
                        * mMinimumHorizontalTangent;
            }
            float arcDistX = dx - ex;
            float arcDistY = dy - ey;
            float arcDist2 = arcDistX * arcDistX + arcDistY * arcDistY;

            float maximumArcDist2 = midDist2 * mMaximumTangent * mMaximumTangent;

            float newArcDistance2 = 0;
            if (arcDist2 < minimumArcDist2) {
                newArcDistance2 = minimumArcDist2;
            } else if (arcDist2 > maximumArcDist2) {
                newArcDistance2 = maximumArcDist2;
            }
            if (newArcDistance2 != 0) {
                float ratio2 = newArcDistance2 / arcDist2;
                float ratio = (float) Math.sqrt(ratio2);
                ex = dx + (ratio * (ex - dx));
                ey = dy + (ratio * (ey - dy));
            }
        }
        float controlX1 = (startX + ex) / 2;
        float controlY1 = (startY + ey) / 2;
        float controlX2 = (ex + endX) / 2;
        float controlY2 = (ey + endY) / 2;
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
        return path;
    }
}
