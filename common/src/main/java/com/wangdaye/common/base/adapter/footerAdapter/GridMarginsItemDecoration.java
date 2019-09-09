package com.wangdaye.common.base.adapter.footerAdapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.common.R;

public class GridMarginsItemDecoration extends RecyclerView.ItemDecoration {

    private int marginGridItem;
    private Rect marginSingleSpan;

    private int cardRadiusGridItem;
    private int cardRadiusSingleSpan;

    private int spanCount;
    private int spanIndex;
    private int adapterPosition;
    private boolean footer;
    private boolean firstLine;

    public GridMarginsItemDecoration(Context context) {
        this(
                context.getResources().getDimensionPixelSize(R.dimen.normal_margin),
                context.getResources().getDimensionPixelSize(R.dimen.material_card_radius)
        );
    }

    public GridMarginsItemDecoration(int marginGridItem, int cardRadiusGridItem) {
        this(marginGridItem, 0, cardRadiusGridItem, 0);
    }

    public GridMarginsItemDecoration(int marginGridItem, int marginSingleSpan,
                                     int cardRadiusGridItem, int cardRadiusSingleSpan) {
        this(marginGridItem,
                marginSingleSpan, marginSingleSpan, marginSingleSpan, marginSingleSpan,
                cardRadiusGridItem, cardRadiusSingleSpan);
    }

    public GridMarginsItemDecoration(int marginGridItem,
                                     int marginSingleSpanLeft, int marginSingleSpanTop,
                                     int marginSingleSpanRight, int marginSingleSpanBottom,
                                     int cardRadiusGridItem, int cardRadiusSingleSpan) {
        this.marginGridItem = marginGridItem;
        this.marginSingleSpan = new Rect(
                marginSingleSpanLeft, marginSingleSpanTop, marginSingleSpanRight, marginSingleSpanBottom);
        this.cardRadiusGridItem = cardRadiusGridItem;
        this.cardRadiusSingleSpan = cardRadiusSingleSpan;

        this.spanCount = 0;
        this.spanIndex = -1;
        this.adapterPosition = -1;
        this.footer = false;
        this.firstLine = false;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params
                    = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            spanIndex = params.getSpanIndex();
            adapterPosition = params.getViewAdapterPosition();

            firstLine = adapterPosition < spanCount;
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams params
                    = (GridLayoutManager.LayoutParams) view.getLayoutParams();

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            spanIndex = params.getSpanIndex();
            adapterPosition = params.getViewAdapterPosition();

            if (adapterPosition >= spanCount) {
                firstLine = false;
            } else {
                GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) layoutManager).getSpanSizeLookup();
                firstLine = lookup.getSpanGroupIndex(adapterPosition, spanCount)
                        == lookup.getSpanGroupIndex(0, spanCount);
            }
        } else { // linear layout manager.
            spanCount = 1;
            spanIndex = 0;
            adapterPosition = ((RecyclerView.LayoutParams) view.getLayoutParams())
                    .getViewAdapterPosition();
            firstLine = adapterPosition == 0;
        }

        footer = false;
        if (parent.getAdapter() instanceof FooterAdapter) {
            footer = ((FooterAdapter) parent.getAdapter()).isFooter(adapterPosition);
        }
        if (footer) {
            return;
        }

        if (view instanceof CardView) {
            ((CardView) view).setRadius(
                    spanCount == 1 ? cardRadiusSingleSpan : cardRadiusGridItem);
        }

        if (spanCount == 1) {
            outRect.set(
                    marginSingleSpan.left,
                    firstLine ? marginSingleSpan.top : 0,
                    marginSingleSpan.right,
                    marginSingleSpan.bottom
            );
        } else {
            if (spanIndex == 0) {
                outRect.set(
                        marginGridItem, firstLine ? marginGridItem : 0, marginGridItem, marginGridItem);
            } else{
                outRect.set(
                        0, firstLine ? marginGridItem : 0, marginGridItem, marginGridItem);
            }
        }
    }
}
