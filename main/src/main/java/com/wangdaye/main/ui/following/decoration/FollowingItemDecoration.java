package com.wangdaye.main.ui.following.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarRecyclerView;
import com.wangdaye.main.R;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;

public class FollowingItemDecoration extends RecyclerView.ItemDecoration {

    private int marginGridItem;
    private Rect marginSingleSpanPhoto;

    private Rect insets;
    private @Px int parentPaddingLeft;
    private @Px int parentPaddingRight;
    private @Px int parentPaddingTop;
    private @Px int parentPaddingBottom;

    public FollowingItemDecoration(Context context, MultipleStateRecyclerView recyclerView) {
        this.marginGridItem = context.getResources().getDimensionPixelOffset(R.dimen.normal_margin);
        this.marginSingleSpanPhoto = new Rect(
                context.getResources().getDimensionPixelSize(R.dimen.large_icon_size),
                marginGridItem,
                marginGridItem,
                marginGridItem
        );

        setParentPadding(recyclerView, getWindowInset(recyclerView));
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

        parentPaddingLeft = parent.getPaddingLeft();
        parentPaddingRight = parent.getPaddingRight();
        parentPaddingTop = parent.getPaddingTop();
        parentPaddingBottom = parent.getPaddingBottom();
        insets = getWindowInset(parent);
        if (parentPaddingLeft != insets.left
                || parentPaddingRight != insets.right
                || parentPaddingTop != 0
                || parentPaddingBottom != insets.bottom) {
            setParentPadding(parent, insets);
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

    private void setParentPadding(RecyclerView parent, Rect insets) {
        parent.setPadding(insets.left, 0, insets.right, insets.bottom);
        parent.setClipToPadding(false);
    }

    private Rect getWindowInset(RecyclerView parent) {
        if (parent instanceof FitBottomSystemBarRecyclerView) {
            return ((FitBottomSystemBarRecyclerView) parent).getWindowInsets();
        } else {
            return new Rect();
        }
    }
}
