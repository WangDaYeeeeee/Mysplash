package com.wangdaye.main.ui.following.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.main.R;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;

public class FollowingItemDecoration extends RecyclerView.ItemDecoration {

    private int marginGridItem;
    private Rect marginSingleSpanPhoto;

    public FollowingItemDecoration(Context context) {
        this.marginGridItem = context.getResources().getDimensionPixelOffset(R.dimen.normal_margin);
        this.marginSingleSpanPhoto = new Rect(
                context.getResources().getDimensionPixelSize(R.dimen.large_icon_size),
                marginGridItem,
                marginGridItem,
                marginGridItem
        );
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        FollowingAdapter adapter = (FollowingAdapter) parent.getAdapter();
        if (adapter == null) {
            return;
        }

        StaggeredGridLayoutManager.LayoutParams params
                = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        int spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        int spanIndex = params.getSpanIndex();

        if (adapter.isPhotoItem(params.getViewAdapterPosition())) {
            if (spanCount == 1) {
                outRect.set(
                        marginSingleSpanPhoto.left,
                        0,
                        marginSingleSpanPhoto.right,
                        marginSingleSpanPhoto.bottom
                );
            } else {
                if (spanIndex == 0) {
                    outRect.set(marginGridItem, 0, marginGridItem, marginGridItem);
                } else {
                    outRect.set(0, 0, marginGridItem, marginGridItem);
                }
            }
        }
    }
}
