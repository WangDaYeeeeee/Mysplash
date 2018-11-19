package com.wangdaye.mysplash.common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class MultipleStateRecyclerView extends RecyclerView {

    private Adapter[] multipleAdapters;
    private LayoutManager[] multipleLayouts;
    private List<OnScrollListener> onScrollListenerList;

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

        state = STATE_LOADING;
        setLayoutManager(multipleLayouts[STATE_LOADING], STATE_LOADING);
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
            animSwitchState(state);
        }
    }

    @StateRule
    public int getState() {
        return state;
    }

    private void animSwitchState(@StateRule final int state) {
        clearAnimation();
        if (getAlpha() == 0) {
            animShow(state);
        } else {
            ObjectAnimator hide = ObjectAnimator
                    .ofFloat(this, "alpha", getAlpha(), 0)
                    .setDuration(150);
            hide.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animShow(state);
                }
            });
            hide.start();
        }
    }

    private void animShow(@StateRule int state) {
        this.state = state;
        setAdapter(multipleAdapters[state], state);
        setLayoutManager(multipleLayouts[state], state);
        if (state == STATE_NORMALLY) {
            for (OnScrollListener l : onScrollListenerList) {
                super.addOnScrollListener(l);
            }
        } else {
            super.clearOnScrollListeners();
        }
        ObjectAnimator
                .ofFloat(this, "alpha", 0, 1F)
                .setDuration(150)
                .start();
    }
}