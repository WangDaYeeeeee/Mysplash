package com.wangdaye.common.presenter;

import com.google.android.material.tabs.TabLayout;

public class TabLayoutDoubleClickBackToTopPresenter implements TabLayout.OnTabSelectedListener {

    private Executor executor;
    private boolean clicked;
    private long clickedTimeStamp;
    private int currentPosition;

    public TabLayoutDoubleClickBackToTopPresenter(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        clicked = false;
        currentPosition = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // do nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (position == currentPosition) {
            long currentTime = System.currentTimeMillis();
            if (clicked && currentTime - clickedTimeStamp <= 300) {
                executor.backToTop();
                clicked = false;
            } else {
                clicked = true;
                clickedTimeStamp = currentTime;
            }
        }
    }

    public interface Executor {
        void backToTop();
    }
}
