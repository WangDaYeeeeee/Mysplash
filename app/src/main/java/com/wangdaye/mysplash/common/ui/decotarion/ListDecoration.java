package com.wangdaye.mysplash.common.ui.decotarion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * List decoration.
 *
 * A simple decoration class for {@link RecyclerView}.
 *
 * */

public class ListDecoration extends RecyclerView.ItemDecoration {

    private Paint paint;
    private int decorationHeight;

    public ListDecoration(Context context) {
        this.decorationHeight = (int) new DisplayUtils(context).dpToPx(1);

        this.paint = new Paint();
        paint.setColor(ThemeManager.getLineColor(context));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(decorationHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, decorationHeight);
    }
}