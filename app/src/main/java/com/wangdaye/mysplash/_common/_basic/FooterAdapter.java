package com.wangdaye.mysplash._common._basic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;

/**
 * Footer adapter.
 * */

public abstract class FooterAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected abstract boolean hasFooter();
    public abstract int getRealItemCount();

    protected boolean isFooter(int position) {
        return hasFooter() && position == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + (hasFooter() ? 1 : 0);
    }

    /** <br> inner class. */

    protected static class FooterHolder extends RecyclerView.ViewHolder {

        private FooterHolder(View itemView) {
            super(itemView);
        }

        public static FooterHolder buildInstance(ViewGroup parent) {
            return new FooterHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        }
    }
}

