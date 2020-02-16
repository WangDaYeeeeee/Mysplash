package com.wangdaye.common.ui.widget.windowInsets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wangdaye.common.R;

public class ApplyWindowInsetsLayout extends FrameLayout {

    @NonNull private Rect windowInsets;
    @Nullable private OnApplyWindowInsetsListener listener = null;

    private Paint paint;

    public ApplyWindowInsetsLayout(Context context) {
        this(context, null);
    }

    public ApplyWindowInsetsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ApplyWindowInsetsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        windowInsets = new Rect(0, 0, 0, 0);

        paint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorRoot));
        } else {
            paint.setColor(Color.BLACK);
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        consumeInsets(insets.left, insets.top, insets.right, insets.bottom);
        return false;
    }

    private void consumeInsets(int left, int top, int right, int bottom) {
        setPadding(left, 0, right, 0);

        boolean changed = false;
        if (windowInsets.left != left || windowInsets.top != top
                || windowInsets.right != right || windowInsets.bottom != bottom) {
            changed = true;
            windowInsets.set(left, top, right, bottom);
        }
        if (changed && listener != null) {
            listener.onApplyWindowInsets(windowInsets);
        }
    }

    @NonNull
    public Rect getWindowInsets() {
        return windowInsets;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (windowInsets.left != 0) {
            canvas.drawRect(0, 0, windowInsets.left, getMeasuredHeight(), paint);
        }
        if (windowInsets.right != 0) {
            canvas.drawRect(
                    getMeasuredWidth() - windowInsets.right,
                    0,
                    getMeasuredWidth(),
                    getMeasuredHeight(),
                    paint
            );
        }
    }

    public interface OnApplyWindowInsetsListener {
        void onApplyWindowInsets(@NonNull Rect windowInsets);
    }

    public void setOnApplyWindowInsetsListener(@Nullable OnApplyWindowInsetsListener l) {
        this.listener = l;
    }
}
