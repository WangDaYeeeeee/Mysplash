package com.wangdaye.downloader.ui;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;

public class DownloadItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;
    private int cardRadius;

    private int adapterPosition;
    private boolean footer;

    public DownloadItemDecoration(Context context) {
        this(
                context.getResources().getDimensionPixelSize(R.dimen.normal_margin),
                context.getResources().getDimensionPixelSize(R.dimen.material_card_radius)
        );
    }

    public DownloadItemDecoration(int margin, int cardRadius) {
        this.margin = margin;
        this.cardRadius = cardRadius;

        this.adapterPosition = -1;
        this.footer = false;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        parent.setClipToPadding(false);
        parent.setPadding(margin / 2, margin / 2, margin / 2, margin / 2);

        GridLayoutManager.LayoutParams params
                = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        adapterPosition = params.getViewAdapterPosition();

        footer = false;
        if (parent.getAdapter() instanceof FooterAdapter) {
            footer = ((FooterAdapter) parent.getAdapter()).isFooter(adapterPosition);
        }
        if (footer) {
            return;
        }

        if (view instanceof CardView) {
            ((CardView) view).setRadius(cardRadius);
        }

        outRect.set(margin / 2, margin / 2, margin / 2, margin / 2);
    }
}
