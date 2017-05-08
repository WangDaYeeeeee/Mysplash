package com.wangdaye.mysplash.common.ui.transition;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;

/**
 * Cover transition.
 *
 * This transition is working for {@link com.wangdaye.mysplash.photo.view.activity.PhotoActivity},
 * it is responsible of the animation of photo image.
 *
 * */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CoverTransition extends ChangeBounds {

    private Context context;
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    public CoverTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        Photo photo = Mysplash.getInstance().getPhoto();
        if (photo != null) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int[] sizes = FreedomImageView.getMeasureSize(
                    context, screenWidth, photo.width, photo.height, true);
            Rect bounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
            bounds.left = 0;
            bounds.top = 0;
            bounds.right = sizes[0];
            bounds.bottom = sizes[1];
            transitionValues.values.put(PROPNAME_BOUNDS, bounds);
        }
    }
}
