package com.wangdaye.mysplash.about.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.LibraryObject;

/**
 * Library holder.
 * */

public class LibraryHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    // data
    private String uri;

    /** <br> life cycle. */

    public LibraryHolder(View itemView, LibraryObject object) {
        super(itemView);

        itemView.findViewById(R.id.item_about_library_container).setOnClickListener(this);

        TextView title = (TextView) itemView.findViewById(R.id.item_about_library_title);
        title.setText(object.title);
        DisplayUtils.setTypeface(itemView.getContext(), title);

        TextView content = (TextView) itemView.findViewById(R.id.item_about_library_content);
        content.setText(object.subtitle);
        DisplayUtils.setTypeface(itemView.getContext(), content);

        this.uri = object.uri;
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_library_container:
                IntentHelper.startWebActivity(v.getContext(), uri);
                break;
        }
    }
}
