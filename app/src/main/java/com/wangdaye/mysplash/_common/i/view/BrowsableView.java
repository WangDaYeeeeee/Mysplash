package com.wangdaye.mysplash._common.i.view;

/**
 * Browsable view.
 * */

public interface BrowsableView {

    void showRequestDialog();
    void dismissRequestDialog();

    void drawBrowsableView();
    void visitParentView();
}
