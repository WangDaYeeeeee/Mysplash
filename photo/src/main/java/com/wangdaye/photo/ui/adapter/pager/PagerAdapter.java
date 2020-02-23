package com.wangdaye.photo.ui.adapter.pager;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.photoView.OnScaleChangedListener;

import java.util.List;

public class PagerAdapter extends BaseAdapter<Photo, PagerModel, PagerHolder> {

    private boolean executeEnterTransition;
    private float currentScale;

    private OnScaleChangedListener onScaleChangedListener = new OnScaleChangedListener() {
        @Override
        public void onScaleChange(float scaleFactor, float focusX, float focusY) {
            currentScale = scaleFactor;
        }
    };

    public PagerAdapter(PhotoActivity activity, @NonNull List<Photo> list) {
        super(activity, list);
        this.executeEnterTransition = true;
        this.currentScale = 1;
    }

    @NonNull
    @Override
    public PagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PagerHolder(parent, onScaleChangedListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull PagerHolder holder, PagerModel model) {
        holder.onBindView((PhotoActivity) getContext(), model, false);
    }

    @Override
    protected void onBindViewHolder(@NonNull PagerHolder holder, PagerModel model,
                                    @NonNull List<Object> payloads) {
        holder.onBindView((PhotoActivity) getContext(), model, !payloads.isEmpty());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PagerHolder holder) {
        holder.onAttachView(executeEnterTransition);
        executeEnterTransition = false;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PagerHolder holder) {
        holder.onDetachView();
    }

    @Override
    public void onViewRecycled(@NonNull PagerHolder holder) {
        holder.onRecycledView();
    }

    @Override
    protected PagerModel getViewModel(Photo model) {
        return new PagerModel(getContext(), model);
    }

    public float getCurrentScale() {
        return currentScale;
    }
}
