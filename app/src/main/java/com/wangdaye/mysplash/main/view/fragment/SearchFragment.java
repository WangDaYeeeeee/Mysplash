package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.i.view.SearchBarView;
import com.wangdaye.mysplash.main.model.fragment.PagerManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.MessageManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.PagerManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.SearchBarImplementor;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.widget.HomeSearchView;
import com.wangdaye.mysplash._common.utils.SafeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Search Fragment.
 * */

public class SearchFragment extends Fragment
        implements SearchBarView, MessageManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        NotificationUtils.SnackbarContainer, ViewPager.OnPageChangeListener, SafeHandler.HandlerContainer {
    // model.
    private PagerManageModel pagerManageModel;

    // view.
    private CoordinatorLayout container;
    private EditText editText;
    private PagerView[] pagers = new PagerView[3];

    private SafeHandler<SearchFragment> handler;

    // presenter.
    private SearchBarPresenter searchBarPresenter;
    private MessageManagePresenter messageManagePresenter;
    private PagerManagePresenter pagerManagePresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initModel();
        initView(view);
        initPresenter();
        messageManagePresenter.sendMessage(1, null);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (PagerView p : pagers) {
            p.cancelRequest();
        }
        searchBarPresenter.hideKeyboard();
        handler.removeCallbacksAndMessages(null);
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.searchBarPresenter = new SearchBarImplementor(this);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
    }

    /** <br> view. */

    // init.

    private void initView(View v) {
        this.handler = new SafeHandler<>(this);

        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_search_statusBar);
        if (ThemeUtils.getInstance(getActivity()).isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_search_container);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_search_toolbar);
        if (ThemeUtils.getInstance(getActivity()).isLightTheme()) {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) v.findViewById(R.id.fragment_search_editText);
        TypefaceUtils.setTypeface(getActivity(), editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();

        initPages(v);
    }

    private void initPages(View v) {
        List<View> pageList = new ArrayList<>();
        pageList.add(new HomeSearchView(getActivity(), HomeSearchView.SEARCH_PHOTOS_TYPE));
        pageList.add(new HomeSearchView(getActivity(), HomeSearchView.SEARCH_COLLECTIONS_TYPE));
        pageList.add(new HomeSearchView(getActivity(), HomeSearchView.SEARCH_USERS_TYPE));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
            pageList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBarPresenter.hideKeyboard();
                }
            });
        }

        List<String> tabList = new ArrayList<>();
        tabList.add("PHOTOS");
        tabList.add("COLLECTIONS");
        tabList.add("USERS");
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.fragment_search_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_search_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    // interface.

    public void pagerBackToTop() {
        pagerManagePresenter.pagerScrollToTop();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.pagerManageModel = new PagerManageObject(0);
    }

    // interface.

    public boolean needPagerBackToTop() {
        return pagerManagePresenter.needPagerBackToTop();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                searchBarPresenter.touchNavigatorIcon(getActivity());
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return searchBarPresenter.touchMenuItem(getActivity(), item.getItemId());
    }

    // on editor action clickListener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        if (!text.equals("")) {
            searchBarPresenter.submitSearchInfo(text);
        }
        searchBarPresenter.hideKeyboard();
        return true;
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        pagerManagePresenter.setPagerPosition(position);
        pagerManagePresenter.checkToRefresh(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(getActivity(), message.what, message.obj);
    }

    // view.

    // search bar view.

    @Override
    public void clearSearchBarText() {
        editText.setText("");
    }

    @Override
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, 0);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void submitSearchInfo(String text) {
        for (PagerView p : pagers) {
            p.setKey(text);
            p.cancelRequest();
            ((HomeSearchView) p).clearAdapter();
        }
        pagerManagePresenter.getPagerView(pagerManagePresenter.getPagerPosition()).refreshPager();
    }

    // message manage view.

    @Override
    public void sendMessage(final int what, final Object o) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, o).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case 1:
                showKeyboard();
                break;
        }
    }

    // page manage view.

    @Override
    public PagerView getPagerView(int position) {
        return pagers[position];
    }

    @Override
    public boolean canPagerSwipeBack(int position, int dir) {
        return false;
    }

    @Override
    public int getPagerItemCount(int position) {
        return pagers[position].getItemCount();
    }
}
