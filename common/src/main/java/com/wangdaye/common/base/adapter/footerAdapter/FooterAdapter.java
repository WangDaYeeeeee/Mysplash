package com.wangdaye.common.base.adapter.footerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;

/**
 * Footer adapter.
 *
 * A RecyclerView.Adapter class with a footer view holder. By extending this adapter, child can
 * adapt footer view for RecyclerView more easily.
 *
 * */

public abstract class FooterAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private boolean hasFooter;

    public static final int PAYLOAD_UPDATE_ITEM = 1;

    public FooterAdapter() {
        hasFooter = hasFooter();
    }

    /**
     * Basic ViewHolder for {@link FooterAdapter}. This holder is used to fill the location of
     * navigation bar.
     * */
    protected static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(ViewGroup parent) {
            this(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_footer, parent, false)
            );
        }

        private FooterHolder(View itemView) {
            super(itemView);
        }

        public void onBindView() {
            if (itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params
                        = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
                params.setFullSpan(true);
                itemView.setLayoutParams(params);
            }
        }

        public void setColor(@ColorInt int color) {
            itemView.setBackgroundColor(color);
        }

        public void setAlpha(@FloatRange(from=0.0, to=1.0) float alpha) {
            itemView.setAlpha(alpha);
        }
    }

    protected boolean hasFooter() {
        return MysplashApplication.getInstance().getWindowInsets().bottom != 0;
    }

    public boolean isFooter(int position) {
        return hasFooter && position == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + (hasFooter ? 1 : 0);
    }

    public abstract int getRealItemCount();
}

