package com.wangdaye.common.presenter.pager;

import com.wangdaye.base.i.PagerView;
import com.wangdaye.common.ui.widget.MultipleStateRecyclerView;

public class PagerStateManagePresenter {

    private MultipleStateRecyclerView recyclerView;
    private PagerView.State state;

    public PagerStateManagePresenter(MultipleStateRecyclerView recyclerView) {
        this(recyclerView, stateMapping(recyclerView.getState()));
    }

    public PagerStateManagePresenter(MultipleStateRecyclerView recyclerView,
                                     PagerView.State state) {
        this.recyclerView = recyclerView;
        this.state = state;
    }

    public PagerView.State getState() {
        return state;
    }

    public boolean setState(PagerView.State state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case ERROR:
                    recyclerView.setState(MultipleStateRecyclerView.STATE_ERROR);
                    break;

                case LOADING:
                    recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);
                    break;

                case NORMAL:
                    recyclerView.setState(MultipleStateRecyclerView.STATE_NORMALLY);
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    private static PagerView.State stateMapping(@MultipleStateRecyclerView.StateRule int state) {
        switch (state) {
            case MultipleStateRecyclerView.STATE_ERROR:
                return PagerView.State.ERROR;

            case MultipleStateRecyclerView.STATE_LOADING:
                return PagerView.State.LOADING;

            case MultipleStateRecyclerView.STATE_NORMALLY:
                return PagerView.State.NORMAL;
        }
        return PagerView.State.ERROR;
    }
}
