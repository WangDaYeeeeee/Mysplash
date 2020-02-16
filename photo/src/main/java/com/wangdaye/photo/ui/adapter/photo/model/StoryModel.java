package com.wangdaye.photo.ui.adapter.photo.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoryModel extends PhotoInfoAdapter3.ViewModel {

    public String title;
    public String subtitle;
    public @Nullable String content;
    public User author;

    public StoryModel(Context context, Photo photo) {
        super(photo);

        if (photo.story != null && !TextUtils.isEmpty(photo.story.title)) {
            title = photo.story.title;
        } else {
            title = photo.user.name;
        }

        if (photo.location != null && !TextUtils.isEmpty(photo.location.title)) {
            subtitle = photo.location.title;
        } else {
            subtitle = DisplayUtils.getDate(context, photo.created_at);
        }

        if (photo.story != null && !TextUtils.isEmpty(photo.story.description)) {
            content = photo.story.description;
        } else if (!TextUtils.isEmpty(photo.description)) {
            content = capEveryWord(photo.description);
        } else {
            content = null;
        }

        author = photo.user;
    }

    private String capEveryWord(String str){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile(
                "([a-z])([a-z]*)",
                Pattern.CASE_INSENSITIVE
        ).matcher(str);
        while (capMatcher.find()){
            capMatcher.appendReplacement(
                    capBuffer,
                    capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
            );
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof StoryModel;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ((StoryModel) newModel).title.equals(title)
                && ((StoryModel) newModel).subtitle.equals(subtitle)
                && ImageHelper.isSameUrl(((StoryModel) newModel).content, content)
                && ((StoryModel) newModel).author.username.equals(author.username);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
