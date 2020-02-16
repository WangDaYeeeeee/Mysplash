package com.wangdaye.photo.ui.adapter.photo.model;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class ExifModel extends PhotoInfoAdapter3.ViewModel {

    public @ColorInt int color;
    public @DrawableRes int iconId;
    public String title;
    public String content;

    public ExifModel(Photo photo, @DrawableRes int iconId, String title, String content) {
        this(photo, iconId, title, content, Color.TRANSPARENT);
    }

    public ExifModel(Photo photo, @DrawableRes int iconId, String title, String content,
                     @ColorInt int color) {
        super(photo);
        this.iconId = iconId;
        this.title = title;
        this.content = content;
        this.color = color;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof ExifModel && ((ExifModel) newModel).iconId == iconId;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ((ExifModel) newModel).color == color
                && ((ExifModel) newModel).iconId == iconId
                && ((ExifModel) newModel).title.equals(title)
                && ((ExifModel) newModel).content.equals(content);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
