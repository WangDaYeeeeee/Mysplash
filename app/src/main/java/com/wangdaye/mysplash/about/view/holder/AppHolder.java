package com.wangdaye.mysplash.about.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.AppAboutObject;

/**
 * App holder.
 * */

public class AppHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    // data
    private int id;

    /** <br> life cycle. */

    public AppHolder(View itemView, AppAboutObject object) {
        super(itemView);

        itemView.findViewById(R.id.item_about_app_container).setOnClickListener(this);

        ImageView icon = (ImageView) itemView.findViewById(R.id.item_about_app_icon);
        icon.setImageResource(object.iconId);

        TextView text = (TextView) itemView.findViewById(R.id.item_about_app_title);
        text.setText(object.text);

        this.id = object.id;
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_about_app_container:
                switch (id) {
                    case 1:
                        IntroduceActivity.watchAllIntroduce(Mysplash.getInstance().getTopActivity());
                        break;

                    case 2:
                        IntentHelper.startWebActivity(v.getContext(), "https://github.com/WangDaYeeeeee", false);
                        break;

                    case 3:
                        IntentHelper.startWebActivity(v.getContext(), "mailto:wangdayeeeeee@gmail.com", false);
                        break;

                    case 4:
                        IntentHelper.startWebActivity(v.getContext(), "https://github.com/WangDaYeeeeee/MySplash", false);
                        break;
                }
                break;
        }
    }
}
