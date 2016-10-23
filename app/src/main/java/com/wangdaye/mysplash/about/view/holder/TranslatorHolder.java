package com.wangdaye.mysplash.about.view.holder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash.about.model.TranslatorObject;

/**
 * Translator holder.
 * */

public class TranslatorHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    // widget
    public CircleImageView avatar;
    public ImageView flag;

    // data
    private String email;

    /** <br> life cycle. */

    public TranslatorHolder(View itemView, TranslatorObject object) {
        super(itemView);

        itemView.findViewById(R.id.item_about_translator_container).setOnClickListener(this);

        this.avatar = (CircleImageView) itemView.findViewById(R.id.item_about_translator_avatar);

        TextView title = (TextView) itemView.findViewById(R.id.item_about_translator_title);
        title.setText(object.title);

        this.flag = (ImageView) itemView.findViewById(R.id.item_about_translator_flag);

        TextView subtitle = (TextView) itemView.findViewById(R.id.item_about_translator_subtitle);
        subtitle.setText(object.subtitle);
        DisplayUtils.setTypeface(itemView.getContext(), subtitle);

        this.email = object.subtitle;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_translator_container:
                Uri email = Uri.parse("mailto:" + this.email);
                v.getContext().startActivity(new Intent(Intent.ACTION_SENDTO, email));
                break;
        }
    }
}
