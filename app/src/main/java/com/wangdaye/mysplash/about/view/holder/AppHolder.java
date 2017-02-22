package com.wangdaye.mysplash.about.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.activity.IntroduceActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.about.model.AppAboutObject;

/**
 * App holder.
 * */

public class AppHolder extends AboutAdapter.ViewHolder
        implements View.OnClickListener {
    // widget
    private ImageView icon;
    private TextView text;

    // data
    private int id;

    /** <br> life cycle. */

    public AppHolder(View itemView) {
        super(itemView);

        itemView.findViewById(R.id.item_about_app_container).setOnClickListener(this);

        this.icon = (ImageView) itemView.findViewById(R.id.item_about_app_icon);
        this.text = (TextView) itemView.findViewById(R.id.item_about_app_title);
    }

    /** <br> UI. */

    @Override
    public void onBindView(MysplashActivity a, AboutModel model) {
        AppAboutObject object = (AppAboutObject) model;

        icon.setImageResource(object.iconId);
        text.setText(object.text);
        id = object.id;
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
                        IntentHelper.startWebActivity(v.getContext(), "https://github.com/WangDaYeeeeee");
                        break;

                    case 3:
                        IntentHelper.startWebActivity(v.getContext(), "mailto:wangdayeeeeee@gmail.com");
                        break;

                    case 4:
                        IntentHelper.startWebActivity(v.getContext(), "https://github.com/WangDaYeeeeee/MySplash");
                        break;
                }
                break;
        }
    }
}
