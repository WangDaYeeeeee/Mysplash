package com.wangdaye.base.pager;

import androidx.annotation.IntRange;

public class ListPager {

    public static final int DEFAULT_PER_PAGE = 10;

    @IntRange(from = 1, to = 30)
    public @interface PerPageRule {}

    @IntRange(from = 1)
    public @interface PageRule {}
}
