package com.wangdaye.mysplash.about.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.about.model.CategoryAboutObject;

/**
 * Category holder.
 * */

public class CategoryHolder extends RecyclerView.ViewHolder {

    /** <br> life cycle. */

    public CategoryHolder(View itemView, CategoryAboutObject object) {
        super(itemView);

        TextView text = (TextView) itemView.findViewById(R.id.item_about_category_title);
        text.setText(object.category);
    }
}
