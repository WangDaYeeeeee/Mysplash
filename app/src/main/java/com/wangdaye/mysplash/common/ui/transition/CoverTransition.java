package com.wangdaye.mysplash.common.ui.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.Size;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

/**
 * Cover transition.
 *
 * This transition is working for {@link com.wangdaye.mysplash.photo.view.activity.PhotoActivity},
 * it is responsible of the animation of photo image.
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CoverTransition extends ChangeBounds {
    // widget
    private Context context;

    // data
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    /** <br> life cycle. */

    public CoverTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /** <br> data. */

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        int[] sizes = getMeasureSize();
        if (sizes[0] != 0 && sizes[1] != 0) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int deltaWidth = sizes[0] - screenWidth;
            Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
            bounds.top = 0;
            bounds.left = (int) (-deltaWidth / 2.0);
            bounds.right = (int) (screenWidth + deltaWidth / 2.0);
            bounds.bottom = sizes[1];
            transitionValues.values.put(PROPNAME_BOUNDS, bounds);
        }
    }

    @Size(2)
    private int[] getMeasureSize() {
        Photo photo = Mysplash.getInstance().getPhoto();
        if (photo != null) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            float limitHeight = screenHeight
                    - context.getResources().getDimensionPixelSize(R.dimen.photo_info_base_view_height);

            if (1.0 * photo.height / photo.width * screenWidth <= limitHeight) {
                return new int[] {
                        (int) (limitHeight * (photo.width / 1.0 / photo.height)),
                        (int) limitHeight};
            } else {
                return new int[] {
                        screenWidth,
                        (int) (screenWidth * (photo.height / 1.0 / photo.width))};
            }
        } else {
            return new int[] {0, 0};
        }
    }
}
