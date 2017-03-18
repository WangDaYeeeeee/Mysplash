package com.wangdaye.mysplash._common.i.view;

import android.os.Bundle;

/**
 * Pager view.
 * */

public interface PagerView {

    void onSaveInstanceState(Bundle bundle);
    void onRestoreInstanceState(Bundle bundle);

    void checkToRefresh();
    boolean checkNeedRefresh();
    boolean checkNeedBackToTop();
    void refreshPager();

    void scrollToPageTop();
    void cancelRequest();

    void setKey(String key);
    String getKey();

    boolean canSwipeBack(int dir);
    int getItemCount();
}
