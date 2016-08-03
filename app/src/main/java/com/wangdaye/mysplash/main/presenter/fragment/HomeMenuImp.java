package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.main.model.fragment.i.OrderModel;
import com.wangdaye.mysplash.main.model.fragment.i.PagerModel;
import com.wangdaye.mysplash.main.presenter.fragment.i.HomeMenuPresenter;
import com.wangdaye.mysplash.main.view.dialog.SelectOrderDialog;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.main.view.fragment.SearchFragment;
import com.wangdaye.mysplash.main.view.fragment.i.FragmentView;
import com.wangdaye.mysplash.main.view.fragment.i.PagerView;

/**
 * Home menu implementor.
 * */

public class HomeMenuImp
        implements HomeMenuPresenter,
        SelectOrderDialog.OnOrderSelectedListener {
    // model.
    private OrderModel orderModel;
    private PagerModel pagerModel;

    // view.
    private FragmentView fragmentView;
    private PagerView pagerView;

    /** <br> life cycle. */

    public HomeMenuImp(OrderModel orderModel, PagerModel pagerModel,
                       FragmentView fragmentView, PagerView pagerView) {
        this.orderModel = orderModel;
        this.pagerModel = pagerModel;
        this.fragmentView = fragmentView;
        this.pagerView = pagerView;
    }

    /** <br> presenter. */

    @Override
    public void clickSearchItem() {
        SearchFragment f = new SearchFragment();
        fragmentView.addFragment(f);
    }

    @Override
    public void clickOrderItem(Context c) {
        SelectOrderDialog selectOrderDialog = new SelectOrderDialog(
                c,
                orderModel.getOrder(),
                ModeUtils.getInstance(c).isNormalMode());
        selectOrderDialog.setOnOrderSelectedListener(this);
        selectOrderDialog.show();
    }

    @Override
    public void clickRandomItem(Context c) {
        saveMode(c, false);
        HomeFragment f = new HomeFragment();
        fragmentView.changeFragment(f);
    }

    @Override
    public void clickNormalItem(Context c) {
        saveMode(c, true);
        HomeFragment f = new HomeFragment();
        fragmentView.changeFragment(f);
    }

    private void saveMode(Context c, boolean normalMode) {
        SharedPreferences.Editor editor1 = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor1.putBoolean(
                c.getString(R.string.key_normal_mode),
                normalMode);
        editor1.apply();
        ModeUtils.getInstance(c).refresh(c);
    }

    /** <br> interface. */

    // on order selected listener.

    @Override
    public void onOrderSelect(String order) {
        if (!orderModel.getOrder().equals(order)) {
            orderModel.setOrder(order);
            pagerView.resetPage(pagerModel.getPage(), order);
        }
    }
}
