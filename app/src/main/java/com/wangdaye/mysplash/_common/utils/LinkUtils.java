package com.wangdaye.mysplash._common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Link utils.
 * */

public class LinkUtils {

    public static void accessLink(Context c, String link) {
        Uri uri = Uri.parse(link);
        c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
