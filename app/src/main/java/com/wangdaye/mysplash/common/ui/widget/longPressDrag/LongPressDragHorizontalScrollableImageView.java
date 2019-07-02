package com.wangdaye.mysplash.common.ui.widget.longPressDrag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.wangdaye.mysplash.common.ui.widget.HorizontalScrollableImageView;

import java.util.List;

public class LongPressDragHorizontalScrollableImageView extends HorizontalScrollableImageView
        implements LongPressDragView {

    private LongPressDragHelper longPressDragHelper;

    public LongPressDragHorizontalScrollableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        longPressDragHelper = new LongPressDragHelper(this);
    }

    public void setLongPressDragChildList(@NonNull List<View> childList) {
        longPressDragHelper.setChildList(childList);
    }

    public void setCancelFlagMarginTop(@Px int marginTop) {
        longPressDragHelper.setCancelFlagMarginTop(marginTop);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        longPressDragHelper.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean consumed = longPressDragHelper.onTouchEvent(ev);
        if (consumed) {
            postInvalidate();
        }

        boolean longPressed = longPressDragHelper.isLongPressed();
        if (!longPressed || ev.getAction() != MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(ev) || consumed;
        }

        return consumed;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        longPressDragHelper.setOnClickListener(l);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(null);
    }

    // interface.

    @Override
    public void innerSetOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
