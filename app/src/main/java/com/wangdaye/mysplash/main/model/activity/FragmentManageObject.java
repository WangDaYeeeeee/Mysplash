package com.wangdaye.mysplash.main.model.activity;

import android.content.Intent;
import android.text.TextUtils;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.FragmentManageModel;

/**
 * Fragment mange object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private int id;

    /** <br> life cycle. */

    public FragmentManageObject(int id, Intent intent) {
        this.id = id;
        if (this.id == 0) {
            this.id = R.id.action_home;
            if (intent != null && !TextUtils.isEmpty(intent.getAction())
                    && intent.getAction().equals("com.wangdaye.mysplash.Search")) {
                this.id = R.id.action_search;
            }
        }
    }

    /** <br> model. */

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
