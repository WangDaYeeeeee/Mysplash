package com.wangdaye.downloader.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.common.ui.widget.windowInsets.FitBottomSystemBarRecyclerView;
import com.wangdaye.downloader.R;

public class DownloadRecyclerView extends FitBottomSystemBarRecyclerView {

    public DownloadRecyclerView(@NonNull Context context) {
        super(context);
    }

    public DownloadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        int padding = getResources().getDimensionPixelSize(R.dimen.little_margin);
        setPadding(padding, padding, padding, padding + insets.bottom);
        setClipToPadding(false);
        return super.fitSystemWindows(insets);
    }
}
