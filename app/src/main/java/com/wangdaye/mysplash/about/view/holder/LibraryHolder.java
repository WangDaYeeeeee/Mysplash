package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.LibraryObject;

/**
 * Library holder.
 * */

public class LibraryHolder extends AboutAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private TextView title;
    private TextView content;

    // data
    private String uri;

    /** <br> life cycle. */

    public LibraryHolder(View itemView) {
        super(itemView);

        itemView.findViewById(R.id.item_about_library_container).setOnClickListener(this);

        this.title = (TextView) itemView.findViewById(R.id.item_about_library_title);
        DisplayUtils.setTypeface(itemView.getContext(), title);

        this.content = (TextView) itemView.findViewById(R.id.item_about_library_content);
        DisplayUtils.setTypeface(itemView.getContext(), content);
    }

    /** <br> UI. */

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        LibraryObject object = (LibraryObject) model;

        title.setText(object.title);
        content.setText(object.subtitle);
        uri = object.uri;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
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
