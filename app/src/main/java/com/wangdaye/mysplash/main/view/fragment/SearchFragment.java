package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
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

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common._basic.MysplashFragment;
import com.wangdaye.mysplash._common.ui.widget.AutoHideInkPageIndicator;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.i.view.SearchBarView;
import com.wangdaye.mysplash.main.model.fragment.PagerManageObject;
import com.wangdaye.mysplash.main.presenter.fragment.MessageManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.PagerManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.SearchBarImplementor;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.HomeSearchView;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Search Fragment.
 * */

public class SearchFragment extends MysplashFragment
        implements SearchBarView, MessageManageView, PagerManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SafeHandler.HandlerContainer {
    // model.
    private PagerManageModel pagerManageModel;

    // view.
    private StatusBarView statusBar;

    private CoordinatorLayout container;
    private NestedScrollAppBarLayout appBar;
    private EditText editText;
    private ViewPager viewPager;
    private AutoHideInkPageIndicator indicator;
    private PagerView[] pagers = new PagerView[3];

    private SafeHandler<SearchFragment> handler;

    // presenter.
    private SearchBarPresenter searchBarPresenter;
    private MessageManagePresenter messageManagePresenter;
    private PagerManagePresenter pagerManagePresenter;

    // data.
    private final String KEY_SEARCH_FRAGMENT_QUERY = "search_fragment_query";
    private final String KEY_SEARCH_FRAGMENT_PAGE_POSITION = "search_fragment_page_position";

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initModel(savedInstanceState);
        initPresenter();
        initView(view, savedInstanceState);
        messageManagePresenter.sendMessage(1, null);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (PagerView p : pagers) {
            if (p != null) {
                p.cancelRequest();
            }
        }
        searchBarPresenter.hideKeyboard();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_FRAGMENT_QUERY, editText.getText().toString());
        outState.putInt(KEY_SEARCH_FRAGMENT_PAGE_POSITION, pagerManagePresenter.getPagerPosition());
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public boolean needPagerBackToTop() {
        return pagerManagePresenter.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagerManagePresenter.pagerScrollToTop();
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (pagers[0] != null) {
            ((MainActivity.SavedStateFragment) outState).setSearchPhotoList(((HomeSearchView) pagers[0]).getPhotos());
        }
        if (pagers[1] != null) {
            ((MainActivity.SavedStateFragment) outState).setSearchCollectionList(((HomeSearchView) pagers[1]).getCollections());
        }
        if (pagers[2] != null) {
            ((MainActivity.SavedStateFragment) outState).setSearchUserList(((HomeSearchView) pagers[2]).getUsers());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (pagers[0] != null) {
            ((HomeSearchView) pagers[0]).setPhotos(((MainActivity.SavedStateFragment) savedInstanceState).getSearchPhotoList());
        }
        if (pagers[1] != null) {
            ((HomeSearchView) pagers[1]).setCollections(((MainActivity.SavedStateFragment) savedInstanceState).getSearchCollectionList());
        }
        if (pagers[2] != null) {
            ((HomeSearchView) pagers[2]).setUsers(((MainActivity.SavedStateFragment) savedInstanceState).getSearchUserList());
        }
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.searchBarPresenter = new SearchBarImplementor(this);
        this.messageManagePresenter = new MessageManageImplementor(this);
        this.pagerManagePresenter = new PagerManageImplementor(pagerManageModel, this);
    }

    /** <br> view. */

    private void initView(View v, Bundle savedInstanceState) {
        this.handler = new SafeHandler<>(this);

        this.statusBar = (StatusBarView) v.findViewById(R.id.fragment_search_statusBar);
        statusBar.setInitMaskAlpha();

        this.container = (CoordinatorLayout) v.findViewById(R.id.fragment_search_container);

        this.appBar = (NestedScrollAppBarLayout) v.findViewById(R.id.fragment_search_appBar);
        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_search_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_light);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.inflateMenu(R.menu.fragment_search_toolbar_dark);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) v.findViewById(R.id.fragment_search_editText);
        DisplayUtils.setTypeface(getActivity(), editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(KEY_SEARCH_FRAGMENT_QUERY));
        }

        initPages(v, savedInstanceState);
    }

    private void initPages(View v, Bundle savedInstanceState) {
        List<View> pageList = new ArrayList<>();
        pageList.add(
                new HomeSearchView(
                        (MainActivity) getActivity(),
                        HomeSearchView.SEARCH_PHOTOS_TYPE,
                        R.id.fragment_search_page_photo));
        pageList.add(
                new HomeSearchView(
                        (MainActivity) getActivity(),
                        HomeSearchView.SEARCH_COLLECTIONS_TYPE,
                        R.id.fragment_search_page_collection));
        pageList.add(
                new HomeSearchView(
                        (MainActivity) getActivity(),
                        HomeSearchView.SEARCH_USERS_TYPE,
                        R.id.fragment_search_page_user));
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
            pageList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBarPresenter.hideKeyboard();
                }
            });
        }

        String[] searchTabs = getResources().getStringArray(R.array.search_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        MyPagerAdapter adapter = new MyPagerAdapter(pageList, tabList);

        this.viewPager = (ViewPager) v.findViewById(R.id.fragment_search_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pagerManagePresenter.getPagerPosition(), false);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.fragment_search_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        this.indicator = (AutoHideInkPageIndicator) v.findViewById(R.id.fragment_search_indicator);
        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        if (savedInstanceState != null) {
            for (PagerView pager : pagers) {
                pager.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    /** <br> model. */

    private void initModel(Bundle saveInstanceState) {
        if (saveInstanceState != null) {
            this.pagerManageModel = new PagerManageObject(
                    saveInstanceState.getInt(KEY_SEARCH_FRAGMENT_PAGE_POSITION, 0));
        } else {
            this.pagerManageModel = new PagerManageObject(0);
        }
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                searchBarPresenter.touchNavigatorIcon((MysplashActivity) getActivity());
                break;
        }
    }

    // on menu item click swipeListener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return searchBarPresenter.touchMenuItem((MysplashActivity) getActivity(), item.getItemId());
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

    // on page change swipeListener.

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
        if (appBar.getY() <= -appBar.getMeasuredHeight()) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    indicator.setDisplayState(true);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    indicator.setDisplayState(false);
                    break;
            }
        }
    }

    // on nested scrolling swipeListener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitAlpha()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitAlpha()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage((MysplashActivity) getActivity(), message.what, message.obj);
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
