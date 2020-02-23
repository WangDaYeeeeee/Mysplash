package com.wangdaye.common.ui.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarRecyclerView;

public class GridMarginsItemDecoration extends RecyclerView.ItemDecoration {

    private @Px int gridMargins;
    private @Px int singleSpanMargins;

    private @Px int gridCardRadius;
    private @Px int singleSpanCardRadius;

    private boolean singleSpan;
    private @Px int margins;
    private @Px int cardRadius;

    private Rect insets;
    private @Px int parentPaddingLeft;
    private @Px int parentPaddingRight;
    private @Px int parentPaddingTop;
    private @Px int parentPaddingBottom;

    public GridMarginsItemDecoration(Context context, RecyclerView recyclerView) {
        this(
                recyclerView,
                context.getResources().getDimensionPixelSize(R.dimen.normal_margin),
                context.getResources().getDimensionPixelSize(R.dimen.material_card_radius)
        );
    }

    public GridMarginsItemDecoration(RecyclerView recyclerView, int gridMargins, int gridCardRadius) {
        this(recyclerView, gridMargins, 0, gridCardRadius, 0);
    }

    public GridMarginsItemDecoration(RecyclerView recyclerView,
                                     int gridMargins, int singleSpanMargins,
                                     int gridCardRadius, int singleSpanCardRadius) {
        this.gridMargins = gridMargins;
        this.singleSpanMargins = singleSpanMargins;
        this.gridCardRadius = gridCardRadius;
        this.singleSpanCardRadius = singleSpanCardRadius;

        RecyclerView.LayoutManager layoutManager = getLayoutManager(recyclerView);
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            singleSpan = ((StaggeredGridLayoutManager) layoutManager).getSpanCount() == 1;
        } else if (layoutManager instanceof GridLayoutManager) {
            singleSpan = ((GridLayoutManager) layoutManager).getSpanCount() == 1;
        } else if (layoutManager instanceof LinearLayoutManager) { // linear layout manager.
            singleSpan = true;
        } else if (MysplashApplication.isDebug(recyclerView.getContext())) {
            throw new RuntimeException("Null layout manager.");
        } else {
            singleSpan = false;
        }
        setParentPadding(
                recyclerView,
                (singleSpan ? singleSpanMargins : gridMargins) / 2,
                getWindowInset(recyclerView)
        );
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.LayoutManager layoutManager = getLayoutManager(parent);
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            singleSpan = ((StaggeredGridLayoutManager) layoutManager).getSpanCount() == 1;
        } else if (layoutManager instanceof GridLayoutManager) {
            singleSpan = ((GridLayoutManager) layoutManager).getSpanCount() == 1;
        } else { // linear layout manager.
            singleSpan = true;
        }

        margins = (singleSpan ? singleSpanMargins : gridMargins) / 2;
        cardRadius = singleSpan ? singleSpanCardRadius : gridCardRadius;

        parentPaddingLeft = parent.getPaddingLeft();
        parentPaddingRight = parent.getPaddingRight();
        parentPaddingTop = parent.getPaddingTop();
        parentPaddingBottom = parent.getPaddingBottom();
        insets = getWindowInset(parent);
        if (parentPaddingLeft != margins + insets.left
                || parentPaddingRight != margins + insets.right
                || parentPaddingTop != margins
                || parentPaddingBottom != margins + insets.bottom) {
            setParentPadding(parent, margins, insets);
        }

        outRect.set(margins, margins, margins, margins);
        if (view instanceof CardView) {
            ((CardView) view).setRadius(cardRadius);
        }
    }

    private void setParentPadding(RecyclerView parent, @Px int padding, Rect insets) {
        parent.setPadding(
                padding + insets.left,
                padding,
                padding + insets.right,
                padding + insets.bottom
        );
        parent.setClipToPadding(false);
    }

    private RecyclerView.LayoutManager getLayoutManager(RecyclerView recyclerView) {
        if (recyclerView instanceof MultipleStateRecyclerView) {
            return ((MultipleStateRecyclerView) recyclerView).getLayoutManager(
                    MultipleStateRecyclerView.STATE_NORMALLY);
        } else {
            return recyclerView.getLayoutManager();
        }
    }

    private Rect getWindowInset(RecyclerView parent) {
        if (parent instanceof FitBottomSystemBarRecyclerView) {
            return ((FitBottomSystemBarRecyclerView) parent).getWindowInsets();
        } else {
            return new Rect();
        }
    }
}