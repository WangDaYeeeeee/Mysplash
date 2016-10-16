package com.wangdaye.mysplash._common.utils;

import android.app.Activity;
import android.content.Intent;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.User;

/**
 * Shre utils.
 * */

public class ShareUtils {

    public static void sharePhoto(Photo p) {
        Activity a = Mysplash.getInstance().getTopActivity();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                a.getString(R.string.feedback_share_photo_title));
        intent.putExtra(
                Intent.EXTRA_TITLE,
                a.getString(R.string.feedback_share_photo_title));
        intent.putExtra(
                Intent.EXTRA_TEXT,
                a.getString(R.string.feedback_share_photo_extra)
                        .replaceFirst("#", p.user.name)
                        .replaceFirst("#", p.created_at.split("T")[0])
                        + "https://unsplash.com/photos/" + p.id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(
                Intent.createChooser(
                        intent,
                        a.getString(R.string.action_share)));
    }

    public static void shareCollection(Collection c) {
        Activity a = Mysplash.getInstance().getTopActivity();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                a.getString(R.string.feedback_share_collection_title));
        intent.putExtra(
                Intent.EXTRA_TITLE,
                a.getString(R.string.feedback_share_collection_title));
        intent.putExtra(
                Intent.EXTRA_TEXT,
                a.getString(R.string.feedback_share_collection_extra)
                        .replaceFirst("#", c.user.name)
                        .replaceFirst("#", c.published_at.split("T")[0])
                        + (c.curated ?
                        ("https://unsplash.com/collections/curated/" + c.id)
                        :
                        ("https://unsplash.com/collections/" + c.id)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(
                Intent.createChooser(
                        intent,
                        a.getString(R.string.action_share)));
    }

    public static void shareUser(User u) {
        Activity a = Mysplash.getInstance().getTopActivity();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                a.getString(R.string.feedback_share_user_title));
        intent.putExtra(
                Intent.EXTRA_TITLE,
                a.getString(R.string.feedback_share_user_title));
        intent.putExtra(
                Intent.EXTRA_TEXT,
                a.getString(R.string.feedback_share_user_extra)
                        .replaceFirst("#", u.name)
                        + "https://unsplash.com/" + "@" + u.username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(
                Intent.createChooser(
                        intent,
                        a.getString(R.string.action_share)));
    }
}
