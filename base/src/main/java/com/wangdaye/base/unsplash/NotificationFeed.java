package com.wangdaye.base.unsplash;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Notification feed.
 * */

public class NotificationFeed implements Serializable {

    @Nullable public List<NotificationResult> results;
}
