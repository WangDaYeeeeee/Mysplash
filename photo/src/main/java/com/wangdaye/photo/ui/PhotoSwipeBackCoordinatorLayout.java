package com.wangdaye.photo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;

public class PhotoSwipeBackCoordinatorLayout extends SwipeBackCoordinatorLayout {

    private @Nullable View target;
    private @Nullable ViewPager2 horizontalConsumer;
    private int horizontalScrollOffset;

    public PhotoSwipeBackCoordinatorLayout(Context context) {
        super(context);
    }

    public PhotoSwipeBackCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoSwipeBackCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawSwipeFeedback(int dir, float swipeDistance, float triggerDistance) {
        if (target != null) {
            target.setTranslationY(
                    (float) (
                            dir * 0.33F
                                    * triggerDistance
                                    * Math.log10(1 + 9.0 * Math.abs(swipeDistance) / triggerDistance)
                    )
            );
        }
    }

    public void setTarget(@Nullable View target) {
        this.target = target;
    }

    public void setHorizontalConsumer(@Nullable ViewPager2 consumer) {
        this.horizontalConsumer = consumer;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes, int type) {
        if (horizontalConsumer != null) {
            horizontalConsumer.beginFakeDrag();
            horizontalScrollOffset = 0;
        }
        return super.onStartNestedScroll(child, target, nestedScrollAxes, type);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        int dxConsumed = onHorizontalPreScroll(dx);

        int[] newConsumed = new int[] {0, 0};
        super.onNestedPreScroll(target, dx - dxConsumed, dy, newConsumed, type);

        consumed[0] = newConsumed[0] + dxConsumed;
        consumed[1] = newConsumed[1];
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        int newDxConsumed = dxConsumed;
        int newDxUnconsumed = dxUnconsumed;
        if (horizontalConsumer != null && horizontalScrollOffset == 0 && dxUnconsumed != 0) {
            horizontalConsumer.fakeDragBy(-dxUnconsumed);
            horizontalScrollOffset += -dxUnconsumed;

            newDxConsumed = dxConsumed + dxUnconsumed;
            newDxUnconsumed = 0;
        }

        super.onNestedScroll(target, newDxConsumed, dyConsumed, newDxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public void onStopNestedScroll(View child, int type) {
        super.onStopNestedScroll(child, type);
        if (horizontalConsumer != null) {
            horizontalConsumer.endFakeDrag();
        }
    }

    private int onHorizontalPreScroll(int dx) {
        if (horizontalConsumer == null || horizontalScrollOffset == 0) {
            return 0;
        }

        int consumed;
        if (horizontalScrollOffset * (horizontalScrollOffset - dx) > 0) {
            consumed = dx;
            horizontalConsumer.fakeDragBy(-dx);
            horizontalScrollOffset += -dx;
        } else {
            consumed = -horizontalScrollOffset;
            horizontalConsumer.fakeDragBy(-horizontalScrollOffset);
            horizontalScrollOffset = 0;
        }
        return consumed;
    }
}
