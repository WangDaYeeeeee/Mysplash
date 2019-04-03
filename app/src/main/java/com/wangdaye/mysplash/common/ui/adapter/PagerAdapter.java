package com.wangdaye.mysplash.common.ui.adapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * My pager adapter.
 *
 * Adapter for {@link ViewPager}.
 *
 * */

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private List<View> viewList;
    public List<String> titleList;

    public PagerAdapter(List<View> viewList, List<String> titleList) {
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
}
