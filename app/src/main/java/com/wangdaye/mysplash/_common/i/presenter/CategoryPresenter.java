package com.wangdaye.mysplash._common.i.presenter;

/**
 * Category presenter.
 * */

public interface CategoryPresenter {

    void requestPhotos(int page, boolean refresh);
    void cancelRequest();

    void refreshNew(boolean notify);
    void loadMore(boolean notify);
    void initRefresh();

    boolean canLoadMore();

    void setCategory(int key);
    void setOrder(String key);
}
