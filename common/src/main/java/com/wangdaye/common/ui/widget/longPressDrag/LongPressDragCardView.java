package com.wangdaye.common.ui.widget.longPressDrag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.wangdaye.common.ui.widget.CoverImageView;

import java.util.List;

public class LongPressDragCardView extends CardView
        implements LongPressDragView {

    private LongPressDragHelper longPressDragHelper;

    @Nullable private CoverImageView coverImage;

    public LongPressDragCardView(@NonNull Context context) {
        this(context, null);
    }

    public LongPressDragCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongPressDragCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        longPressDragHelper = new LongPressDragHelper(this);
    }

    public void setLongPressDragChildList(@NonNull List<View> childList) {
        longPressDragHelper.setChildList(childList);
    }

    public void setCancelFlagMarginTop(@Px int marginTop) {
        longPressDragHelper.setCancelFlagMarginTop(marginTop);
    }

    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);
        if (coverImage != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            coverImage.setOutlineProvider(new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(
                            view.getPaddingLeft(),
                            view.getPaddingTop(),
                            view.getWidth() - view.getPaddingRight(),
                            view.getHeight() - view.getPaddingBottom(),
                            radius
                    );
                }
            });
            coverImage.setClipToOutline(true);
        }
    }

    public void setCoverImage(@Nullable CoverImageView view) {
        coverImage = view;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        longPressDragHelper.draw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = longPressDragHelper.onInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev) || intercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean consumed = longPressDragHelper.onTouchEvent(ev);
        if (consumed) {
            postInvalidate();
        }
        return super.onTouchEvent(ev) || consumed;
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
