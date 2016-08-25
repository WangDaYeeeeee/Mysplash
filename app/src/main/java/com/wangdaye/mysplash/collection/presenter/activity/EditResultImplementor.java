package com.wangdaye.mysplash.collection.presenter.activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.data.Collection;
import com.wangdaye.mysplash._common.i.model.EditResultModel;
import com.wangdaye.mysplash._common.i.presenter.EditResultPresenter;
import com.wangdaye.mysplash._common.i.view.EditResultView;

/**
 * Edit result implementor.
 * */

public class EditResultImplementor
        implements EditResultPresenter {
    // model & view.
    private EditResultModel model;
    private EditResultView view;

    /** <br> life cycle. */

    public EditResultImplementor(EditResultModel model, EditResultView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void createSomething(Object newKey) {
        // do nothing.
    }

    @Override
    public void updateSomething(Object newKey) {
        model.setEditKey(newKey);
        Mysplash.getInstance().setCollection((Collection) newKey);
        view.drawUpdateResult(newKey);
    }

    @Override
    public void deleteSomething(Object oldKey) {
        Mysplash.getInstance().setCollection((Collection) oldKey);
        view.drawDeleteResult(oldKey);
    }

    @Override
    public Object getEditKey() {
        return model.getEditKey();
    }
}
