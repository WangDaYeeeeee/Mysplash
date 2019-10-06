package com.wangdaye.base.pager;

import androidx.annotation.IntDef;

public class ProfilePager {

    public static final int PAGE_PHOTO = 0;
    public static final int PAGE_LIKE = 1;
    public static final int PAGE_COLLECTION = 2;

    @IntDef({PAGE_PHOTO, PAGE_LIKE, PAGE_COLLECTION})
    public @interface ProfilePagerRule {}
}
