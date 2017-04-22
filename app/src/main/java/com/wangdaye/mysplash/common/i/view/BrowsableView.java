package com.wangdaye.mysplash.common.i.view;

/**
 * Browsable view.
 *
 * If a view need to respond a web page request, it should implement this interface.
 *
 * */

public interface BrowsableView {

    /**
     * A dialog to tell user that the progress is loading data. For example,
     * {@link com.wangdaye.mysplash.photo.view.activity.PhotoActivity} implements this interface.
     * When it was opened by a web url intent like <a href="https://unsplash.com/photos/pFqrYbhIAXs" />,
     * it need to request {@link com.wangdaye.mysplash.common.data.entity.unsplash.Photo} data
     * by a HTTP request.
     * */
    void showRequestDialog();
    void dismissRequestDialog();

    /**
     * After loading data, the view will show those data, just like a normal activity.
     * */
    void drawBrowsableView(Object result);

    void visitPreviousPage();
}
