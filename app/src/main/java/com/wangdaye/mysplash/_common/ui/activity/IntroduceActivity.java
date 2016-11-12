package com.wangdaye.mysplash._common.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.adapter.MyPagerAdapter;
import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Introduce dialog.
 * */

public class IntroduceActivity extends MysplashActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener, SafeHandler.HandlerContainer {
    // widget
    private CoordinatorLayout container;
    private ViewPager viewPager;
    private Button button;

    private SafeHandler<IntroduceActivity> handler;

    // data
    private boolean backPressed = false;

    private List<IntroduceModel> introduceModelList;

    private static final int FIRST_VERSION = 0;
    private static final int MULTI_FILTER_NEW_SEARCH_VERSION = 1;
    private static final int NEW_VERSION = MULTI_FILTER_NEW_SEARCH_VERSION;

    private static final String PREFERENCE_NAME = "mysplash_introduce";
    private static final String KEY_INTRODUCE_VERSION = "introduce_version";

    /** <br> life cycle. */

    public static void checkAndStartIntroduce(Activity a) {
        SharedPreferences sharedPreferences = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE);
/*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();
*/
        if (sharedPreferences.getInt(KEY_INTRODUCE_VERSION, FIRST_VERSION) < NEW_VERSION) {
            IntentHelper.startIntroduceActivity(a);
        }
    }

    public static void watchAllIntroduce(Activity a) {
        SharedPreferences.Editor editor = a.getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);
        editor.apply();

        Intent intent = new Intent(a, IntroduceActivity.class);
        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_in, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
            overridePendingTransition(0, R.anim.activity_slide_out_bottom);
        } else {
            backPressed = true;
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_click_again_to_exit),
                    Snackbar.LENGTH_SHORT);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
        }
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    /** <br> UI. */

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_introduce_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        ImageButton backBtn = (ImageButton) findViewById(R.id.activity_introduce_backBtn);
        if (Mysplash.getInstance().isLightTheme()) {
            backBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            backBtn.setImageResource(R.drawable.ic_close_dark);
        }
        backBtn.setOnClickListener(this);

        this.container = (CoordinatorLayout) findViewById(R.id.activity_introduce_container);

        this.button = (Button) findViewById(R.id.activity_introduce_button);
        button.setOnClickListener(this);
        setBottomButtonStyle(0);

        initPage();

        InkPageIndicator indicator = (InkPageIndicator) findViewById(R.id.activity_introduce_indicator);
        indicator.setViewPager(viewPager);
    }

    @SuppressLint("InflateParams")
    private void initPage() {
        List<View> pageList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < introduceModelList.size(); i ++) {
            View v = LayoutInflater.from(this).inflate(R.layout.container_introduce, null);

            TextView title = (TextView) v.findViewById(R.id.container_introduce_title);
            title.setText(introduceModelList.get(i).title);

            FreedomImageView image = (FreedomImageView) v.findViewById(R.id.container_introduce_image);
            Glide.with(this)
                    .load(introduceModelList.get(i).imageRes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);

            TextView description = (TextView) v.findViewById(R.id.container_introduce_description);
            description.setText(introduceModelList.get(i).description);
            DisplayUtils.setTypeface(this, description);

            setPageButtonStyle(v, i);

            pageList.add(v);
            titleList.add(introduceModelList.get(i).title);
        }

        MyPagerAdapter adapter = new MyPagerAdapter(pageList, titleList);

        viewPager = (ViewPager) findViewById(R.id.activity_introduce_viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void setBottomButtonStyle(int page) {
        if (page == introduceModelList.size() - 1) {
            button.setText(getString(R.string.enter));
        } else {
            button.setText(getString(R.string.next));
        }
    }

    private void setPageButtonStyle(View page, int position) {
        Button b = (Button) page.findViewById(R.id.container_introduce_button);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (Mysplash.getInstance().isLightTheme()) {
                b.setBackgroundResource(R.color.colorPrimary_dark);
            } else {
                b.setBackgroundResource(R.color.colorPrimary_light);
            }
        } else {
            b.setBackgroundResource(R.drawable.button_login);
        }

        switch (introduceModelList.get(position).imageRes) {
            case R.drawable.illustration_back_top:
                b.setText(getString(R.string.set));
                b.setOnClickListener(this);
                break;

            default:
                b.setVisibility(View.GONE);
                break;
        }
    }

    /** <br> data. */

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        int versionCode = sharedPreferences.getInt(KEY_INTRODUCE_VERSION, FIRST_VERSION);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_INTRODUCE_VERSION, NEW_VERSION);
        editor.apply();

        introduceModelList = new ArrayList<>();
        switch (versionCode) {
            case FIRST_VERSION:
                introduceModelList.add(
                        new IntroduceModel(
                                R.string.introduce_title_search,
                                R.drawable.illustration_search,
                                R.string.introduce_description_search));
                introduceModelList.add(
                        new IntroduceModel(
                                R.string.introduce_title_filter,
                                R.drawable.illustration_filter,
                                R.string.introduce_description_filter));
                introduceModelList.add(
                        new IntroduceModel(
                                R.string.introduce_title_back_top,
                                R.drawable.illustration_back_top,
                                R.string.introduce_description_back_top));
                introduceModelList.add(
                        new IntroduceModel(
                                R.string.introduce_title_start,
                                R.drawable.illustration_start,
                                R.string.introduce_description_start));
                break;
        }
    }

    /** <br> inner class. */

    private class IntroduceModel {
        public String title;
        int imageRes;
        String description;

        IntroduceModel(int titleRes, int imageRes, int descriptionRes) {
            this.title = getString(titleRes);
            this.imageRes = imageRes;
            this.description = getString(descriptionRes);
        }
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_introduce_backBtn:
                finishActivity();
                break;

            case R.id.activity_introduce_button:
                if (viewPager.getCurrentItem() == introduceModelList.size() - 1) {
                    finishActivity();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
                break;

            case R.id.container_introduce_button:
                switch (introduceModelList.get(viewPager.getCurrentItem()).imageRes) {
                    case R.drawable.illustration_back_top:
                        Intent s = new Intent(this, SettingsActivity.class);
                        startActivity(s);
                        overridePendingTransition(R.anim.activity_in, 0);
                        break;
                }
                break;
        }
    }

    // on page changed listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        setBottomButtonStyle(position);
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
        switch (message.what) {
            case 1:
                backPressed = false;
                break;
        }
    }
}
