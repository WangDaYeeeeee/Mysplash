package com.wangdaye.mysplash.common.basic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.mysplash.R;

public abstract class MultiColumnAdapter<VH extends RecyclerView.ViewHolder> extends FooterAdapter<VH> {

    private int columnCount;

    private int gridMarginPixel;
    private int singleColumnMarginPixel;

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(View container, int columnCount,
                                           int gridMarginPixel, int singleColumnMarginPixel);

        protected void setLayoutParamsForGridItemMargin(View container, int columnCount,
                                                        int gridMarginPixel, int singleColumnMarginPixel) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            if (columnCount > 1) {
                params.setMargins(0, 0, gridMarginPixel, gridMarginPixel);
            } else {
                params.setMargins(0, 0, singleColumnMarginPixel, singleColumnMarginPixel);
            }
            container.setLayoutParams(params);
        }
    }

    public MultiColumnAdapter(Context context) {
        this(context, 1);
    }

    protected MultiColumnAdapter(Context context, int columnCount) {
        this(context, columnCount, context.getResources().getDimensionPixelSize(R.dimen.normal_margin),
                0);
    }

    protected MultiColumnAdapter(Context context, int columnCount,
                                 int gridMarginPixel, int singleColumnMarginPixel) {
        super(context);
        this.columnCount = columnCount;
        this.gridMarginPixel = gridMarginPixel;
        this.singleColumnMarginPixel = singleColumnMarginPixel;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(RecyclerView view, int columnCount) {
        if (this.columnCount != columnCount) {
            this.columnCount = columnCount;
            notifyDataSetChanged();
        }
        setGridRecyclerViewPadding(view);
    }

    public int getGridMarginPixel() {
        return gridMarginPixel;
    }

    public void setGridMarginPixel(RecyclerView view, int gridMarginPixel) {
        if (this.gridMarginPixel != gridMarginPixel) {
            this.gridMarginPixel = gridMarginPixel;
            notifyDataSetChanged();
        }
        setGridRecyclerViewPadding(view);
    }

    public int getSingleColumnMarginPixel() {
        return singleColumnMarginPixel;
    }

    public void setSingleColumnMarginPixel(RecyclerView view, int singleColumnMarginPixel) {
        if (this.singleColumnMarginPixel != singleColumnMarginPixel) {
            this.singleColumnMarginPixel = singleColumnMarginPixel;
            notifyDataSetChanged();
        }
        setGridRecyclerViewPadding(view);
    }

    protected void setGridRecyclerViewPadding(RecyclerView view) {
        if (columnCount > 1) {
            view.setPadding(gridMarginPixel, gridMarginPixel, 0, 0);
        } else {
            view.setPadding(singleColumnMarginPixel, singleColumnMarginPixel, 0, 0);
        }
    }
}
