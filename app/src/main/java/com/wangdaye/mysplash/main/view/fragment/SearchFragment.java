package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.main.model.fragment.SearchObject;
import com.wangdaye.mysplash.main.model.fragment.i.SearchModel;
import com.wangdaye.mysplash.main.presenter.fragment.SearchBarImp;
import com.wangdaye.mysplash.main.presenter.fragment.i.SearchBarPresenter;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.common.widget.StatusBarView;
import com.wangdaye.mysplash.main.view.fragment.i.SearchBarView;
import com.wangdaye.mysplash.main.view.widget.SearchContentView;
import com.wangdaye.mysplash.common.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Search Fragment.
 * */

public class SearchFragment extends Fragment
        implements SearchBarView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        SafeHandler.HandlerContainer {
    // model.
    private SearchModel searchModel;

    // view.
    private EditText editText;
    private TextView orientationTxt;
    private ImageView menuIcon;
    private SearchContentView contentView;

    private SafeHandler<SearchFragment> handler;

    // presenter.
    private SearchBarPresenter searchBarPresenter;

    /** <br> life cycle. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initModel();
        initView(view);
        initPresenter();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(1).sendToTarget();
            }
        }, 400);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        contentView.cancelRequest();
        hideKeyboard();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.searchBarPresenter = new SearchBarImp(searchModel, this);
    }

    /** <br> view. */

    private void initView(View v) {
        this.handler = new SafeHandler<>(this);

        StatusBarView statusBar = (StatusBarView) v.findViewById(R.id.fragment_search_statusBar);
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            statusBar.setMask(true);
        }

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_search_toolbar);
        toolbar.inflateMenu(R.menu.menu_fragment_search_toolbar);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) v.findViewById(R.id.fragment_search_editText);
        editText.setOnEditorActionListener(this);
        editText.setFocusable(true);
        editText.requestFocus();

        FrameLayout orientationContainer = (FrameLayout) v.findViewById(R.id.fragment_search_orientationContainer);
        orientationContainer.setOnClickListener(this);

        RelativeLayout orientationMenu = (RelativeLayout) v.findViewById(R.id.fragment_search_orientationMenu);
        orientationMenu.setOnClickListener(this);

        this.menuIcon = (ImageView) v.findViewById(R.id.fragment_search_menuIcon);

        this.orientationTxt = (TextView) v.findViewById(R.id.fragment_search_nowTxt);
        orientationTxt.setText(ValueUtils.getOrientationName(getActivity(), searchModel.getOrientation()));

        this.contentView = (SearchContentView) v.findViewById(R.id.fragment_search_contentView);
        contentView.setActivity(getActivity());
        contentView.setOnClickListener(this);
    }

    /** <br> model. */

    private void initModel() {
        this.searchModel = new SearchObject();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                searchBarPresenter.clickNavigationIcon();
                break;

            case R.id.fragment_search_orientationContainer:
                searchBarPresenter.clickSearchBar();
                break;

            case R.id.fragment_search_orientationMenu:
                searchBarPresenter.showOrientationMenu(getActivity(), menuIcon);
                break;

            case R.id.fragment_search_contentView:
                hideKeyboard();
                break;
        }
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        searchBarPresenter.clickMenuItem(item.getItemId());
        return true;
    }

    // on editor action clickListener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        searchBarPresenter.inputSearchQuery(textView.getText().toString());
        return true;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                showKeyboard();
                break;
        }
    }

    // view.

    // search bar view.

    @Override
    public void clickNavigationIcon() {
        ((MainActivity) getActivity()).removeFragment();
    }

    @Override
    public void clearSearchText() {
        editText.setText("");
    }

    @Override
    public void changeOrientation(String orientation) {
        orientationTxt.setText(ValueUtils.getOrientationName(getActivity(), orientation));
    }

    @Override
    public void scrollToTop() {
        contentView.scrollToTop();
    }

    @Override
    public void inputSearchQuery(String query, String orientation) {
        contentView.doSearch(query, orientation);
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
}
