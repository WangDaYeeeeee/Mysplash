package com.wangdaye.mysplash._common.i.model;

/**
 * About model.
 * */

public interface AboutModel {
    int TYPE_HEADER = 1;
    int TYPE_CATEGORY = 2;
    int TYPE_APP = 3;
    int TYPE_TRANSLATOR = 4;
    int TYPE_LIBRARY = 5;

    int getType();
}
