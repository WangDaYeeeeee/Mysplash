package com.wangdaye.mysplash.common.basic.adapter;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;

/**
 * Footer adapter.
 *
 * A RecyclerView.Adapter class with a footer view holder. By extending this adapter, child can
 * adapt footer view for RecyclerView more easily.
 *
 * */

public abstract class FooterAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private boolean hasFooter;

    public FooterAdapter(Context context) {
        hasFooter = hasFooter(context);
    }

    /**
     * Basic ViewHolder for {@link FooterAdapter}. This holder is used to fill the location of
     * navigation bar.
     * */
    protected static class FooterHolder extends RecyclerView.ViewHolder {

        private FooterHolder(View itemView) {
            super(itemView);
        }

        public static FooterHolder buildInstance(ViewGroup parent) {
            return new FooterHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        }

        public void setColor(@ColorInt int color) {
            itemView.setBackgroundColor(color);
        }

        public void setAlpha(@FloatRange(from=0.0, to=1.0) float alpha) {
            itemView.setAlpha(alpha);
        }
    }

    protected abstract boolean hasFooter(Context context);

    protected boolean isFooter(int position) {
        return hasFooter && position == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + (hasFooter ? 1 : 0);
    }

    public abstract int getRealItemCount();
}

