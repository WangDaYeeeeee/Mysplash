package com.wangdaye.common.ui.adapter;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarBothWaySwipeRefreshLayout;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarNestedScrollView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarRecyclerView;
import com.wangdaye.common.ui.widget.insets.FitBottomSystemBarViewPager;

import java.util.List;

/**
 * My pager adapter.
 *
 * Adapter for {@link ViewPager}.
 *
 * */

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private FitBottomSystemBarViewPager pager;
    private List<View> viewList;
    public List<String> titleList;

    public PagerAdapter(FitBottomSystemBarViewPager pager,
                        List<View> viewList, List<String> titleList) {
        this.pager = pager;
        this.viewList = viewList;
        this.titleList = titleList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        setWindowInsetsForViewTree(viewList.get(position), pager.getWindowInsets());
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    private void setWindowInsetsForViewTree(View view, Rect insets) {
        setWindowInsets(view, insets);
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i ++) {
                setWindowInsetsForViewTree(((ViewGroup) view).getChildAt(i), insets);
            }
        }
    }

    private void setWindowInsets(View view, Rect insets) {
        if (view instanceof FitBottomSystemBarBothWaySwipeRefreshLayout) {
            ((FitBottomSystemBarBothWaySwipeRefreshLayout) view).fitSystemWindows(insets);
        } else if (view instanceof FitBottomSystemBarNestedScrollView) {
            ((FitBottomSystemBarNestedScrollView) view).fitSystemWindows(insets);
        } else if (view instanceof FitBottomSystemBarRecyclerView) {
            ((FitBottomSystemBarRecyclerView) view).fitSystemWindows(insets);
        }
    }
}
