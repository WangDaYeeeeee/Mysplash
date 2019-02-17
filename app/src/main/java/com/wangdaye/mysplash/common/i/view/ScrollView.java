package com.wangdaye.mysplash.common.i.view;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Scroll view.
 *
 * A view which has a scrollable view like {@link RecyclerView}. By
 * implementing this interface, view can help parent view to control the scrollable view.
 *
 * */

public interface ScrollView {

    boolean needBackToTop();
    void scrollToTop();

    void autoLoad(int dy);
}
