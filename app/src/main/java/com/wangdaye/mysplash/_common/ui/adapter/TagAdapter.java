package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Category;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

/**
 * Tag adapter.
 * */

public class TagAdapter extends com.zhy.view.flowlayout.TagAdapter<Category> {

    /** <br> life cycle. */

    public TagAdapter(List<Category> categoryList) {
        super(categoryList);
    }

    /** <br> UI. */

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(FlowLayout parent, int position, Category category) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        v.setText("#" + category.title.toUpperCase());
        return v;
    }
}
