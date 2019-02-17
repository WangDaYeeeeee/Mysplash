package com.wangdaye.mysplash.common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class MultipleStateRecyclerView extends RecyclerView {

    private Adapter[] multipleAdapters;
    private LayoutManager[] multipleLayouts;
    private List<OnScrollListener> onScrollListenerList;

    private ObjectAnimator animator;

    private int paddingStart;
    private int paddingTop;
    private int paddingEnd;
    private int paddingBottom;

    private boolean layoutFinished;

    @StateRule
    private int state;

    public static final int STATE_NORMALLY = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    @IntDef({
            STATE_NORMALLY,
            STATE_LOADING,
            STATE_ERROR})
    public @interface StateRule {}

    public MultipleStateRecyclerView(@NonNull Context context) {
        super(context);
        this.initialize(context);
    }

    public MultipleStateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
    }

    public MultipleStateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context);
    }

    private void initialize(Context context) {
        multipleAdapters = new Adapter[STATE_ERROR + 1];
        multipleLayouts = new LayoutManager[] {
                null,
                new LinearLayoutManager(context),
                new LinearLayoutManager(context)};
        onScrollListenerList = new ArrayList<>();

        paddingStart = paddingEnd = paddingTop = paddingBottom = 0;

        state = STATE_LOADING;
        setLayoutManager(multipleLayouts[STATE_LOADING], STATE_LOADING);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.layoutFinished = true;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        this.setPaddingRelative(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        if (state == STATE_NORMALLY) {
            super.setPaddingRelative(start, top, end, bottom);
        }
        paddingStart = start;
        paddingTop = top;
        paddingEnd = end;
        paddingBottom = bottom;
    }

    @Override
    public void addOnScrollListener(@NonNull RecyclerView.OnScrollListener listener) {
        if (state == STATE_NORMALLY) {
            super.addOnScrollListener(listener);
        }
        onScrollListenerList.add(listener);
    }

    @Override
    public void removeOnScrollListener(@NonNull RecyclerView.OnScrollListener listener) {
        if (state == STATE_NORMALLY) {
            super.removeOnScrollListener(listener);
        }
        onScrollListenerList.remove(listener);
    }

    @Override
    public void clearOnScrollListeners() {
        if (state == STATE_NORMALLY) {
            super.clearOnScrollListeners();
        }
        onScrollListenerList.clear();
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        setLayoutManager(layout, STATE_NORMALLY);
    }

    public void setLayoutManager(@Nullable LayoutManager layout, @StateRule int state) {
        if (getState() == state) {
            super.setLayoutManager(layout);
        }
        multipleLayouts[state] = layout;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        setAdapter(adapter, STATE_NORMALLY);
    }

    public void setAdapter(@Nullable Adapter adapter, @StateRule int state) {
        if (getState() == state) {
            super.setAdapter(adapter);
        }
        multipleAdapters[state] = adapter;
    }

    public void setState(@StateRule int state) {
        if (getState() != state) {
            if (layoutFinished) {
                animSwitchState(state);
            } else {
                bindStateData(state);
            }
        }
    }

    @StateRule
    public int getState() {
        return state;
    }

    private void animSwitchState(@StateRule final int state) {
        if (animator != null) {
            animator.cancel();
        }
        if (getAlpha() == 0) {
            animShow(state);
        } else {
            animator = ObjectAnimator
                    .ofFloat(this, "alpha", getAlpha(), 0)
                    .setDuration(150);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animator = null;
                    animShow(state);
                }
            });
            animator.start();
        }
    }

    private void bindStateData(@StateRule int state) {
        this.state = state;
        setAdapter(multipleAdapters[state], state);
        setLayoutManager(multipleLayouts[state], state);
        if (state == STATE_NORMALLY) {
            for (OnScrollListener l : onScrollListenerList) {
                super.addOnScrollListener(l);
            }
            super.setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);
        } else {
            super.clearOnScrollListeners();
            super.setPaddingRelative(0, 0, 0, 0);
        }
    }

    private void animShow(@StateRule int state) {
        bindStateData(state);
        if (animator != null) {
            animator.cancel();
        }
        animator = ObjectAnimator
                .ofFloat(this, "alpha", 0, 1F)
                .setDuration(150);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator = null;
            }
        });
        animator.start();
    }
}