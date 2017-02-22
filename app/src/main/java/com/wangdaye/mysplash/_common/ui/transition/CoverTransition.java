package com.wangdaye.mysplash._common.ui.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CoverTransition extends ChangeBounds {
    // widget
    private Context context;

    // data
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    public CoverTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int[] sizes = getMeasureSize();
        int deltaWidth = sizes[0] - screenWidth;
        Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
        bounds.top = 0;
        bounds.left = (int) (-deltaWidth / 2.0);
        bounds.right = (int) (screenWidth + deltaWidth / 2.0);
        bounds.bottom = sizes[1];
        transitionValues.values.put(PROPNAME_BOUNDS, bounds);
    }

    private int[] getMeasureSize() {
        Photo photo = Mysplash.getInstance().getPhoto();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        float limitHeight = screenHeight
                - new DisplayUtils(context).dpToPx(300);

        if (1.0 * photo.height / photo.width * screenWidth <= limitHeight) {
            return new int[] {
                    (int) (limitHeight * (photo.width / 1.0 / photo.height)),
                    (int) limitHeight};
        } else {
            return new int[] {
                    screenWidth,
                    (int) (screenWidth * (photo.height / 1.0 / photo.width))};
        }
    }
}
