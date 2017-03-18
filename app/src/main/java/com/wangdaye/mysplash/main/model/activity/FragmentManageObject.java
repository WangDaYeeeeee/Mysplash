package com.wangdaye.mysplash.main.model.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment mange object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private List<Integer> idList;

    /** <br> life cycle. */

    public FragmentManageObject(@Nullable List<Integer> list, Intent intent) {
        this.idList = new ArrayList<>();
        if (list != null) {
            idList.addAll(list);
        }
        if (idList.size() == 0) {
            idList.add(R.id.action_home);
            if (intent != null && !TextUtils.isEmpty(intent.getAction())
                    && intent.getAction().equals("com.wangdaye.mysplash.Search")) {
                idList.add(R.id.action_search);
            }
        }
    }

    /** <br> model. */

    @Override
    public List<Integer> getIdList() {
        return idList;
    }

    @Override
    public int getFragmentCount() {
        return idList.size();
    }

    @Override
    public void addFragmentToList(int id) {
        idList.add(id);
    }

    @Override
    public void popFragmentFromList() {
        idList.remove(idList.size() - 1);
    }
}
